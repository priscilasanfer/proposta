package br.com.zupacademy.priscila.integracao.analise

import br.com.zupacademy.priscila.proposta.Proposta
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client

@Client(value = "\${analise.url}")
interface AnaliseFinanceira {

    @Post
    fun solicitaAnaliseFinanceira(@Body request: SolicitaAnaliseFinanceiraRequest): HttpResponse<SolicitaAnaliseFinanceiraResponse>
}

data class SolicitaAnaliseFinanceiraRequest(
    val documento: String,
    val nome: String,
    val idProposta: String
){
    companion object{
        fun of(proposta: Proposta): SolicitaAnaliseFinanceiraRequest{
            return SolicitaAnaliseFinanceiraRequest (
                documento = proposta.documento,
                nome = proposta.nome,
                idProposta = proposta.propostaId.toString()
            )
        }
    }
}

data class SolicitaAnaliseFinanceiraResponse(
    val documento: String,
    val nome: String,
    val idProposta: String,
    val resultadoSolicitacao: StatusAnaliseFinanceira
)

enum class StatusAnaliseFinanceira{
    COM_RESTRICAO,
    SEM_RESTRICAO
}
