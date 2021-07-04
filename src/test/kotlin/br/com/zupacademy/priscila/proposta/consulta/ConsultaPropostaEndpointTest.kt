package br.com.zupacademy.priscila.proposta.consulta

import br.com.zupacademy.priscila.ConsultaPropostaServiceGrpc
import br.com.zupacademy.priscila.ConsultaRequest
import br.com.zupacademy.priscila.cartao.Cartao
import br.com.zupacademy.priscila.proposta.Proposta
import br.com.zupacademy.priscila.proposta.PropostaRepository
import br.com.zupacademy.priscila.proposta.Status
import io.grpc.ManagedChannel
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@MicronautTest(transactional = false)
internal class ConsultaPropostaEndpointTest(
    val repository: PropostaRepository,
    val grpcClient: ConsultaPropostaServiceGrpc.ConsultaPropostaServiceBlockingStub
) {

    @BeforeEach
    internal fun setUp() {

    }

    @AfterEach
    internal fun cleanUp() {
        repository.deleteAll()
    }

    @Test
    internal fun `deve retornar proposta elegivel existente com cartao associado `() {
        val propostaSalvaSemCartao = repository.save(proposta(Status.ELEGIVEL))
        propostaSalvaSemCartao.cartao = cartao(propostaSalvaSemCartao)
        val propostaSalvaComCartao = repository.update(propostaSalvaSemCartao)

        val response = grpcClient.consulta(
            ConsultaRequest.newBuilder()
                .setPropostaId(propostaSalvaComCartao.propostaId.toString())
                .build()
        )

        with(response) {
            assertNotNull(propostaId)
            assertEquals(email, propostaSalvaComCartao.email)
            assertTrue(repository.existsByPropostaId(UUID.fromString(propostaId)))
            assertEquals(
                Status.ELEGIVEL,
                repository.findByPropostaId(propostaId = UUID.fromString(propostaId)).get().status
            )
            assertNotNull(cartao)
            assertEquals(cartao.numero, propostaSalvaComCartao.cartao!!.numero)
        }

    }

    @Test
    internal fun `deve retornar proposta nao elegivel existente sem cartao`() {
        val propostaSalva = repository.save(proposta(Status.NAO_ELEGIVEL))

        val response = grpcClient.consulta(
            ConsultaRequest.newBuilder()
                .setPropostaId(propostaSalva.propostaId.toString())
                .build()
        )

        with(response) {
            assertNotNull(propostaId)
            assertEquals(email, propostaSalva.email)
            assertTrue(repository.existsByPropostaId(UUID.fromString(propostaId)))
            assertEquals(
                Status.NAO_ELEGIVEL,
                repository.findByPropostaId(propostaId = UUID.fromString(propostaId)).get().status
            )
            // assertNull(cartao)  TODO Validar que o cartão vem nulo
        }

    }

    @Test
    internal fun `deve retornar IllegalArgumentException quando proposta a for nula ou em branco`() {

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.consulta(
                ConsultaRequest.newBuilder()
                    .setPropostaId("")
                    .build()
            )
        }

        with(error) {
            assertEquals(io.grpc.Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Número da proposta não pode ser nulo ou vazio", status.description)
        }
    }


    @Test
    internal fun `deve retornar PropostaNaoEncontradaException quando a proposta nao existir`() {

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.consulta(
                ConsultaRequest.newBuilder()
                    .setPropostaId("9e2fd246-dccc-11eb-ba80-0242ac130004")
                    .build()
            )
        }

        with(error) {
            assertEquals(io.grpc.Status.NOT_FOUND.code, status.code)
            assertEquals("A Proposta informada não foi encontrada", status.description)
        }
    }

    @Factory
    class Clients {
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): ConsultaPropostaServiceGrpc.ConsultaPropostaServiceBlockingStub? {
            return ConsultaPropostaServiceGrpc.newBlockingStub(channel)
        }
    }

    private fun proposta(status: Status): Proposta {
        return Proposta(
            documento = "123.456.789-10",
            nome = "Nasxiga Naumere",
            email = "nasxiga.naumere@teste.com",
            endereco = "Rua Um 17",
            salario = BigDecimal("2500.00"),
            status = status
        )

    }


    private fun cartao(proposta: Proposta): Cartao {
        return Cartao(
            numero = "5286 3770 0711 0170",
            proposta = proposta,
            emitidoEm = LocalDateTime.now(),
            limite = BigDecimal("6789")
        )
    }

}