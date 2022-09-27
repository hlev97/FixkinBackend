package hu.bme.aut.it9p0z.fixkin.skin_condition_log.repository

import hu.bme.aut.it9p0z.fixkin.skin_condition_log.model.SkinConditionLog
import hu.bme.aut.it9p0z.fixkin.skin_condition_log.model.hideUsername
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service

@Repository
interface SkinConditionLogMongoRepository : MongoRepository<SkinConditionLog,Int>

@Service
class SkinConditionLogOperationsService @Autowired constructor(
    private val repository: SkinConditionLogMongoRepository,
    private val mongoTemplate: MongoTemplate
) : SkinConditionLogOperations {

    override fun getAllLogs(): List<SkinConditionLog> =
        repository.findAll().map { log -> log.hideUsername() }

    override fun getAllLogsByUser(userName: String): List<SkinConditionLog> {
        val query = Query()
        query.addCriteria(Criteria.where("userName").`is`(userName))
        return mongoTemplate.find(query, SkinConditionLog::class.java)
    }

    override fun insertLog(userName: String, log: SkinConditionLog): SkinConditionLog {
        val logToInsert = SkinConditionLog(
            scLogId = log.scLogId,
            userName = userName,
            creationDate = log.creationDate,
            feeling = log.feeling,
            triggers = log.triggers
        )
        return repository.insert(logToInsert).hideUsername()
    }

    override fun updateLog(userName: String, scLogId: Int, log: SkinConditionLog): SkinConditionLog {
        val logToUpdate = repository.findById(scLogId)
        if (logToUpdate.isPresent) {
            val updatedLog = SkinConditionLog(
                scLogId = logToUpdate.get().scLogId,
                userName = userName,
                creationDate = log.creationDate,
                feeling = log.feeling,
                triggers = log.triggers
            )
            return repository.insert(updatedLog).hideUsername()
        } else throw Exception("Log with the given id is not in the database")
    }

    override fun deleteLog(userName: String, scLogId: Int) {
        try {
            val logs = getAllLogsByUser(userName)
            val logToDelete = logs[scLogId]
            if (logToDelete.userName == userName) {
                repository.delete(logToDelete)
            } else throw Exception("You are not authorized to delete this user's log.")
        } catch (e: Exception) {
            throw e
        }
    }

    override fun deleteAllLogsOfUser(userName: String) {
        try {
            val logs = getAllLogsByUser(userName)
            repository.deleteAll(logs)
        } catch (e: Exception) {
            throw e
        }
    }

}
