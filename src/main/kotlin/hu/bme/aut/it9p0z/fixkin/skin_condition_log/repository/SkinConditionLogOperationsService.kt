package hu.bme.aut.it9p0z.fixkin.skin_condition_log.repository

import hu.bme.aut.it9p0z.fixkin.skin_condition_log.model.SkinConditionLog
import hu.bme.aut.it9p0z.fixkin.skin_condition_log.model.hideUsername
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
interface SkinConditionLogMongoRepository : MongoRepository<SkinConditionLog,LocalDate>

@Service
class SkinConditionLogOperationsService @Autowired constructor(
    private val repository: SkinConditionLogMongoRepository,
    private val mongoTemplate: MongoTemplate
) : SkinConditionLogOperations {

    override fun getAllLogs(): ResponseEntity<List<SkinConditionLog>> = ResponseEntity.ok(
        repository.findAll().map { log -> log.hideUsername() }
    )

    override fun getAllLogsByUser(userName: String): ResponseEntity<List<SkinConditionLog>> {
        return try {
            val query = Query()
            query.addCriteria(Criteria.where("userName").`is`(userName))
            val queryResult = mongoTemplate.find(query, SkinConditionLog::class.java)
            ResponseEntity.ok(queryResult)
        } catch (e: Exception) {
            ResponseEntity.notFound().build()
        }
    }

    override fun insertLog(userName: String, log: SkinConditionLog): ResponseEntity<SkinConditionLog> {
        val logsByUser = repository.findAll()
        val logToInsert = SkinConditionLog(
            scLogId = logsByUser.size,
            userName = userName,
            creationDate = log.creationDate,
            feeling = log.feeling,
            foodTriggers = log.foodTriggers,
            weatherTriggers = log.weatherTriggers,
            mentalHealthTriggers = log.mentalHealthTriggers,
            otherTriggers = log.otherTriggers
        )
        val responseLog = repository.insert(logToInsert).hideUsername()
        return ResponseEntity.ok(responseLog)
    }

    override fun updateLog(userName: String, scLogId: Int, log: SkinConditionLog): ResponseEntity<SkinConditionLog> {
        val query = Query()
        query.addCriteria(Criteria.where("scLogId").`is`(scLogId))
        val update = Update()
        update.set("feeling",log.feeling)
        update.set("foodTriggers",log.foodTriggers)
        update.set("weatherTriggers",log.weatherTriggers)
        update.set("mentalHealthTriggers",log.mentalHealthTriggers)
        update.set("otherTriggers",log.otherTriggers)
        val updateResult = mongoTemplate.updateFirst(query,update,SkinConditionLog::class.java)
        if (!updateResult.wasAcknowledged()) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build()
        }
        return ResponseEntity.ok(log)
    }

    override fun deleteLog(userName: String, scLogId: Int): ResponseEntity<Any> {
        return try {
            val query = Query()
            query.addCriteria(Criteria.where("userName").`is`(userName))
            val logs = mongoTemplate.find(query, SkinConditionLog::class.java)
            val logToDelete = logs[scLogId]
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
            val logs = mongoTemplate.find(query, SkinConditionLog::class.java)
            repository.deleteAll(logs)
            ResponseEntity.ok().build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.CONFLICT).build()
        }
    }
}
