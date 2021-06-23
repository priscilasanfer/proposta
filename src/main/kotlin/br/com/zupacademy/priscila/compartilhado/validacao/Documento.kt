package br.com.zupacademy.priscila.compartilhado.validacao

import org.hibernate.validator.constraints.CompositionType
import org.hibernate.validator.constraints.ConstraintComposition
import org.hibernate.validator.constraints.br.CNPJ
import org.hibernate.validator.constraints.br.CPF
import java.lang.annotation.Documented
import javax.validation.Constraint
import javax.validation.Payload
import javax.validation.ReportAsSingleViolation
import kotlin.annotation.AnnotationRetention.*
import kotlin.annotation.AnnotationTarget.*
import kotlin.reflect.KClass

@Documented
@Target(FIELD)
@ReportAsSingleViolation
@Constraint(validatedBy = [])
@CPF
@CNPJ
@ConstraintComposition(CompositionType.OR)
@Retention(RUNTIME)
annotation class Documento(
    val message: String = "Documento invalido (\${validatedValue})",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = []
)
