package br.com.zupacademy.priscila.proposta.cadastra

import br.com.zupacademy.priscila.compartilhado.validacao.Documento
import br.com.zupacademy.priscila.compartilhado.validacao.ValorUnico
import br.com.zupacademy.priscila.proposta.Proposta
import io.micronaut.core.annotation.Introspected
import java.math.BigDecimal
import javax.validation.constraints.*

@Introspected
class NovaProposta(

    @field:NotBlank
    @field:Documento
    @field:ValorUnico(targetClass = Proposta::class, field = "documento")
    val documento: String,

    @field:NotBlank
    @field:Email
    val email: String,

    @field:NotBlank
    val nome: String,

    @field:NotBlank
    val endereco: String,

    @field:Positive
    val salario: BigDecimal
) {
    fun toModel(): Proposta {
        return Proposta(
            documento = this.documento,
            email = this.email,
            nome = this.nome,
            endereco = this.endereco,
            salario = this.salario
        )
    }
}
