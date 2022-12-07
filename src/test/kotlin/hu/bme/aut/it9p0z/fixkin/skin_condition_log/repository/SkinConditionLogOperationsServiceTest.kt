package hu.bme.aut.it9p0z.fixkin.skin_condition_log.repository

import hu.bme.aut.it9p0z.fixkin.skin_condition_log.model.SkinConditionLog
import hu.bme.aut.it9p0z.fixkin.user.details.UserMongoRepository
import hu.bme.aut.it9p0z.fixkin.user.model.User
import org.assertj.core.api.Assertions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.util.AssertionErrors
import java.time.LocalDate

@RunWith(SpringRunner::class)
@SpringBootTest
class SkinConditionLogOperationsServiceTest {

    @Autowired
    lateinit var operations: SkinConditionLogOperationsService

    @Autowired
    lateinit var userMongoRepository: UserMongoRepository

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    @BeforeEach
    fun setUp() {
        addUser()
        addConditionLogs()
    }

    @AfterEach
    fun tearDown() {
        operations.deleteAllLogsOfUser("hlev97")
        userMongoRepository.deleteById("hlev97")
    }

    @Test
    fun contextLoads() {
        Assertions.assertThat(operations).isNotNull
        Assertions.assertThat(userMongoRepository).isNotNull
        Assertions.assertThat(passwordEncoder).isNotNull
    }

    @Test
    fun getAllLogs() {
        val expectedSize = logs.size
        val actualSize = operations.getAllLogs().body?.size ?: 0
        AssertionErrors.assertEquals("getAllLogs test", expectedSize, actualSize)
    }

    @Test
    fun getAllLogsByUser() {
        val expectedSize = logs.size
        val actualSize = operations.getAllLogsByUser("hlev97").body?.size ?: 0
        AssertionErrors.assertEquals("getAllLogsByUser test", expectedSize, actualSize)
    }

    @Test
    fun insertLog() {
        val sizeBeforeInsert = operations.getAllLogs().body?.size ?: 0
        operations.insertLog(
            userName = "hlev97",
            log = SkinConditionLog(
                scLogId = 12,
                userName = "hlev97",
                creationDate = LocalDate.of(2022, 10, 28),
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
            )
        )
        val sizeAfterInsert = operations.getAllLogs().body?.size ?: 0
        AssertionErrors.assertEquals("insertLog test", sizeAfterInsert, sizeBeforeInsert + 1)
    }

    @Test
    fun updateLog() {
        val sizeBeforeInsert = operations.getAllLogs().body?.size ?: 0
        val log = SkinConditionLog(
            scLogId = sizeBeforeInsert-1,
            userName = "hlev97",
            creationDate = LocalDate.now(),
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
                "dry" to true,
                "cold" to true,
                "rainy" to false,
                "windy" to true,
                "snowy" to true
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
        )
        operations.insertLog(
            userName = "hlev97",
            log = log
        )
        var logs = operations.getAllLogs().body ?: emptyList()
        val insertedLog = logs[logs.lastIndex]
        val upadatedLog = SkinConditionLog(
            scLogId = insertedLog.scLogId,
            userName = insertedLog.userName,
            creationDate = insertedLog.creationDate,
            feeling = "neutral",
            foodTriggers = hashMapOf(
                "wheat" to true,
                "milk" to false,
                "egg" to true,
                "sea food" to false,
                "nightshade" to true,
                "soy" to true,
                "citrus" to false,
                "fast food" to true,
                "fatty food" to true,
                "alcohol" to true
            ),
            weatherTriggers = insertedLog.weatherTriggers,
            mentalHealthTriggers = hashMapOf(
                "anxiety" to false,
                "depression" to true,
                "insomnia" to true
            ),
            otherTriggers = insertedLog.otherTriggers
        )
        operations.updateLog("hlev97",upadatedLog.scLogId,upadatedLog)
        logs = operations.getAllLogs().body ?: emptyList()
        AssertionErrors.assertEquals("updateLog, test", upadatedLog, logs[logs.lastIndex])
    }

    @Test
    fun deleteLog() {
        val sizeBeforeDelete = operations.getAllLogs().body?.size ?: 0
        operations.deleteLog("hlev97",2)
        val sizeAfterDelete = operations.getAllLogs().body?.size ?: 0
        AssertionErrors.assertEquals("delete by id test", sizeBeforeDelete - 1, sizeAfterDelete)
    }

    @Test
    fun deleteAllLogsOfUser() {
        operations.deleteAllLogsOfUser("hlev97")
        val sizeAfterDelete = operations.getAllLogs().body?.size ?: 0
        AssertionErrors.assertEquals("delete all by user test", 0, sizeAfterDelete)
    }

    @Test
    fun getStatistics() {
        val statistics = operations.getStatistics("hlev97").body
        assertTrue(statistics != null)
    }

    @Test
    fun getCreationDates() {
        val expectedSize = logs.size
        val actualSize = operations.getCreationDates("hlev97").body?.size ?: 0
        assertEquals(expectedSize,actualSize)
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
            roles = mutableListOf(User.ROLE_USER),
            password = passwordEncoder.encode("password"),
            enabled = true
        )

        userMongoRepository.save(user)
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

    private fun addConditionLogs() {

        for (log in logs) {
            operations.insertLog("hlev97",log)
        }
    }
}