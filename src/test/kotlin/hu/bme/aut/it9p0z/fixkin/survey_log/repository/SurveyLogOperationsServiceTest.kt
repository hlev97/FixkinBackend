package hu.bme.aut.it9p0z.fixkin.survey_log.repository

import hu.bme.aut.it9p0z.fixkin.survey_log.model.SurveyLog
import hu.bme.aut.it9p0z.fixkin.user.details.UserMongoRepository
import hu.bme.aut.it9p0z.fixkin.user.model.User
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.runner.RunWith

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.util.AssertionErrors.assertEquals
import java.time.LocalDate

@RunWith(SpringRunner::class)
@SpringBootTest
class SurveyLogOperationsServiceTest {

    @Autowired
    lateinit var operations: SurveyLogOperationsService

    @Autowired
    lateinit var userMongoRepository: UserMongoRepository

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

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
        Assertions.assertThat(operations).isNotNull
        Assertions.assertThat(userMongoRepository).isNotNull
        Assertions.assertThat(passwordEncoder).isNotNull
    }

    @Test
    fun getAllLogs() {
        val expectedSize = logs.size
        val actualSize = operations.getAllLogs().body?.size ?: 0
        assertEquals("getAllLogs test", expectedSize, actualSize)
    }

    @Test
    fun getAllLogsByUser() {
        val expectedSize = logs.size
        val actualSize = operations.getAllLogsByUser("hlev97").body?.size ?: 0
        assertEquals("getAllLogsByUser test", expectedSize, actualSize)
    }

    @Test
    fun insertLog() {
        val sizeBeforeInsert = operations.getAllLogs().body?.size ?: 0
        operations.insertLog(
            userName = "hlev97",
            log = SurveyLog(
                id = sizeBeforeInsert,
                surveyLogId = sizeBeforeInsert,
                userName = "hlev97",
                creationDate = LocalDate.now(),
                result = 5.0
            )
        )
        val sizeAfterInsert = operations.getAllLogs().body?.size ?: 0
        assertEquals("insertLog test", sizeAfterInsert,sizeBeforeInsert+1)
    }

    @Test
    fun updateLog() {
        val sizeBeforeInsert = operations.getAllLogs().body?.size ?: 0
        val log = SurveyLog(
            id = sizeBeforeInsert,
            surveyLogId = sizeBeforeInsert,
            userName = "hlev97",
            creationDate = LocalDate.now(),
            result = 5.0
        )
        operations.insertLog(
            userName = "hlev97",
            log = log
        )
        var logs = operations.getAllLogs().body ?: emptyList()
        val insertedLog = logs[logs.lastIndex]
        val upadatedLog = SurveyLog(
            id = insertedLog.surveyLogId,
            surveyLogId = insertedLog.surveyLogId,
            userName = insertedLog.userName,
            creationDate = insertedLog.creationDate,
            result = 20.0
        )
        operations.updateLog("hlev97",upadatedLog.surveyLogId,upadatedLog)
        logs = operations.getAllLogs().body ?: emptyList()
        assertEquals("updateLog, test", upadatedLog,logs[logs.lastIndex])
    }

    @Test
    fun deleteLog() {
        val sizeBeforeDelete = operations.getAllLogs().body?.size ?: 0
        operations.deleteLog("hlev97",2)
        val sizeAfterDelete = operations.getAllLogs().body?.size ?: 0
        assertEquals("delete by id test", sizeBeforeDelete-1,sizeAfterDelete)
    }

    @Test
    fun deleteAllLogsOfUser() {
        operations.deleteAllLogsOfUser("hlev97")
        val sizeAfterDelete = operations.getAllLogs().body?.size ?: 0
        assertEquals("delete all by user test", 0,sizeAfterDelete)
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

    private fun addSurveyLogs() {
        for (log in logs) {
            operations.insertLog("hlev97",log)
        }
    }

}