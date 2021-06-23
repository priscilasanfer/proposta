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
    val validator: Validator,
    @Inject val repository: PropostaRepository
) : PropostaServiceGrpc.PropostaServiceImplBase() {

    private val logger = LoggerFactory.getLogger((this::class.java))

    override fun cadastra(
        request: NovaPropostaRequest,
        responseObserver: StreamObserver<NovaPropostaResponse>
    ) {
        val novaProposta = request.toModel(validator)
        val propostaSalva = repository.save(novaProposta)

        logger.info("Proposta de id ${novaProposta.propostaId} salva com sucesso")

        responseObserver.onNext(
            NovaPropostaResponse.newBuilder()
                .setPropostaId(propostaSalva.propostaId.toString())
                .setEmail(propostaSalva.email)
                .build()
        )
        responseObserver.onCompleted()
    }
}

fun NovaPropostaRequest.toModel(validator: Validator): Proposta {
    val novaProposta = Proposta(
        documento = this.documento,
        email = this.email,
        nome = this.nome,
        endereco = this.endereco,
        salario = BigDecimal(this.salario)
    )

    val erros = validator.validate(novaProposta)

    if (erros.isNotEmpty()) {
        throw ConstraintViolationException(erros)
    }

    return novaProposta
}