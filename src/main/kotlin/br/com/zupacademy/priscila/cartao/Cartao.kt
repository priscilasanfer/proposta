package br.com.zupacademy.priscila.cartao

import br.com.zupacademy.priscila.proposta.Proposta
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Cartao(
    val numero: String,

    @OneToOne(mappedBy = "cartao", cascade = [CascadeType.MERGE])
    val proposta: Proposta,

    val emitidoEm: LocalDateTime,

    val limite: BigDecimal
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @field:Enumerated(EnumType.STRING)
    var status: StatusCartao = StatusCartao.ATIVO

}