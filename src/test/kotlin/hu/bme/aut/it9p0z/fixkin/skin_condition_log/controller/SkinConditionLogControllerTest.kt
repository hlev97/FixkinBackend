package hu.bme.aut.it9p0z.fixkin.skin_condition_log.controller

import hu.bme.aut.it9p0z.fixkin.skin_condition_log.model.SkinConditionLog
import hu.bme.aut.it9p0z.fixkin.skin_condition_log.repository.SkinConditionLogOperationsService
import hu.bme.aut.it9p0z.fixkin.user.details.UserMongoRepository
import hu.bme.aut.it9p0z.fixkin.user.model.User
import org.assertj.core.api.Assertions
import org.hamcrest.Matchers.containsString
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
class SkinConditionLogControllerTest @Autowired constructor(
    val mvc: MockMvc,
    val operations: SkinConditionLogOperationsService,
    val userMongoRepository: UserMongoRepository,
    val passwordEncoder: PasswordEncoder
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
    fun getAllLogs_Forbidden() {
        mvc.perform(get("http://localhost:8102/skin_condition_logs/all/")
            .with(user("hlev97").password("password").roles("USER"))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden)
    }

    @Test
    fun `Adding new log to Mongo DB`() {
        val body = "{\"creationDate\" : \"2012/08/15\",\"feeling\" : \"bad\",\"foodTriggers\" : {\"soy\" : true},\"weatherTriggers\" : {\"rainy\" : true},\"mentalHealthTriggers\" : {\"anxiety\" : true},\"otherTriggers\" : {\"smoking\" : true}}"
        val result = "{\"scLogId\":12,\"userName\":null,\"creationDate\":\"2012/08/15\",\"feeling\":\"bad\",\"foodTriggers\":{\"soy\":true},\"weatherTriggers\":{\"rainy\":true},\"mentalHealthTriggers\":{\"anxiety\":true},\"otherTriggers\":{\"smoking\":true}}"
        mvc.perform(post("http://localhost:8102/skin_condition_logs/")
            .content(body)
            .accept(MediaType.APPLICATION_JSON)
            .with(user("hlev97").password("password").roles("USER"))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(result))
            .andExpect(status().isOk)
    }

    @Test
    fun updateLog() {
        val body = "{\"creationDate\" : \"2022/10/15\",\"feeling\" : \"normal\",\"foodTriggers\" : {\"soy\" : true},\"weatherTriggers\" : {\"rainy\" : true},\"mentalHealthTriggers\" : {\"anxiety\" : true},\"otherTriggers\" : {\"smoking\" : true}}"
        val result = "{\"scLogId\":0,\"userName\":null,\"creationDate\":\"2022/10/15\",\"feeling\":\"normal\",\"foodTriggers\":{\"soy\":true},\"weatherTriggers\":{\"rainy\":true},\"mentalHealthTriggers\":{\"anxiety\":true},\"otherTriggers\":{\"smoking\":true}}"
        mvc.perform(put("http://localhost:8102/skin_condition_logs/{scLogId}","10")
            .content(body)
            .accept(MediaType.APPLICATION_JSON)
            .with(user("hlev97").password("password").roles("USER"))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(result))
            .andExpect(status().isOk)
    }

    @Test
    fun getAllLogsByUser() {
        val result = "[{\"scLogId\":1,\"userName\":\"hlev97\",\"creationDate\":\"2022/10/01\",\"feeling\":\"sad\",\"foodTriggers\":{\"alcohol\":false,\"egg\":true,\"fast food\":true,\"milk\":false,\"fatty food\":true,\"wheat\":true,\"sea food\":false,\"soy\":false,\"nightshade\":true,\"citrus\":false},\"weatherTriggers\":{\"rainy\":true,\"dry\":false,\"cold\":true,\"hot\":false,\"snowy\":false,\"windy\":true},\"mentalHealthTriggers\":{\"anxiety\":false,\"depression\":true,\"insomnia\":false},\"otherTriggers\":{\"infection\":false,\"smoking\":true,\"sweat\":false,\"medicine\":true}},{\"scLogId\":2,\"userName\":\"hlev97\",\"creationDate\":\"2022/10/02\",\"feeling\":\"unhappy\",\"foodTriggers\":{\"alcohol\":false,\"egg\":true,\"fast food\":false,\"milk\":true,\"fatty food\":false,\"wheat\":false,\"sea food\":false,\"soy\":true,\"nightshade\":true,\"citrus\":false},\"weatherTriggers\":{\"rainy\":true,\"dry\":false,\"cold\":true,\"hot\":false,\"snowy\":false,\"windy\":true},\"mentalHealthTriggers\":{\"anxiety\":false,\"depression\":true,\"insomnia\":false},\"otherTriggers\":{\"infection\":false,\"smoking\":true,\"sweat\":false,\"medicine\":true}},{\"scLogId\":3,\"userName\":\"hlev97\",\"creationDate\":\"2022/10/03\",\"feeling\":\"neutral\",\"foodTriggers\":{\"alcohol\":false,\"egg\":true,\"fast food\":false,\"milk\":true,\"fatty food\":false,\"wheat\":true,\"sea food\":false,\"soy\":false,\"nightshade\":false,\"citrus\":false},\"weatherTriggers\":{\"rainy\":true,\"dry\":false,\"cold\":true,\"hot\":false,\"snowy\":false,\"windy\":true},\"mentalHealthTriggers\":{\"anxiety\":false,\"depression\":true,\"insomnia\":false},\"otherTriggers\":{\"infection\":false,\"smoking\":true,\"sweat\":false,\"medicine\":true}},{\"scLogId\":4,\"userName\":\"hlev97\",\"creationDate\":\"2022/10/04\",\"feeling\":\"neutral\",\"foodTriggers\":{\"alcohol\":false,\"egg\":true,\"fast food\":false,\"milk\":false,\"fatty food\":false,\"wheat\":false,\"sea food\":true,\"soy\":false,\"nightshade\":false,\"citrus\":false},\"weatherTriggers\":{\"rainy\":false,\"dry\":false,\"cold\":true,\"hot\":false,\"snowy\":false,\"windy\":true},\"mentalHealthTriggers\":{\"anxiety\":false,\"depression\":false,\"insomnia\":false},\"otherTriggers\":{\"infection\":false,\"smoking\":true,\"sweat\":false,\"medicine\":false}},{\"scLogId\":5,\"userName\":\"hlev97\",\"creationDate\":\"2022/10/05\",\"feeling\":\"unhappy\",\"foodTriggers\":{\"alcohol\":false,\"egg\":true,\"fast food\":false,\"milk\":true,\"fatty food\":true,\"wheat\":true,\"sea food\":false,\"soy\":false,\"nightshade\":false,\"citrus\":false},\"weatherTriggers\":{\"rainy\":false,\"dry\":false,\"cold\":false,\"hot\":false,\"snowy\":false,\"windy\":false},\"mentalHealthTriggers\":{\"anxiety\":fa"
        mvc.perform(get("http://localhost:8102/skin_condition_logs/all/me")
            .with(user("hlev97").password("password").roles("USER"))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(containsString(result)))
            .andExpect(status().isOk)
    }

    @Test
    fun deleteLogById() {
        mvc.perform(delete("http://localhost:8102/skin_condition_logs/{scLogId}","2")
            .with(user("hlev97").password("password").roles("USER"))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
    }

    @Test
    fun getStatistics() {
        val result = "{\"feelings\":{\"unhappy\":0.4,\"sad\":0.2,\"happy\":0.0,\"neutral\":0.4,\"joyful\":0.0},\"foodTriggers\":{\"egg\":0.26190478,\"milk\":0.21428572,\"fatty food\":0.1904762,\"wheat\":0.21428572,\"nightshade\":0.04761905},\"weatherTriggers\":{\"rainy\":0.27272728,\"cold\":0.36363637,\"hot\":0.0,\"snowy\":0.0,\"windy\":0.36363637},\"mentalHealthTriggers\":{\"anxiety\":0.0,\"depression\":0.3,\"insomnia\":0.7},\"otherTriggers\":{\"smoking\":0.78571427,\"sweat\":0.0,\"medicine\":0.21428572}}"
        mvc.perform(get("http://localhost:8102/skin_condition_logs/statistics/me")
            .with(user("hlev97").password("password").roles("ADMIN","USER"))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(result))
            .andExpect(status().isOk)
    }

    @Test
    fun getDates() {
        val result = "[\"2022-10-01\",\"2022-10-02\",\"2022-10-03\",\"2022-10-04\",\"2022-10-05\",\"2022-10-06\",\"2022-10-07\",\"2022-10-08\",\"2022-10-09\",\"2022-10-10\",\"2022-10-11\"]"
        mvc.perform(get("http://localhost:8102/skin_condition_logs/creationDates/me")
            .with(user("hlev97").password("password").roles("ADMIN","USER"))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(result))
            .andExpect(status().isOk)
    }

    val logs = listOf(
        SkinConditionLog(
            scLogId = 1,
            userName = "hlev97",
            creationDate = LocalDate.of(2022, 10, 1),
            feeling = "sad",
            foodTriggers = hashMapOf(
                "wheat" to true,
                "milk" to false,
                "egg" to true,
                "sea food" to false,
                "nightshade" to true,
                "soy" to false,
                "citrus" to false,
                "fast food" to true,
                "fatty food" to true,
                "alcohol" to false
            ),
            weatherTriggers = hashMapOf(
                "hot" to false,
                "dry" to false,
                "cold" to true,
                "rainy" to true,
                "windy" to true,
                "snowy" to false
            ),
            mentalHealthTriggers = hashMapOf(
                "anxiety" to false,
                "depression" to true,
                "insomnia" to false
            ),
            otherTriggers = hashMapOf(
                "medicine" to true,
                "infection" to false,
                "sweat" to false,
                "smoking" to true
            )
        ),
        SkinConditionLog(
            scLogId = 2,
            userName = "hlev97",
            creationDate = LocalDate.of(2022, 10, 2),
            feeling = "unhappy",
            foodTriggers = hashMapOf(
                "wheat" to false,
                "milk" to true,
                "egg" to true,
                "sea food" to false,
                "nightshade" to true,
                "soy" to true,
                "citrus" to false,
                "fast food" to false,
                "fatty food" to false,
                "alcohol" to false
            ),
            weatherTriggers = hashMapOf(
                "hot" to false,
                "dry" to false,
                "cold" to true,
                "rainy" to true,
                "windy" to true,
                "snowy" to false
            ),
            mentalHealthTriggers = hashMapOf(
                "anxiety" to false,
                "depression" to true,
                "insomnia" to false
            ),
            otherTriggers = hashMapOf(
                "medicine" to true,
                "infection" to false,
                "sweat" to false,
                "smoking" to true
            )
        ),
        SkinConditionLog(
            scLogId = 3,
            userName = "hlev97",
            creationDate = LocalDate.of(2022, 10, 3),
            feeling = "neutral",
            foodTriggers = hashMapOf(
                "wheat" to true,
                "milk" to true,
                "egg" to true,
                "sea food" to false,
                "nightshade" to false,
                "soy" to false,
                "citrus" to false,
                "fast food" to false,
                "fatty food" to false,
                "alcohol" to false
            ),
            weatherTriggers = hashMapOf(
                "hot" to false,
                "dry" to false,
                "cold" to true,
                "rainy" to true,
                "windy" to true,
                "snowy" to false
            ),
            mentalHealthTriggers = hashMapOf(
                "anxiety" to false,
                "depression" to true,
                "insomnia" to false
            ),
            otherTriggers = hashMapOf(
                "medicine" to true,
                "infection" to false,
                "sweat" to false,
                "smoking" to true
            )
        ),
        SkinConditionLog(
            scLogId = 4,
            userName = "hlev97",
            creationDate = LocalDate.of(2022, 10, 4),
            feeling = "neutral",
            foodTriggers = hashMapOf(
                "wheat" to false,
                "milk" to false,
                "egg" to true,
                "sea food" to true,
                "nightshade" to false,
                "soy" to false,
                "citrus" to false,
                "fast food" to false,
                "fatty food" to false,
                "alcohol" to false
            ),
            weatherTriggers = hashMapOf(
                "hot" to false,
                "dry" to false,
                "cold" to true,
                "rainy" to false,
                "windy" to true,
                "snowy" to false
            ),
            mentalHealthTriggers = hashMapOf(
                "anxiety" to false,
                "depression" to false,
                "insomnia" to false
            ),
            otherTriggers = hashMapOf(
                "medicine" to false,
                "infection" to false,
                "sweat" to false,
                "smoking" to true
            )
        ),
        SkinConditionLog(
            scLogId = 5,
            userName = "hlev97",
            creationDate = LocalDate.of(2022, 10, 5),
            feeling = "unhappy",
            foodTriggers = hashMapOf(
                "wheat" to true,
                "milk" to true,
                "egg" to true,
                "sea food" to false,
                "nightshade" to false,
                "soy" to false,
                "citrus" to false,
                "fast food" to false,
                "fatty food" to true,
                "alcohol" to false
            ),
            weatherTriggers = hashMapOf(
                "hot" to false,
                "dry" to false,
                "cold" to false,
                "rainy" to false,
                "windy" to false,
                "snowy" to false
            ),
            mentalHealthTriggers = hashMapOf(
                "anxiety" to false,
                "depression" to false,
                "insomnia" to true
            ),
            otherTriggers = hashMapOf(
                "medicine" to false,
                "infection" to false,
                "sweat" to false,
                "smoking" to true
            )
        ),
        SkinConditionLog(
            scLogId = 6,
            userName = "hlev97",
            creationDate = LocalDate.of(2022, 10, 6),
            feeling = "unhappy",
            foodTriggers = hashMapOf(
                "wheat" to true,
                "milk" to true,
                "egg" to true,
                "sea food" to false,
                "nightshade" to false,
                "soy" to false,
                "citrus" to false,
                "fast food" to false,
                "fatty food" to true,
                "alcohol" to false
            ),
            weatherTriggers = hashMapOf(
                "hot" to false,
                "dry" to false,
                "cold" to false,
                "rainy" to false,
                "windy" to false,
                "snowy" to false
            ),
            mentalHealthTriggers = hashMapOf(
                "anxiety" to false,
                "depression" to false,
                "insomnia" to true
            ),
            otherTriggers = hashMapOf(
                "medicine" to false,
                "infection" to false,
                "sweat" to false,
                "smoking" to true
            )
        ),
        SkinConditionLog(
            scLogId = 7,
            userName = "hlev97",
            creationDate = LocalDate.of(2022, 10, 7),
            feeling = "unhappy",
            foodTriggers = hashMapOf(
                "wheat" to true,
                "milk" to true,
                "egg" to true,
                "sea food" to false,
                "nightshade" to false,
                "soy" to false,
                "citrus" to false,
                "fast food" to false,
                "fatty food" to true,
                "alcohol" to false
            ),
            weatherTriggers = hashMapOf(
                "hot" to false,
                "dry" to false,
                "cold" to false,
                "rainy" to false,
                "windy" to false,
                "snowy" to false
            ),
            mentalHealthTriggers = hashMapOf(
                "anxiety" to false,
                "depression" to false,
                "insomnia" to true
            ),
            otherTriggers = hashMapOf(
                "medicine" to false,
                "infection" to false,
                "sweat" to false,
                "smoking" to true
            )
        ),
        SkinConditionLog(
            scLogId = 8,
            userName = "hlev97",
            creationDate = LocalDate.of(2022, 10, 8),
            feeling = "unhappy",
            foodTriggers = hashMapOf(
                "wheat" to true,
                "milk" to true,
                "egg" to true,
                "sea food" to false,
                "nightshade" to false,
                "soy" to false,
                "citrus" to false,
                "fast food" to false,
                "fatty food" to true,
                "alcohol" to false
            ),
            weatherTriggers = hashMapOf(
                "hot" to false,
                "dry" to false,
                "cold" to false,
                "rainy" to false,
                "windy" to false,
                "snowy" to false
            ),
            mentalHealthTriggers = hashMapOf(
                "anxiety" to false,
                "depression" to false,
                "insomnia" to true
            ),
            otherTriggers = hashMapOf(
                "medicine" to false,
                "infection" to false,
                "sweat" to false,
                "smoking" to true
            )
        ),
        SkinConditionLog(
            scLogId = 9,
            userName = "hlev97",
            creationDate = LocalDate.of(2022, 10, 9),
            feeling = "unhappy",
            foodTriggers = hashMapOf(
                "wheat" to true,
                "milk" to true,
                "egg" to true,
                "sea food" to false,
                "nightshade" to false,
                "soy" to false,
                "citrus" to false,
                "fast food" to false,
                "fatty food" to true,
                "alcohol" to false
            ),
            weatherTriggers = hashMapOf(
                "hot" to false,
                "dry" to false,
                "cold" to false,
                "rainy" to false,
                "windy" to false,
                "snowy" to false
            ),
            mentalHealthTriggers = hashMapOf(
                "anxiety" to false,
                "depression" to false,
                "insomnia" to true
            ),
            otherTriggers = hashMapOf(
                "medicine" to false,
                "infection" to false,
                "sweat" to false,
                "smoking" to true
            )
        ),
        SkinConditionLog(
            scLogId = 10,
            userName = "hlev97",
            creationDate = LocalDate.of(2022, 10, 10),
            feeling = "unhappy",
            foodTriggers = hashMapOf(
                "wheat" to true,
                "milk" to true,
                "egg" to true,
                "sea food" to false,
                "nightshade" to false,
                "soy" to false,
                "citrus" to false,
                "fast food" to false,
                "fatty food" to true,
                "alcohol" to false
            ),
            weatherTriggers = hashMapOf(
                "hot" to false,
                "dry" to false,
                "cold" to false,
                "rainy" to false,
                "windy" to false,
                "snowy" to false
            ),
            mentalHealthTriggers = hashMapOf(
                "anxiety" to false,
                "depression" to false,
                "insomnia" to true
            ),
            otherTriggers = hashMapOf(
                "medicine" to false,
                "infection" to false,
                "sweat" to false,
                "smoking" to true
            )
        ),
        SkinConditionLog(
            scLogId = 11,
            userName = "hlev97",
            creationDate = LocalDate.of(2022, 10, 11),
            feeling = "unhappy",
            foodTriggers = hashMapOf(
                "wheat" to true,
                "milk" to true,
                "egg" to true,
                "sea food" to false,
                "nightshade" to false,
                "soy" to false,
                "citrus" to false,
                "fast food" to false,
                "fatty food" to true,
                "alcohol" to false
            ),
            weatherTriggers = hashMapOf(
                "hot" to false,
                "dry" to false,
                "cold" to false,
                "rainy" to false,
                "windy" to false,
                "snowy" to false
            ),
            mentalHealthTriggers = hashMapOf(
                "anxiety" to false,
                "depression" to false,
                "insomnia" to true
            ),
            otherTriggers = hashMapOf(
                "medicine" to false,
                "infection" to false,
                "sweat" to false,
                "smoking" to true
            )
        )
    )

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

        userMongoRepository.save(user)
    }

    private fun addSurveyLogs() {
        for (log in logs) {
            operations.insertLog("hlev97",log)
        }
    }
}