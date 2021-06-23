package br.com.zupacademy.priscila.compartilhado.excecao

import io.micronaut.aop.Around
import io.micronaut.context.annotation.Type
import kotlin.annotation.AnnotationRetention.*
import kotlin.annotation.AnnotationTarget.*

@MustBeDocumented
@Target(CLASS, TYPE, FUNCTION)
@Retention(RUNTIME)
@Type(ExceptionHandlerInterceptor::class)
@Around
annotation class ErrorHandler
