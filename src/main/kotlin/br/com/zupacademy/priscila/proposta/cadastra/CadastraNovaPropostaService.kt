package br.com.zupacademy.priscila.proposta.cadastra

import br.com.zupacademy.priscila.compartilhado.excecao.DocumentoJaAssociadoAPropostaException
import br.com.zupacademy.priscila.integracao.analise.AnaliseFinanceira
import br.com.zupacademy.priscila.integracao.analise.SolicitaAnaliseFinanceiraRequest
import br.com.zupacademy.priscila.proposta.Proposta
import br.com.zupacademy.priscila.proposta.PropostaRepository
import br.com.zupacademy.priscila.proposta.Status
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class CadastraNovaPropostaService(
    @Inject val repository: PropostaRepository,
    @Inject val consultaFinanceira: AnaliseFinanceira
) {
    private val logger = LoggerFactory.getLogger((this::class.java))

    @Transactional
    fun cadastra(@Valid novaProposta: NovaProposta): Proposta {
        if (repository.existsByDocumento(novaProposta.documento))
            throw DocumentoJaAssociadoAPropostaException("Proposta já existe com o documento '${novaProposta.documento}' informado")

        val proposta = novaProposta.toModel()
        repository.save(proposta)

        consultafinceira(proposta)

        repository.update(proposta)

        return proposta
    }

    private fun consultafinceira(proposta: Proposta) {
        var status: Status
        try {
            consultaFinanceira.solicitaAnaliseFinanceira(
                SolicitaAnaliseFinanceiraRequest.of(proposta)
            )
            status = Status.ELEGIVEL

        } catch (e: Exception) {
            status = Status.NAO_ELEGIVEL
        }

        proposta.atualizaStatus(status)
    }
}
