package br.com.zupacademy.priscila.eventos

import br.com.zupacademy.priscila.cartao.Cartao
import br.com.zupacademy.priscila.integracao.cartao.CartaoResource
import br.com.zupacademy.priscila.proposta.PropostaRepository
import io.micronaut.runtime.event.annotation.EventListener
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PropostaSemCartaoListener(
    @Inject val cartaoResource: CartaoResource,
    @Inject val repository: PropostaRepository
) {
    private val logger = LoggerFactory.getLogger((this::class.java))

    @EventListener
    internal fun novoCartaoParaPropostaElegivel(event: PropostaSemCartaoEvent) {
        val proposta = event.proposta

        try {
            val solicitaCartao = cartaoResource.solicitaCartao(proposta.propostaId.toString())

            val cartao = Cartao(
                numero = solicitaCartao.body()!!.id,
                proposta = proposta,
                emitidoEm = solicitaCartao.body()!!.emitidoEm,
                limite = solicitaCartao.body()!!.limite
            )

            proposta.cartao = cartao

            repository.update(proposta)

            logger.info("Proposta ${proposta.propostaId} atualizada")

        } catch (e: Exception) {
            logger.error("Um erro inexperado aconteceu, causa: ${e.cause} e mensagem: ${e.message}");
        }
    }
}