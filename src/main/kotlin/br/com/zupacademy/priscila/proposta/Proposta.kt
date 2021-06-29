package br.com.zupacademy.priscila.proposta

import br.com.zupacademy.priscila.cartao.Cartao
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(
    uniqueConstraints = [UniqueConstraint(
        name = "uk_chave_proposta",
        columnNames = ["id"]
    )]
)
class Proposta(

    @Column(unique = true, nullable = false)
    val documento: String,

    @Column(nullable = false)
    val email: String,

    @Column(nullable = false)
    val nome: String,

    @Column(nullable = false)
    val endereco: String,

    @Column(nullable = false)
    val salario: BigDecimal,

    @field:Enumerated(EnumType.STRING)
    var status: Status = Status.NAO_ANALISADO
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    val propostaId: UUID = UUID.randomUUID()

    val criadaEm = LocalDateTime.now()

    @OneToOne(cascade = [CascadeType.MERGE])
    var cartao: Cartao? = null

    fun atualizaStatus(status: Status) {
        this.status = status
    }
}