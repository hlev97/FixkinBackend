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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val o: User = other as User
        if (userName != o.userName) return false
        return true
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + if (fullName.isNullOrEmpty()) 0 else fullName.hashCode()
        return result
    }

}
