package com.fetocan.feedbutton.service.exception

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.http.HttpStatus
import org.springframework.validation.BindException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.view.json.MappingJackson2JsonView
import java.time.Instant
import javax.servlet.http.HttpServletRequest
import javax.validation.ValidationException


@ControllerAdvice
class DefaultExceptionHandler(
    private val objectMapper: ObjectMapper
) {

    @ExceptionHandler(*[Exception::class])
    @Throws(Exception::class)
    fun defaultExceptionHandler(
        request: HttpServletRequest,
        exception: Exception
    ): ModelAndView {
        val httpStatus = resolveResponseStatus(exception)
        //This will support both BindException(for get method without @RequestBody) & MethodArgumentNotValidException which is subclass of BindException.
        val ex = if (exception is BindException)
            buildPrettyMessageForFieldValidation(exception)
        else exception

        val attributeValue = ErrorModel(
            timestamp = Instant.now().toString(),
            code = if (ex is BaseRestException) ex.code else "error.internal.server",
            status = httpStatus.value(),
            path = request.servletPath,
            error = ex.javaClass.simpleName,
            message = ex.message
        )
        val view = MappingJackson2JsonView(objectMapper)
        view.setExtractValueFromSingleKeyModel(true)
        val modelAndView = ModelAndView(view)
        modelAndView.addObject(
            attributeValue
        )
        modelAndView.status = httpStatus
        return modelAndView
    }

    @Throws(Exception::class)
    private fun resolveResponseStatus(
        exception: Exception
    ): HttpStatus {
        val mergedAnnotation: ResponseStatus? = AnnotatedElementUtils.findMergedAnnotation(
            exception.javaClass,
            ResponseStatus::class.java
        )
        return mergedAnnotation?.code
            ?: getCustomResponseStatus(exception)
    }

    private fun getCustomResponseStatus(
        exception: Exception
    ): HttpStatus {
        //Use this for existing exception without annotation @ResponseStatus
        return when (exception) {
            is HttpClientErrorException.NotFound -> HttpStatus.NOT_FOUND
            is MethodArgumentNotValidException -> HttpStatus.BAD_REQUEST
            else -> HttpStatus.INTERNAL_SERVER_ERROR
        }
    }

    private fun buildPrettyMessageForFieldValidation(
        ex: BindException
    ): ValidationException {
        val errors: MutableMap<String, String?> = HashMap()
        ex.bindingResult.allErrors.forEach {
            val fieldName = (it as FieldError).field
            val errorMessage = it.defaultMessage
            errors[fieldName] = errorMessage
        }
        return ValidationException(ObjectMapper().writeValueAsString(errors))
    }

}

data class ErrorModel(
    val timestamp: String,
    val code: String,
    val status: Int? = null,
    val path: String? = null,
    val error: String? = null,
    val message: String? = null
)
