package hu.bme.aut.it9p0z.fixkin.skin_condition_log.repository

import hu.bme.aut.it9p0z.fixkin.skin_condition_log.model.SkinConditionLog
import hu.bme.aut.it9p0z.fixkin.skin_condition_log.model.ConditionLogStatistics
import org.springframework.http.ResponseEntity
import java.time.LocalDate

interface SkinConditionLogOperations {

    fun getAllLogs(): ResponseEntity<List<SkinConditionLog>>

    fun getAllLogsByUser(userName: String): ResponseEntity<List<SkinConditionLog>>

    fun insertLog(userName: String, log: SkinConditionLog): ResponseEntity<SkinConditionLog>

    fun updateLog(userName: String, scLogId: Int, log: SkinConditionLog): ResponseEntity<SkinConditionLog>

    fun deleteLog(userName: String, scLogId: Int): Any

    fun deleteAllLogsOfUser(userName: String): ResponseEntity<Any>

    fun getStatistics(userName: String): ResponseEntity<ConditionLogStatistics>

    fun getCreationDates(userName: String): ResponseEntity<List<LocalDate>>

}
