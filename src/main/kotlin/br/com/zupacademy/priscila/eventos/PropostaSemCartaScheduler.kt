package br.com.zupacademy.priscila.eventos

import br.com.zupacademy.priscila.proposta.Proposta
import br.com.zupacademy.priscila.proposta.PropostaRepository
import br.com.zupacademy.priscila.proposta.Status
import io.micronaut.context.event.ApplicationEventPublisher
import io.micronaut.scheduling.annotation.Scheduled
import io.micronaut.transaction.annotation.TransactionalAdvice
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional

@Singleton
class PropostaSemCartaScheduler(
    @Inject val repository: PropostaRepository,
    @Inject val eventPublisher: ApplicationEventPublisher
) {

    private val logger = LoggerFactory.getLogger((this::class.java))

    @Scheduled(fixedDelay = "5m")
    fun verificaPropostaElegivelSemCartao() {
        logger.info("Job: Verificando se existe propostas elegiveis sem cartão")

        val propostas = repository.findByStatusAndCartaoIsNull(Status.ELEGIVEL)

        propostas.forEach { proposta ->
            logger.info("${proposta.propostaId} encontrada")
            eventPublisher.publishEvent(PropostaSemCartaoEvent(proposta))
        }
    }
}

data class PropostaSemCartaoEvent(val proposta: Proposta)