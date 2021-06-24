package br.com.zupacademy.priscila.proposta

import java.math.BigDecimal
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
    val salario: BigDecimal
) {
    @Id
    @GeneratedValue
    val id: Long? = null

    val propostaId: UUID = UUID.randomUUID()
}