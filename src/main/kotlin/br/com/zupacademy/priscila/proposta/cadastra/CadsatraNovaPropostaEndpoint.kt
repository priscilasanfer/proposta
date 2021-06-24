package br.com.zupacademy.priscila.proposta.cadastra

import br.com.zupacademy.priscila.NovaPropostaRequest
import br.com.zupacademy.priscila.NovaPropostaResponse
import br.com.zupacademy.priscila.PropostaServiceGrpc
import br.com.zupacademy.priscila.compartilhado.excecao.ErrorHandler
import br.com.zupacademy.priscila.proposta.Proposta
import br.com.zupacademy.priscila.proposta.PropostaRepository
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.ConstraintViolationException
import javax.validation.Validator

@Singleton
@ErrorHandler
class CadsatraNovaPropostaEndpoint(
    @Inject private val service: CadastraNovaPropostaService
) : PropostaServiceGrpc.PropostaServiceImplBase() {

    private val logger = LoggerFactory.getLogger((this::class.java))

    override fun cadastra(
        request: NovaPropostaRequest,
        responseObserver: StreamObserver<NovaPropostaResponse>
    ) {
        val novaProposta = request.toModel()

        val propostaSalva = service.cadastra(novaProposta)

        logger.info("Proposta de id ${propostaSalva.propostaId} salva com sucesso")

        responseObserver.onNext(
            NovaPropostaResponse.newBuilder()
                .setPropostaId(propostaSalva.propostaId.toString())
                .setEmail(propostaSalva.email)
                .build()
        )
        responseObserver.onCompleted()
    }
}

