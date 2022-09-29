package com.interview.tui.githubapi.error

import com.fasterxml.jackson.databind.ObjectMapper
import com.interview.tui.githubapi.error.dto.ApplicationErrorResponse
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest

@Component
class GlobalErrorAttributes(
    private val mapper: ObjectMapper
) : DefaultErrorAttributes() {

    companion object {
        const val STATUS_KEY = "status"
        const val MESSAGE_KEY = "message"
    }

    val mapType = mapper.typeFactory.constructMapType(HashMap::class.java, String::class.java, Any::class.java)

    override fun getErrorAttributes(request: ServerRequest?, options: ErrorAttributeOptions?): MutableMap<String, Any> {
        val updOpts = options?.including(ErrorAttributeOptions.Include.MESSAGE)
        val errorAttributes = super.getErrorAttributes(request, updOpts)
        if (errorAttributes.containsKey(STATUS_KEY)) {
            val status = errorAttributes[STATUS_KEY] as Int
            return mapper.convertValue(
                ApplicationErrorResponse(
                    status = status,
                    message = (errorAttributes[MESSAGE_KEY] ?: HttpStatus.valueOf(status).toString()) as String
                ),
                mapType
            )
        }
        return errorAttributes;
    }
}