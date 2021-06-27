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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SolicitaAnaliseFinanceiraRequest) return false

        if (documento != other.documento) return false
        if (nome != other.nome) return false

        return true
    }

    override fun hashCode(): Int {
        var result = documento.hashCode()
        result = 31 * result + nome.hashCode()
        return result
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
