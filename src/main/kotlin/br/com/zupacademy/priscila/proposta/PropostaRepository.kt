package br.com.zupacademy.priscila.proposta

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface PropostaRepository : JpaRepository<Proposta, Long> {
    fun existsByPropostaId(propostaId: UUID): Boolean
}