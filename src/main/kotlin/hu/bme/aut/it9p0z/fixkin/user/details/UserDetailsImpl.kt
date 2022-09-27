package hu.bme.aut.it9p0z.fixkin.user.details

import hu.bme.aut.it9p0z.fixkin.user.model.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.stream.Collectors

class UserDetailsImpl constructor(
    private val user: User
) : UserDetails {

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> =
        user.roles.stream().map { role: String? ->
            SimpleGrantedAuthority(role)
        }.collect(Collectors.toList())

    override fun getPassword(): String = user.password

    override fun getUsername(): String = user.userName

    override fun isAccountNonExpired(): Boolean = !user.expired

    override fun isAccountNonLocked(): Boolean = !user.locked

    override fun isCredentialsNonExpired(): Boolean = !user.credentialsExpired

    override fun isEnabled(): Boolean = user.enabled

}
