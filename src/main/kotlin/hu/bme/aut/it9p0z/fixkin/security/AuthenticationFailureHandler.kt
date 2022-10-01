package hu.bme.aut.it9p0z.fixkin.security

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpStatus
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthenticationFailureHandler : AuthenticationFailureHandler {
    private val objectMapper = Jackson2ObjectMapperBuilder.json().build<ObjectMapper>()

    override fun onAuthenticationFailure(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        exception: AuthenticationException?
    ) {
        val json = objectMapper.writeValueAsString("{\"message\":\"You're attempt to log in was unsuccessful\"}")
        response?.contentType = "application/json"
        response?.characterEncoding = "UTF-8"
        response?.status = HttpStatus.UNAUTHORIZED.value()
        response?.writer?.write(json)
    }
}