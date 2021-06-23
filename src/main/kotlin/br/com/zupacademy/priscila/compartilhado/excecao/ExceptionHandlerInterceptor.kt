package br.com.zupacademy.priscila.compartilhado.excecao

import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.stub.StreamObserver
import io.micronaut.aop.InterceptorBean
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import org.slf4j.LoggerFactory
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
@InterceptorBean(ErrorHandler::class)
class ExceptionHandlerInterceptor : MethodInterceptor<Any, Any> {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun intercept(context: MethodInvocationContext<Any, Any>): Any? {

        return try {
            context.proceed()
        } catch (e: Exception) {
            logger.error(
                "Handling the exception '${e.javaClass.name}' while processing the call: ${context.targetMethod}",
                e
            )

            val responseObserver = context.parameterValues[1] as StreamObserver<*>

            val status = when (e) {
                is IllegalArgumentException -> Status.INVALID_ARGUMENT.withDescription(e.message)
                is IllegalStateException -> Status.FAILED_PRECONDITION.withDescription(e.message)
                is ConstraintViolationException -> Status.INVALID_ARGUMENT.withCause(e).withDescription(e.message)

                else -> Status.UNKNOWN.withCause(e).withDescription("Um erro inesperado aconteceu")
            }

            val statusRuntimeException = StatusRuntimeException(status)

            responseObserver.onError(statusRuntimeException)

        }
    }
}
