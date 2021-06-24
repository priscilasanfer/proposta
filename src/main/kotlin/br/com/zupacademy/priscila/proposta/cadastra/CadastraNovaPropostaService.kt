package br.com.zupacademy.priscila.proposta.cadastra

import br.com.zupacademy.priscila.compartilhado.excecao.DocumentoJaAssociadoAPropostaException
import br.com.zupacademy.priscila.proposta.Proposta
import br.com.zupacademy.priscila.proposta.PropostaRepository
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class CadastraNovaPropostaService(
    @Inject val repository: PropostaRepository
) {
    private val logger = LoggerFactory.getLogger((this::class.java))

    @Transactional
    fun cadastra(@Valid novaProposta: NovaProposta): Proposta {
        if(repository.existsByDocumento(novaProposta.documento))
            throw DocumentoJaAssociadoAPropostaException("Prosta já existe com o documento '${novaProposta.documento}' informado")

        val proposta = novaProposta.toModel()
        repository.save(proposta)

        return proposta
    }
}