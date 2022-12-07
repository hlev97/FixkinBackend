package hu.bme.aut.it9p0z.fixkin.user.controller

import hu.bme.aut.it9p0z.fixkin.user.details.UserMongoRepository
import hu.bme.aut.it9p0z.fixkin.user.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/user")
class UserController @Autowired constructor(
    private val userRepository: UserMongoRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @GetMapping("/all")
    @Secured(User.ROLE_ADMIN)
    fun getAllUsers(): List<User> = userRepository.findAll().map { it.copy(password = "") }

    @GetMapping("/all/usernames")
    fun getAllUsernames(): ResponseEntity<List<String>> {
        val users = userRepository.findAll()
        return ResponseEntity.ok(users.map { it.userName })
    }

    @GetMapping("/me/principal")
    @Secured(User.ROLE_USER)
    fun userData(principal: Principal?): ResponseEntity<Principal?>? {
        return ResponseEntity.ok(principal)
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

    @PostMapping
    fun addNewUser(@RequestBody user: User): ResponseEntity<User> {
        val userToAdd = User(
            userName = user.userName,
            fullName = user.fullName,
            height = user.height,
            weight = user.weight,
            diseases = user.diseases,
            medicines = user.medicines,
            averageLifeQualityIndex = user.averageLifeQualityIndex,
            roles = mutableListOf(User.ROLE_USER),
            password =  passwordEncoder.encode(user.password),
            expired = user.expired,
            locked = user.locked,
            credentialsExpired = user.credentialsExpired,
            enabled = true
        )
        return ResponseEntity.ok(userRepository.save(userToAdd))
    }

    @GetMapping("/diseases")
    @Secured(User.ROLE_USER)
    fun getDiseasesByUser(): Any {
        val auth: Authentication = SecurityContextHolder.getContext().authentication
        val user = userRepository.findById(auth.name)
        return if (user.isPresent) {
            ResponseEntity.ok(user.get().diseases)
        } else ResponseEntity.notFound().build()
    }

    @PutMapping("/diseases/add/{newDisease}")
    @Secured(User.ROLE_USER)
    fun addNewDiseaseToUser(
        @PathVariable("newDisease") newDisease: String
    ): Any {
        val auth: Authentication = SecurityContextHolder.getContext().authentication
        val user = userRepository.findById(auth.name)
        return if (user.isPresent) {
            val userCopy = user.get()
            userCopy.diseases.add(newDisease)
            userRepository.save(userCopy)
            ResponseEntity.ok("$newDisease is added ${auth.name}'s diseases")
        } else ResponseEntity.notFound().build()
    }

    @PutMapping("/diseases/remove/{diseaseToRemove}")
    @Secured(User.ROLE_USER)
    fun removeDiseaseFromUser(
        @PathVariable("diseaseToRemove") diseaseToRemove: String
    ): Any {
        val auth: Authentication = SecurityContextHolder.getContext().authentication
        val user = userRepository.findById(auth.name)
        return if (user.isPresent) {
            val userCopy = user.get()
            userCopy.diseases.remove(diseaseToRemove)
            userRepository.save(userCopy)
            ResponseEntity.ok("$diseaseToRemove is removed from ${auth.name}'s diseases")
        } else ResponseEntity.notFound().build()
    }

    @GetMapping("/medicines")
    @Secured(User.ROLE_USER)
    fun getMedicinesByUser(): Any {
        val auth: Authentication = SecurityContextHolder.getContext().authentication
        val user = userRepository.findById(auth.name)
        return if (user.isPresent) {
            ResponseEntity.ok(user.get().medicines)
        } else ResponseEntity.notFound()
    }

    @PutMapping("/medicines/add/{newMedicine}")
    @Secured(User.ROLE_USER)
    fun addNewMedicineToUser(
        @PathVariable("newMedicine") newMedicine: String
    ): Any {
        val auth: Authentication = SecurityContextHolder.getContext().authentication
        val user = userRepository.findById(auth.name)
        return if (user.isPresent) {
            val userCopy = user.get()
            userCopy.medicines.add(newMedicine)
            userRepository.save(userCopy)
            ResponseEntity.ok("$newMedicine is added ${auth.name}'s medicines")
        } else ResponseEntity.notFound().build()
    }

    @PutMapping("/medicines/remove/{medicineToRemove}")
    @Secured(User.ROLE_USER)
    fun removeMedicine(
        @PathVariable("medicineToRemove") medicineToRemove: String
    ): Any {
        val auth: Authentication = SecurityContextHolder.getContext().authentication
        val user = userRepository.findById(auth.name)
        return if (user.isPresent) {
            val userCopy = user.get()
            userCopy.medicines.remove(medicineToRemove)
            userRepository.save(userCopy)
            ResponseEntity.ok("$medicineToRemove is removed from ${auth.name}'s medicines")
        } else ResponseEntity.notFound().build()
    }
}
