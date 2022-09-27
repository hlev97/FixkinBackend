package hu.bme.aut.it9p0z.fixkin.survey_log.repository

import hu.bme.aut.it9p0z.fixkin.survey_log.model.SurveyLog
import hu.bme.aut.it9p0z.fixkin.survey_log.model.hideUsername
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service

@Repository
interface SurveyLogMongoRepository : MongoRepository<SurveyLog,Int>

@Service
class SurveyLogOperationsService @Autowired constructor(
    private val repository: SurveyLogMongoRepository,
    private val mongoTemplate: MongoTemplate
) : SurveyLogOperations {

    override fun getAllLogs(): List<SurveyLog> = repository.findAll().map { log -> log.hideUsername() }

    override fun getAllLogsByUser(userName: String): List<SurveyLog> {
        val query = Query()
        query.addCriteria(Criteria.where("userName").`is`(userName))
        return mongoTemplate.find(query, SurveyLog::class.java)
    }

    override fun insertLog(userName: String, log: SurveyLog): SurveyLog {
        val logToInsert = SurveyLog(
            surveyLogId = log.surveyLogId,
            userName = userName,
            creationDate = log.creationDate,
            result = log.result
        )
        return repository.insert(logToInsert).hideUsername()
    }

    override fun updateLog(userName: String, surveyLogId: Int, log: SurveyLog): SurveyLog {
        val logToUpdate = repository.findById(surveyLogId)
        if (logToUpdate.isPresent) {
            val updatedLog = SurveyLog(
                surveyLogId = logToUpdate.get().surveyLogId,
                userName = userName,
                creationDate = log.creationDate,
                result = log.result
            )
            return repository.insert(updatedLog).hideUsername()
        } else throw Exception("Log with the given id is not in the database")
    }

    override fun deleteLog(userName: String, surveyLogId: Int) {
        try {
            val logs = getAllLogsByUser(userName)
            val logToDelete = logs[surveyLogId]
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
