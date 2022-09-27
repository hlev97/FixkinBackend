package hu.bme.aut.it9p0z.fixkin.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
class SecurityConfig @Autowired constructor(
    private val restAuthEntryPoint: RestAuthEntryPoint,
    private val userDetailsService: UserDetailsService
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        val authManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder::class.java)
        authManagerBuilder.userDetailsService(userDetailsService)
        val authManager = authManagerBuilder.build()
        return http
            .cors()
            .and()
            .csrf()
            .disable()
            .authorizeHttpRequests()
            .antMatchers("/users/*").permitAll()
            .antMatchers("/").hasRole("USER")
            .and()
            .formLogin()
            .loginPage("/login")
            .defaultSuccessUrl("/home")
            .failureUrl("/")
            .permitAll()
            .and()
            .logout()
            .logoutSuccessHandler(LogoutSuccessHandler())
            .and()
            .httpBasic()
            .authenticationEntryPoint(restAuthEntryPoint)
            .and()
            .authenticationManager(authManager)
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
            .and()
            .build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource? {
        val config = CorsConfiguration()
        config.allowedOrigins = listOf("*")
        config.allowedMethods = listOf("GET", "POST", "PUT", "DELETE")
        config.allowCredentials = true
        config.allowedHeaders = listOf("Authorization", "Cache-Control", "Content-Type")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return source
    }

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder = BCryptPasswordEncoder()

}
