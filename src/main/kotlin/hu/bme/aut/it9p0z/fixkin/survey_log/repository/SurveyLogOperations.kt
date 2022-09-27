package hu.bme.aut.it9p0z.fixkin.survey_log.repository

import hu.bme.aut.it9p0z.fixkin.survey_log.model.SurveyLog

interface SurveyLogOperations {

    fun getAllLogs(): List<SurveyLog>

    fun getAllLogsByUser(userName: String): List<SurveyLog>

    fun insertLog(userName: String, log: SurveyLog): SurveyLog

    fun updateLog(userName: String, surveyLogId: Int, log: SurveyLog): SurveyLog

    fun deleteLog(userName: String, surveyLogId: Int)

    fun deleteAllLogsOfUser(userName: String)

}
