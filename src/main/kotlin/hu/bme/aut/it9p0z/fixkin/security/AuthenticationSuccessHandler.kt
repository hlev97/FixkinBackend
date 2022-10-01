package hu.bme.aut.it9p0z.fixkin.security

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpStatus
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthenticationSuccessHandler : AuthenticationSuccessHandler {
    private val objectMapper = Jackson2ObjectMapperBuilder.json().build<ObjectMapper>()

    override fun onAuthenticationSuccess(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authentication: Authentication?
    ) {
        val username = authentication?.name
        val json = objectMapper.writeValueAsString("{\"message\":\"$username successfully logged in\"}")
        response?.contentType = "application/json"
        response?.characterEncoding = "UTF-8"
        response?.status = HttpStatus.UNAUTHORIZED.value()
        response?.writer?.write(json)
    }
}