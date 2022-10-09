package hu.bme.aut.it9p0z.fixkin.user.controller

import hu.bme.aut.it9p0z.fixkin.user.model.User
import hu.bme.aut.it9p0z.fixkin.user.repository.UserMongoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException.Unauthorized
import java.io.Serializable
import java.security.Principal

@RestController
@RequestMapping("/user")
class UserController @Autowired constructor(
    private val userRepository: UserMongoRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @GetMapping("/all")
    @Secured(User.ROLE_USER)
    fun getAllUsers(): MutableList<User> = userRepository.findAll()

    @GetMapping("/all/usernames")
    fun getAllUsernames(): List<String> {
        val users = userRepository.findAll()
        return users.map { it.userName }
    }

    @GetMapping("/me/principal")
    @Secured(User.ROLE_USER)
    fun userData(principal: Principal?): ResponseEntity<Principal?>? {
        return ResponseEntity(principal, HttpStatus.OK)
    }

    @GetMapping("/me")
    @Secured(User.ROLE_USER)
    fun getMe(): ResponseEntity<Any> =
        try {
            val auth: Authentication = SecurityContextHolder.getContext().authentication
            val user = userRepository.findById(auth.name)
            if (user.isPresent) {
                ResponseEntity(user.get(), HttpStatus.OK)
            } else throw UsernameNotFoundException("Username not found")
        } catch (e: UsernameNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }

    @PostMapping("/create")
    fun addNewUser(@RequestBody user: User): User {
        val userToAdd = User(
            userName = user.userName,
            fullName = user.fullName,
            height = user.height,
            weight = user.weight,
            diseases = user.diseases,
            medicines = user.medicines,
            averageLifeQualityIndex = user.averageLifeQualityIndex,
            roles = user.roles,
            password =  passwordEncoder.encode(user.password),
            expired = user.expired,
            locked = user.locked,
            credentialsExpired = user.credentialsExpired,
            enabled = true
        )
        return userRepository.save(userToAdd)
    }

    @GetMapping("/diseases/{userName}")
    @Secured(User.ROLE_USER)
    fun getDiseasesByUser(@PathVariable("userName") userName: String): Serializable? {
        val user = userRepository.findById(userName)
        return if (user.isPresent) {
            user.get().diseases
        } else "User not found"
    }

    @PutMapping("/diseases/add/{userName}/{newDisease}")
    @Secured(User.ROLE_USER)
    fun addNewDiseaseToUser(
        @PathVariable("userName") userName: String,
        @PathVariable("newDisease") newDisease: String
    ): String {
        val user = userRepository.findById(userName)
        return if (user.isPresent) {
            val userCopy = user.get()
            userCopy.diseases.add(newDisease)
            userRepository.save(userCopy)
            "$newDisease is added $userName's diseases"
        } else "User not found"
    }

    @PutMapping("/diseases/remove/{userName}/{diseaseToRemove}")
    @Secured(User.ROLE_USER)
    fun removeDiseaseFromUser(
        @PathVariable("userName") userName: String,
        @PathVariable("diseaseToRemove") diseaseToRemove: String
    ): String {
        val user = userRepository.findById(userName)
        return if (user.isPresent) {
            val userCopy = user.get()
            userCopy.diseases.remove(diseaseToRemove)
            userRepository.save(userCopy)
            "$diseaseToRemove is removed from $userName's diseases"
        } else "User not found"
    }

    @GetMapping("/medicines/{userName}")
    @Secured(User.ROLE_USER)
    fun getMedicinesByUser(@PathVariable("userName") userName: String): Serializable? {
        val user = userRepository.findById(userName)
        return if (user.isPresent) {
            user.get().medicines
        } else "User not found"
    }

    @PutMapping("/medicines/add/{userName}/{newMedicine}")
    @Secured(User.ROLE_USER)
    fun addNewMedicineToUser(
        @PathVariable("userName") userName: String,
        @PathVariable("newMedicine") newMedicine: String
    ): String {
        val user = userRepository.findById(userName)
        return if (user.isPresent) {
            val userCopy = user.get()
            userCopy.medicines.add(newMedicine)
            userRepository.save(userCopy)
            "$newMedicine is added $userName's medicines"
        } else "User not found"
    }

    @PutMapping("/medicines/remove/{userName}/{medicineToRemove}")
    @Secured(User.ROLE_USER)
    fun removeMedicine(
        @PathVariable("userName") userName: String,
        @PathVariable("medicineToRemove") medicineToRemove: String
    ): String {
        val user = userRepository.findById(userName)
        return if (user.isPresent) {
            val userCopy = user.get()
            userCopy.medicines.remove(medicineToRemove)
            userRepository.save(userCopy)
            "$medicineToRemove is removed from $userName's medicines"
        } else "User not found"
    }

}
