package hu.bme.aut.it9p0z.fixkin.survey_log.repository

import hu.bme.aut.it9p0z.fixkin.survey_log.model.SurveyLog
import org.springframework.http.ResponseEntity

interface SurveyLogOperations {

    fun getAllLogs(): ResponseEntity<List<SurveyLog>>

    fun getAllLogsByUser(userName: String): ResponseEntity<List<SurveyLog>>

    fun insertLog(userName: String, log: SurveyLog): ResponseEntity<SurveyLog>

    fun updateLog(userName: String, surveyLogId: Int, log: SurveyLog): ResponseEntity<SurveyLog>

    fun deleteLog(userName: String, surveyLogId: Int): ResponseEntity<Any>

    fun deleteAllLogsOfUser(userName: String): ResponseEntity<Any>

}
