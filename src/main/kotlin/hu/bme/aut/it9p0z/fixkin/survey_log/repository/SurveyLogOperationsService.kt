package hu.bme.aut.it9p0z.fixkin.survey_log.repository

import hu.bme.aut.it9p0z.fixkin.survey_log.model.SurveyLog
import hu.bme.aut.it9p0z.fixkin.survey_log.model.hideUsername
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Repository
interface SurveyLogMongoRepository : MongoRepository<SurveyLog,LocalDate>

@Service
class SurveyLogOperationsService @Autowired constructor(
    private val repository: SurveyLogMongoRepository,
    private val mongoTemplate: MongoTemplate
) : SurveyLogOperations {

    override fun getAllLogs(): ResponseEntity<List<SurveyLog>> = ResponseEntity.ok(
        repository.findAll().map { log -> log.hideUsername() }
    )


    override fun getAllLogsByUser(userName: String): ResponseEntity<List<SurveyLog>> {
        return try {
            val query = Query()
            query.addCriteria(Criteria.where("userName").`is`(userName))
            val queryResult = mongoTemplate.find(query, SurveyLog::class.java)
            ResponseEntity.ok(queryResult)
        } catch (e: Exception) {
            ResponseEntity.notFound().build()
        }
    }

    override fun insertLog(userName: String, log: SurveyLog): ResponseEntity<SurveyLog> {
        val logsByUser = repository.findAll()
        val logToInsert = SurveyLog(
            surveyLogId = logsByUser.size+1,
            userName = userName,
            creationDate = log.creationDate,
            result = log.result
        )
        val responseLog = repository.insert(logToInsert).hideUsername()
        return ResponseEntity.ok(responseLog)
    }

    override fun updateLog(userName: String, surveyLogId: Int, log: SurveyLog): ResponseEntity<SurveyLog> {
        val query = Query()
        query.addCriteria(Criteria.where("surveyLogId").`is`(surveyLogId))
        val update = Update()
        update.set("result",log.result)
        val updateResult = mongoTemplate.updateFirst(query,update, SurveyLog::class.java)
        if (!updateResult.wasAcknowledged()) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build()
        }
        return ResponseEntity.ok(log)
    }

    override fun deleteLog(userName: String, surveyLogId: Int): ResponseEntity<Any> {
        return try {
            val query = Query()
            query.addCriteria(Criteria.where("userName").`is`(userName))
            val logs = mongoTemplate.find(query, SurveyLog::class.java)
            val logToDelete = logs[surveyLogId]
            if (logToDelete.userName == userName) {
                repository.delete(logToDelete)
                ResponseEntity.ok().build()
            } else ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.CONFLICT).build()
        }
    }

    override fun deleteAllLogsOfUser(userName: String): ResponseEntity<Any> {
        return try {
            val query = Query()
            query.addCriteria(Criteria.where("userName").`is`(userName))
            val logs = mongoTemplate.find(query, SurveyLog::class.java)
            repository.deleteAll(logs)
            ResponseEntity.ok().build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.CONFLICT).build()
        }
    }

}
