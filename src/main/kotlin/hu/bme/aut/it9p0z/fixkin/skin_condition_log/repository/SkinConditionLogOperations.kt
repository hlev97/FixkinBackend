package hu.bme.aut.it9p0z.fixkin.skin_condition_log.repository

import hu.bme.aut.it9p0z.fixkin.skin_condition_log.model.SkinConditionLog

interface SkinConditionLogOperations {

    fun getAllLogs(): List<SkinConditionLog>

    fun getAllLogsByUser(userName: String): List<SkinConditionLog>

    fun insertLog(userName: String, log: SkinConditionLog): SkinConditionLog

    fun updateLog(userName: String, scLogId: Int, log: SkinConditionLog): SkinConditionLog

    fun deleteLog(userName: String, scLogId: Int)

    fun deleteAllLogsOfUser(userName: String)

}
