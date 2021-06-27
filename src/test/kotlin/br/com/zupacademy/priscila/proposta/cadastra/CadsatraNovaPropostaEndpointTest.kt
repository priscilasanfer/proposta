package br.com.zupacademy.priscila.proposta.cadastra

import br.com.zupacademy.priscila.NovaPropostaRequest
import br.com.zupacademy.priscila.PropostaServiceGrpc
import br.com.zupacademy.priscila.integracao.analise.AnaliseFinanceira
import br.com.zupacademy.priscila.integracao.analise.SolicitaAnaliseFinanceiraRequest
import br.com.zupacademy.priscila.integracao.analise.SolicitaAnaliseFinanceiraResponse
import br.com.zupacademy.priscila.integracao.analise.StatusAnaliseFinanceira
import br.com.zupacademy.priscila.proposta.Proposta
import br.com.zupacademy.priscila.proposta.PropostaRepository
import br.com.zupacademy.priscila.proposta.Status.ELEGIVEL
import br.com.zupacademy.priscila.proposta.Status.NAO_ELEGIVEL
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class CadsatraNovaPropostaEndpointTest(
    val repository: PropostaRepository,
    val grpcClient: PropostaServiceGrpc.PropostaServiceBlockingStub
) {

    @BeforeEach
    internal fun setUp() {
        repository.deleteAll()
    }

    @field:Inject
    lateinit var analiseFinanceiraClient: AnaliseFinanceira

    @Test
    internal fun `deve cadastrar uma nova proposta com status elegivel`() {

        val request = NovaPropostaRequest.newBuilder()
            .setDocumento("512.642.090-96")
            .setNome("Nasxiga Naumere")
            .setEmail("nasxiga.naumere@teste.com")
            .setEndereco("Rua Um 17")
            .setSalario("2500.00")
            .build()

        `when`(
            analiseFinanceiraClient.solicitaAnaliseFinanceira(
                SolicitaAnaliseFinanceiraRequest(
                    documento = request.documento,
                    nome = request.nome,
                    idProposta = anyString()
                )
            )
        ).thenReturn(
            HttpResponse.created(
                SolicitaAnaliseFinanceiraResponse(
                    documento = request.documento,
                    nome = request.nome,
                    idProposta = UUID.randomUUID().toString(),
                    resultadoSolicitacao = StatusAnaliseFinanceira.SEM_RESTRICAO
                )
            )
        )

        val response = grpcClient.cadastra(request)

        with(response) {
            assertNotNull(propostaId)
            assertEquals(email, request.email)
            assertTrue(repository.existsByPropostaId(UUID.fromString(propostaId)))
            assertEquals(
                ELEGIVEL,
                repository.findByPropostaId(propostaId = UUID.fromString(propostaId)).get().status
            )
        }
    }

    @ParameterizedTest
    @CsvSource(
        "123.456.789-10, Nasxiga Naumere, nasxiga.naumere@teste.com, Rua Um 17, 2500.00, cadastra.novaProposta.documento: Documento invalido (123.456.789-10)",
        "512.642.090-96, '' , nasxiga.naumere@teste.com, Rua Um 17, 2500.00, cadastra.novaProposta.nome: não deve estar em branco",
        "512.642.090-96, Nasxiga Naumere , '', Rua Um 17, 2500.00, cadastra.novaProposta.email: não deve estar em branco",
        "512.642.090-96, Nasxiga Naumere , nasxiga.naumere@teste.com, '', 2500.00, cadastra.novaProposta.endereco: não deve estar em branco",
        "512.642.090-96, Nasxiga Naumere , nasxiga.naumere@teste.com, Rua Um 17, 0, cadastra.novaProposta.salario: deve ser maior que 0, endereco: não deve estar em branco",
        "512.642.090-96, Nasxiga Naumere , nasxiga.naumere@teste.com, Rua Um 17, '', Salário não pode ser nulo"
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

    @Test
    internal fun `nao deve cadastrar proposta quando ja houver proposta com o mesmo documento`() {
        val existente = repository.save(
            Proposta(
                documento = "512.642.090-96",
                email = "nasxiga.naumere@teste.com",
                nome = "Nasxiga Naumere",
                salario = BigDecimal("2500.00"),
                endereco = "Rua Um 17"
            )
        )

        val request = NovaPropostaRequest.newBuilder()
            .setDocumento("512.642.090-96")
            .setNome("Nasxiga Naumere")
            .setEmail("nasxiga.naumere@teste.com")
            .setEndereco("Rua Um 17")
            .setSalario("2500.00")
            .build()

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.cadastra(request)
        }

        with(error) {
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertEquals("Proposta já existe com o documento '${request.documento}' informado", status.description)
        }
    }

    @Test
    internal fun `deve cadastrar uma nova proposta com status nao elegivel`() {

        val id= UUID.randomUUID()
        val request = NovaPropostaRequest.newBuilder()
            .setDocumento("383.589.320-30")
            .setNome("Nasxiga Naumere")
            .setEmail("nasxiga.naumere@teste.com")
            .setEndereco("Rua Um 17")
            .setSalario("2500.00")
            .build()

        `when`(
            analiseFinanceiraClient.solicitaAnaliseFinanceira(
                SolicitaAnaliseFinanceiraRequest(
                    documento = request.documento,
                    nome = request.nome,
                    idProposta = id.toString()
                )
            )
        ).thenThrow(HttpClientResponseException::class.java)

        val response = grpcClient.cadastra(request)

        with(response) {
            assertNotNull(propostaId)
            assertEquals(email, request.email)
            assertTrue(repository.existsByPropostaId(UUID.fromString(propostaId)))
            assertEquals(
                NAO_ELEGIVEL,
                repository.findByPropostaId(propostaId = UUID.fromString(propostaId)).get().status
            )
        }
    }

    @MockBean(AnaliseFinanceira::class)
    fun analiseFinanceiraMock(): AnaliseFinanceira {
        return mock(AnaliseFinanceira::class.java)
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