package br.com.zupacademy.priscila.proposta.cadastra

import br.com.zupacademy.priscila.NovaPropostaRequest
import br.com.zupacademy.priscila.PropostaServiceGrpc
import br.com.zupacademy.priscila.proposta.PropostaRepository
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.util.*
import javax.inject.Singleton
import org.junit.jupiter.api.assertThrows

@MicronautTest(transactional = false)
internal class CadsatraNovaPropostaEndpointTest(
    val repository: PropostaRepository,
    val grpcClient: PropostaServiceGrpc.PropostaServiceBlockingStub
) {

    @BeforeEach
    internal fun setUp() {
        repository.deleteAll()
    }

    @Test
    internal fun `deve cadastrar uma nova proposta`() {
        val request = NovaPropostaRequest.newBuilder()
            .setDocumento("512.642.090-96")
            .setNome("Nasxiga Naumere")
            .setEmail("nasxiga.naumere@teste.com")
            .setEndereco("Rua Um 17")
            .setSalario("2500.00")
            .build()

        val response = grpcClient.cadastra(request)

        with(response) {
            assertNotNull(propostaId)
            assertEquals(email, request.email)
            assertTrue(repository.existsByPropostaId(UUID.fromString(propostaId)))
        }
    }

    @ParameterizedTest
    @CsvSource(
        "123.456.789-10, Nasxiga Naumere, nasxiga.naumere@teste.com, Rua Um 17, 2500.00, documento: Documento invalido (123.456.789-10)",
        "512.642.090-96, '' , nasxiga.naumere@teste.com, Rua Um 17, 2500.00, nome: não deve estar em branco",
        "512.642.090-96, Nasxiga Naumere , '', Rua Um 17, 2500.00, email: não deve estar em branco",
        "512.642.090-96, Nasxiga Naumere , nasxiga.naumere@teste.com, '', 2500.00, endereco: não deve estar em branco",
        "512.642.090-96, Nasxiga Naumere , nasxiga.naumere@teste.com, Rua Um 17, 0, salario: deve ser maior que 0, endereco: não deve estar em branco"
    )
    internal fun `nao deve cadastrar proposta quando algum dado de entrada estiver invalido`(
        documento: String,
        nome: String,
        email: String,
        endereco: String,
        salario: String,
        mensagem: String
    ) {

        val request = NovaPropostaRequest.newBuilder()
            .setDocumento(documento)
            .setNome(nome)
            .setEmail(email)
            .setEndereco(endereco)
            .setSalario(salario)
            .build()

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.cadastra(request)
        }

        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals(mensagem, status.description)
        }
    }

    @Factory
    class Cadastra {

        @Singleton
        fun blockingStub(
            @GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel
        ): PropostaServiceGrpc.PropostaServiceBlockingStub {
            return PropostaServiceGrpc.newBlockingStub(channel)
        }
    }
}