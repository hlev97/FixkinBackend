package hu.bme.aut.it9p0z.fixkin.security

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.io.IOException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class RestAuthEntryPoint : BasicAuthenticationEntryPoint() {

    private val objectMapper = Jackson2ObjectMapperBuilder.json().build<ObjectMapper>()

    @Throws(IOException::class)
    override fun commence(request: HttpServletRequest, response: HttpServletResponse, authException: AuthenticationException) {
        val authHeader = "Auth realm=$realmName"
        response.addHeader("WWW-Authenticate", authHeader)
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        val json = objectMapper.writeValueAsString("{\"error\":\"unauthorized\"}")
        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"
        response.writer.write(json)
    }

    override fun afterPropertiesSet() {
        realmName = "hu.bme.aut.it9p0z"
        super.afterPropertiesSet()
    }

}
