package hu.bme.aut.it9p0z.fixkin.user.details

import hu.bme.aut.it9p0z.fixkin.user.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import java.util.*

@Repository
interface UserMongoRepository : MongoRepository<User, String>
@Service
class UserDetailsServiceImpl @Autowired constructor(
    private val repository: UserMongoRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user: Optional<User> = repository.findById(username)
        return if (!user.isPresent) {
            throw UsernameNotFoundException("$username is not listed")
        } else UserDetailsImpl(user.get())
    }

}
