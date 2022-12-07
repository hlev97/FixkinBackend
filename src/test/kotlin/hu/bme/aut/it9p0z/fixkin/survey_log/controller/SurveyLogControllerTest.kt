package hu.bme.aut.it9p0z.fixkin.survey_log.controller

import hu.bme.aut.it9p0z.fixkin.survey_log.model.SurveyLog
import hu.bme.aut.it9p0z.fixkin.survey_log.repository.SurveyLogOperationsService
import hu.bme.aut.it9p0z.fixkin.user.details.UserMongoRepository
import hu.bme.aut.it9p0z.fixkin.user.model.User
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate


@AutoConfigureMockMvc
@SpringBootTest
class SurveyLogControllerTest @Autowired constructor(
    private val mvc: MockMvc,
    private val operations: SurveyLogOperationsService,
    private val userMongoRepository: UserMongoRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @BeforeEach
    fun setUp() {
        addUser()
        addSurveyLogs()
    }

    @AfterEach
    fun tearDown() {
        operations.deleteAllLogsOfUser("hlev97")
        userMongoRepository.deleteById("hlev97")
    }

    @Test
    fun contextLoads() {
        Assertions.assertThat(mvc).isNotNull
        Assertions.assertThat(operations).isNotNull
        Assertions.assertThat(userMongoRepository).isNotNull
        Assertions.assertThat(passwordEncoder).isNotNull
    }

    @Test
    fun `get all survey logs, request allowed`() {
        val result = "[{\"id\":1,\"surveyLogId\":1,\"userName\":null,\"creationDate\":\"2022/12/01\",\"result\":14.0},{\"id\":2,\"surveyLogId\":2,\"userName\":null,\"creationDate\":\"2022/12/02\",\"result\":12.0},{\"id\":3,\"surveyLogId\":3,\"userName\":null,\"creationDate\":\"2022/12/03\",\"result\":13.0},{\"id\":4,\"surveyLogId\":4,\"userName\":null,\"creationDate\":\"2022/12/04\",\"result\":15.0},{\"id\":5,\"surveyLogId\":5,\"userName\":null,\"creationDate\":\"2022/12/05\",\"result\":12.0}]"
        mvc.perform(get("http://localhost:8102/survey_logs/all")
            .with(user("hlev97").password("password").roles("ADMIN","USER"))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(result))
            .andExpect(status().isOk)
    }

    @Test
    fun `GET request made with a USER role expecting FORBIDDEN status`() {
        mvc.perform(get("http://localhost:8102/survey_logs/all")
            .with(user("hlev97").password("password").roles("USER"))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden)
    }

    @Test
    fun `POST request expecting OK status and matching result`() {
        val body = "{\"creationDate\":\"2022/12/06\",\"result\":14.0}"
        val result = "{\"id\":6,\"surveyLogId\":6,\"userName\":null,\"creationDate\":\"2022/12/06\",\"result\":14.0}"
        mvc.perform(post("http://localhost:8102/survey_logs/")
            .content(body)
            .accept(MediaType.APPLICATION_JSON)
            .with(user("hlev97").password("password").roles("USER"))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(result))
    }

    @Test
    fun `PUT request expecting OK status and matching result`() {
        val body = "{\"creationDate\":\"2022/12/02\",\"result\":14.0}"
        val result = "{\"id\":0,\"surveyLogId\":0,\"userName\":null,\"creationDate\":\"2022/12/02\",\"result\":14.0}"
        mvc.perform(put("http://localhost:8102/survey_logs/{surveyLogId}","2")
            .content(body)
            .accept(MediaType.APPLICATION_JSON)
            .with(user("hlev97").password("password").roles("USER"))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(result))
            .andExpect(status().isOk)
    }

    @Test
    fun `GET request expecting OK status and matching result that contains a list of survey logs`() {
        val result = "[{\"id\":1,\"surveyLogId\":1,\"userName\":\"hlev97\",\"creationDate\":\"2022/12/01\",\"result\":14.0},{\"id\":2,\"surveyLogId\":2,\"userName\":\"hlev97\",\"creationDate\":\"2022/12/02\",\"result\":12.0},{\"id\":3,\"surveyLogId\":3,\"userName\":\"hlev97\",\"creationDate\":\"2022/12/03\",\"result\":13.0},{\"id\":4,\"surveyLogId\":4,\"userName\":\"hlev97\",\"creationDate\":\"2022/12/04\",\"result\":15.0},{\"id\":5,\"surveyLogId\":5,\"userName\":\"hlev97\",\"creationDate\":\"2022/12/05\",\"result\":12.0}]"
        mvc.perform(get("http://localhost:8102/survey_logs/all/me")
            .with(user("hlev97").password("password").roles("USER"))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(result))
            .andExpect(status().isOk)
    }

    @Test
    fun `DELETE request expecting OK status`() {
        mvc.perform(delete("http://localhost:8102/survey_logs/{surveyLogId}","2")
            .with(user("hlev97").password("password").roles("USER"))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
    }

    val logs = listOf(
        SurveyLog(
            id = 1,
            surveyLogId = 1,
            userName = "hlev97",
            creationDate = LocalDate.of(2022,12,1),
            result = 14.0
        ),
        SurveyLog(
            id = 2,
            surveyLogId = 2,
            userName = "hlev97",
            creationDate = LocalDate.of(2022,12,2),
            result = 12.0
        ),
        SurveyLog(
            id = 3,
            surveyLogId = 3,
            userName = "hlev97",
            creationDate = LocalDate.of(2022,12,3),
            result = 13.0
        ),
        SurveyLog(
            id = 4,
            surveyLogId = 4,
            userName = "hlev97",
            creationDate = LocalDate.of(2022,12,4),
            result = 15.0
        ),
        SurveyLog(
            id = 5,
            surveyLogId = 5,
            userName = "hlev97",
            creationDate = LocalDate.of(2022,12,5),
            result = 12.0
        )
    )

    private fun addUser() {
        var user = User(
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

        userMongoRepository.save(user)

        user = User(
            userName = "hlev",
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

        userMongoRepository.save(user)
    }

    private fun addSurveyLogs() {
        for (log in logs) {
            operations.insertLog("hlev97",log)
        }
    }
}