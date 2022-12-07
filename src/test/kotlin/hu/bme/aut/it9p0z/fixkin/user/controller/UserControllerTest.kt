package hu.bme.aut.it9p0z.fixkin.user.controller

import hu.bme.aut.it9p0z.fixkin.user.details.UserMongoRepository
import hu.bme.aut.it9p0z.fixkin.user.model.User
import org.hamcrest.CoreMatchers.containsString
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@AutoConfigureMockMvc
@SpringBootTest
class UserControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var repository: UserMongoRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @BeforeEach
    fun setUp() {
        addUser()
    }

    @AfterEach
    fun tearDown() {
        repository.deleteById("hlev97")

        if (repository.findById("hlev").isPresent) {
            repository.deleteById("hlev")
        }
    }

    @Test
    fun getAllUsers() {
        val result = "[{\"userName\":\"hlev97\",\"fullName\":\"Levente Heizer\",\"height\":178.0,\"weight\":74.0,\"diseases\":[\"inverse psoriasis\",\"plaque psoriasis\"],\"medicines\":[\"methotrexate\"],\"averageLifeQualityIndex\":null,\"roles\":[\"ROLE_USER\",\"ROLE_ADMIN\"],\"password\":"
        mvc.perform(get("http://localhost:8102/user/all/")
            .with(SecurityMockMvcRequestPostProcessors.user("hlev97").password("password").roles("USER","ADMIN"))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(containsString(result)))
            .andExpect(status().isOk)
    }

    @Test
    fun getAllUsernames() {
        val result = "[\"hlev97\"]"
        mvc.perform(get("http://localhost:8102/user/all/usernames")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(result))
            .andExpect(status().isOk)
    }

    @Test
    fun `GET request expecting OK status and matching result that contains user info`() {
        val result = "{\"userName\":\"hlev97\",\"fullName\":\"Levente Heizer\",\"height\":178.0,\"weight\":74.0,\"diseases\":[\"inverse psoriasis\",\"plaque psoriasis\"],\"medicines\":[\"methotrexate\"],\"averageLifeQualityIndex\":null,\"roles\":[\"ROLE_USER\",\"ROLE_ADMIN\"],\"password\":\"\$2a\$10\$"
        mvc.perform(get("http://localhost:8102/user/me/")
            .with(SecurityMockMvcRequestPostProcessors.user("hlev97").password("password").roles("USER"))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(containsString(result)))
            .andExpect(status().isOk)
    }

    @Test
    fun `POST request expecting OK status and matching response that contains registered user info`() {
        val body = "{\"userName\":\"hlev\",\"fullName\":\"Heizer Levente\",\"height\":178.0,\"weight\":75.0,\"roles\":[\"ROLE_USER\"],\"password\":\"password\"}"
        val result = "{\"userName\":\"hlev\",\"fullName\":\"Heizer Levente\",\"height\":178.0,\"weight\":75.0,\"diseases\":[],\"medicines\":[],\"averageLifeQualityIndex\":null,\"roles\":[\"ROLE_USER\"],\"password\":"
        mvc.perform(post("http://localhost:8102/user/")
                .content(body)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(containsString(result)))
            .andExpect(status().isOk)
    }

    @Test
    fun `GET request expecting OK status and matching result that contains a list of the user's diseases`() {
        val result = "[\"inverse psoriasis\",\"plaque psoriasis\"]"
        mvc.perform(get("http://localhost:8102/user/diseases")
            .with(SecurityMockMvcRequestPostProcessors.user("hlev97").password("password").roles("USER"))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(result))
            .andExpect(status().isOk)
    }

    @Test
    fun `PUT request expecting OK status and matching result about the addition of a new disease`() {
        val result = "psoriasis is added hlev97's diseases"
        mvc.perform(put("http://localhost:8102/user/diseases/add/{newDisease}","psoriasis")
            .with(SecurityMockMvcRequestPostProcessors.user("hlev97").password("password").roles("USER"))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(result))
            .andExpect(status().isOk)
    }

    @Test
    fun `PUT request expecting OK status and matching result about the removal of a disease`() {
        val result = "psoriasis is removed from hlev97's diseases"
        mvc.perform(put("http://localhost:8102/user/diseases/remove/{diseaseToRemove}","psoriasis")
            .with(SecurityMockMvcRequestPostProcessors.user("hlev97").password("password").roles("USER"))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(result))
            .andExpect(status().isOk)
    }

    @Test
    fun `GET request expecting OK status and matching result that contains a list of the user's medicines`() {
        val result = "[\"methotrexate\"]"
        mvc.perform(get("http://localhost:8102/user/medicines")
            .with(SecurityMockMvcRequestPostProcessors.user("hlev97").password("password").roles("USER"))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(result))
            .andExpect(status().isOk)
    }

    @Test
    fun `PUT request expecting OK status and matching result about the addition of a new medicine`() {
        val result = "Vitamin D is added hlev97's medicines"
        mvc.perform(put("http://localhost:8102/user/medicines/add/{newMedicine}","Vitamin D")
            .with(SecurityMockMvcRequestPostProcessors.user("hlev97").password("password").roles("USER"))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(result))
            .andExpect(status().isOk)
    }

    @Test
    fun `PUT request expecting OK status and matching result about the removal of a medicine`() {
        val result = "Vitamin D is removed from hlev97's medicines"
        mvc.perform(put("http://localhost:8102/user/medicines/remove/{medicineToRemove}","Vitamin D")
            .with(SecurityMockMvcRequestPostProcessors.user("hlev97").password("password").roles("USER"))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(result))
            .andExpect(status().isOk)
    }

    private fun addUser() {
        val user = User(
            userName = "hlev97",
            fullName = "Levente Heizer",
            height = 178.0,
            weight = 74.0,
            averageLifeQualityIndex = null,
            diseases = arrayListOf("inverse psoriasis", "plaque psoriasis"),
            medicines = arrayListOf("methotrexate"),
            roles = mutableListOf(User.ROLE_USER, User.ROLE_ADMIN),
            password = passwordEncoder.encode("password"),
            enabled = true
        )

        repository.save(user)
    }
}