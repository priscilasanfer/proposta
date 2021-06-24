package br.com.zupacademy.priscila.compartilhado.validacao

import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.transaction.annotation.TransactionalAdvice
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.inject.Inject
import javax.inject.Singleton
import javax.persistence.EntityManager
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FIELD
import kotlin.annotation.AnnotationTarget.TYPE_PARAMETER
import kotlin.reflect.KClass

@Target(FIELD, TYPE_PARAMETER)
@Retention(RUNTIME)
@Constraint(validatedBy = [ValorUnicoValidator::class])
annotation class ValorUnico(

    val targetClass: KClass<*>,
    val field: String,
    val message: String = "O dado (\${validatedValue}) já está em uso.",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = []
  )

@Singleton
@TransactionalAdvice
class ValorUnicoValidator(@Inject val manager: EntityManager) : ConstraintValidator<ValorUnico, Any> {

    lateinit var field: String
    lateinit var targetClass: KClass<*>

    override fun initialize(constraintAnnotation: ValorUnico) {
        field = constraintAnnotation.field
        targetClass = constraintAnnotation.targetClass
    }

    override fun isValid(
        value: Any?,
        annotationMetadata: AnnotationValue<ValorUnico>,
        context: ConstraintValidatorContext
    ): Boolean {

        if(value == null) return true

        return manager
            .createQuery("select 1 from ${targetClass.simpleName} where $field =:valor")
            .setParameter("valor", value)
            .resultList
            .isEmpty()
    }
}
