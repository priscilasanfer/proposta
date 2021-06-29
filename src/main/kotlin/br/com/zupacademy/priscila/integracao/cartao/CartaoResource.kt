package br.com.zupacademy.priscila.integracao.cartao

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client
import java.math.BigDecimal
import java.time.LocalDateTime

@Client(value = "\${cartao.url}")
interface CartaoResource {

    @Get
    fun solicitaCartao(@QueryValue idProposta: String): HttpResponse<SolicitaCartaoResponse>
}

data class SolicitaCartaoResponse(
    val id: String,
    val emitidoEm: LocalDateTime,
    val titular: String,
    val bloqueios: Array<Bloqueios>,
    val avisos: Array<Avisos>,
    val carteiras: Array<Carteiras>,
    val parcelas: Array<Parcelas>,
    val limite: BigDecimal,
    val renegociacao: Array<Renegociacao>?,
    val vencimento: Vencimento,
    val idProposta: String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SolicitaCartaoResponse) return false

        if (id != other.id) return false
        if (emitidoEm != other.emitidoEm) return false
        if (titular != other.titular) return false
        if (!bloqueios.contentEquals(other.bloqueios)) return false
        if (!avisos.contentEquals(other.avisos)) return false
        if (!carteiras.contentEquals(other.carteiras)) return false
        if (!parcelas.contentEquals(other.parcelas)) return false
        if (limite != other.limite) return false
        if (!renegociacao.contentEquals(other.renegociacao)) return false
        if (vencimento != other.vencimento) return false
        if (idProposta != other.idProposta) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + emitidoEm.hashCode()
        result = 31 * result + titular.hashCode()
        result = 31 * result + bloqueios.contentHashCode()
        result = 31 * result + avisos.contentHashCode()
        result = 31 * result + carteiras.contentHashCode()
        result = 31 * result + parcelas.contentHashCode()
        result = 31 * result + limite.hashCode()
        result = 31 * result + renegociacao.contentHashCode()
        result = 31 * result + vencimento.hashCode()
        result = 31 * result + idProposta.hashCode()
        return result
    }

}

data class Bloqueios(
    val id: String,
    val bloqueadoEm: LocalDateTime,
    val sistemaResponsavel: String,
    val ativo: Boolean
)

data class Avisos(
    val validoAte: LocalDateTime,
    val destino: String
)

data class Carteiras(
    val id: String,
    val email: String,
    val associadaEm: LocalDateTime,
    val emissor: String
)

data class Parcelas(
    val id: String,
    val quantidade: Int,
    val valor: BigDecimal
)

data class Renegociacao(
    val id: String,
    val quantidade: Int,
    val valor: BigDecimal,
    val dataDeCriacao: LocalDateTime
)

data class Vencimento(
    val id: String,
    val dia: Int,
    val dataDeCriacao: LocalDateTime
)
