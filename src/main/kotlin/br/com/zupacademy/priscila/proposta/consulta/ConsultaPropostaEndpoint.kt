package br.com.zupacademy.priscila.proposta.consulta

import br.com.zupacademy.priscila.ConsultaRequest
import br.com.zupacademy.priscila.ConsultaResponse
import br.com.zupacademy.priscila.PropostaServiceGrpc
import br.com.zupacademy.priscila.compartilhado.excecao.ErrorHandler
import br.com.zupacademy.priscila.compartilhado.excecao.PropostaNaoEncontradaException
import br.com.zupacademy.priscila.proposta.PropostaRepository
import br.com.zupacademy.priscila.proposta.Status
import io.grpc.stub.StreamObserver
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ErrorHandler
class ConsultaPropostaEndpoint(
    @Inject val repository: PropostaRepository
) : PropostaServiceGrpc.PropostaServiceImplBase() {

    override fun consulta(
        request: ConsultaRequest,
        responseObserver: StreamObserver<ConsultaResponse>
    ) {

        if (request.propostaId.isNullOrBlank()) {
            throw IllegalArgumentException("Número da proposta não pode ser nulo ou vazio")
        }

        val propostaId = UUID.fromString(request.propostaId)

        val propostaRetornada = repository.findByPropostaId(propostaId)

        if (propostaRetornada.isEmpty) {
            throw PropostaNaoEncontradaException("A Proposta informada não foi encontrada")
        }

        val proposta = propostaRetornada.get()

        //TODO Ver como buildar o response quando a proposta nao for elegive
        val response = ConsultaResponse.newBuilder()
            .setEmail(proposta.email)
            .setNome(proposta.nome)
            .setPropostaId(proposta.propostaId.toString())
            .setCartao(
                ConsultaResponse.Cartao.newBuilder()
                    .setNumero(proposta.cartao!!.numero)
                    .setLimite(proposta.cartao!!.limite.toString())
                    .build()
            )
            .build()

        responseObserver.onNext(response)
        responseObserver.onCompleted()

    }
}