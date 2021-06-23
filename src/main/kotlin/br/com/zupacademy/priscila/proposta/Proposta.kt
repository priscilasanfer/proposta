package br.com.zupacademy.priscila.proposta

import br.com.zupacademy.priscila.compartilhado.validacao.Documento
import br.com.zupacademy.priscila.compartilhado.validacao.ValorUnico
import java.math.BigDecimal
import java.util.*
import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Positive

@Entity
@Table(
    uniqueConstraints = [UniqueConstraint(
        name = "uk_chave_proposta",
        columnNames = ["id"]
    )]
)
class Proposta(

    @field:NotBlank
    @Column(nullable = false)
    @Documento
    @field:ValorUnico(targetClass = Proposta::class , field = "documento")
    val documento: String,

    @field:NotBlank
    @field:Email
    @field:Column(nullable = false)
    val email: String,

    @field:NotBlank
    @Column(nullable = false)
    val nome: String,

    @field:NotBlank
    @Column(nullable = false)
    val endereco: String,

    @field:Positive
    @Column(nullable = false)
    val salario: BigDecimal
) {
    @Id
    @GeneratedValue
    val id: Long? = null

    val propostaId: UUID = UUID.randomUUID()
}