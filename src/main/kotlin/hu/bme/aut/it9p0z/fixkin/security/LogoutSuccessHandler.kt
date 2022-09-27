package hu.bme.aut.it9p0z.fixkin.security

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class LogoutSuccessHandler : SimpleUrlLogoutSuccessHandler() {
    private val objectMapper = Jackson2ObjectMapperBuilder.json().build<ObjectMapper>()

    @Throws(IOException::class, ServletException::class)
    override fun onLogoutSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val json = objectMapper.writeValueAsString("{\"message\":\"Successfully logged out\"}")
        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"
        response.writer.write(json)
    }
}
