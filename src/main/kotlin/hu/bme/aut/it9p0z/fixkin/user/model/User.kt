package hu.bme.aut.it9p0z.fixkin.user.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "users")
data class User(
    @Id val userName: String,
    val fullName: String?,
    val height: Double?,
    val weight: Double?,
    val diseases: ArrayList<String> = arrayListOf(),
    val medicines: ArrayList<String> = arrayListOf(),
    val averageLifeQualityIndex: Double?,
    val roles: MutableList<String> = mutableListOf(),
    val password: String,
    val expired: Boolean = false,
    val locked: Boolean = false,
    val credentialsExpired: Boolean = false,
    val enabled: Boolean = false,
) {

    companion object {
        const val ROLE_USER = "ROLE_USER"
        const val ROLE_ADMIN = "ROLE_ADMIN"
    }

}
