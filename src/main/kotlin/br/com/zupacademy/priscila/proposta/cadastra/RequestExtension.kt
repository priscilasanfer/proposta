package br.com.zupacademy.priscila.proposta.cadastra

import br.com.zupacademy.priscila.NovaPropostaRequest
import java.math.BigDecimal

fun NovaPropostaRequest.toModel(): NovaProposta {
    return NovaProposta(
        documento = this.documento,
        email = this.email,
        nome = this.nome,
        endereco = this.endereco,
        salario = BigDecimal(this.salario)
    )
}