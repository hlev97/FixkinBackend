package hu.bme.aut.it9p0z.fixkin.skin_condition_log.controller

import hu.bme.aut.it9p0z.fixkin.skin_condition_log.model.SkinConditionLog
import hu.bme.aut.it9p0z.fixkin.skin_condition_log.model.ConditionLogStatistics
import hu.bme.aut.it9p0z.fixkin.skin_condition_log.repository.SkinConditionLogOperationsService
import hu.bme.aut.it9p0z.fixkin.user.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/skin_condition_logs")
class SkinConditionLogController @Autowired constructor(
    private val operationsService: SkinConditionLogOperationsService
) {
    @GetMapping("/all")
    @Secured(User.ROLE_ADMIN)
    fun getAllLogs(): ResponseEntity<List<SkinConditionLog>> = operationsService.getAllLogs()

    @PostMapping
    @Secured(User.ROLE_USER)
    fun addNewLog(
        @RequestBody log: SkinConditionLog
    ): ResponseEntity<SkinConditionLog> {
        val auth: Authentication = SecurityContextHolder.getContext().authentication
        return operationsService.insertLog(auth.name, log)
    }

    @PutMapping("/{scLogId}")
    @Secured(User.ROLE_USER)
    fun updateLog(
        @PathVariable("scLogId") scLogId: Int,
        @RequestBody log: SkinConditionLog
    ): ResponseEntity<SkinConditionLog> {
        val auth: Authentication = SecurityContextHolder.getContext().authentication
        return operationsService.updateLog(auth.name, scLogId, log)
    }

    @GetMapping("/all/me")
    @Secured(User.ROLE_USER)
    fun getAllLogsByUser(): ResponseEntity<List<SkinConditionLog>> {
        val auth: Authentication = SecurityContextHolder.getContext().authentication
        return operationsService.getAllLogsByUser(auth.name)
    }

    @DeleteMapping("/{scLogId}")
    @Secured(User.ROLE_USER)
    fun deleteLogById(
        @PathVariable("scLogId") scLogId: Int
    ): Any {
        val auth: Authentication = SecurityContextHolder.getContext().authentication
        return operationsService.deleteLog(auth.name, scLogId)
    }

    @GetMapping("statistics/me")
    @Secured(User.ROLE_USER)
    fun getStatistics(): ResponseEntity<ConditionLogStatistics> {
        val auth: Authentication = SecurityContextHolder.getContext().authentication
        return operationsService.getStatistics(auth.name)
    }

    @GetMapping("creationDates/me")
    @Secured(User.ROLE_USER)
    fun getDates(): ResponseEntity<List<LocalDate>> {
        val auth: Authentication = SecurityContextHolder.getContext().authentication
        return operationsService.getCreationDates(auth.name)
    }

    @GetMapping("last/me")
    @Secured(User.ROLE_USER)
    fun getLastLog(): ResponseEntity<SkinConditionLog> {
        val auth: Authentication = SecurityContextHolder.getContext().authentication
        return operationsService.getLastLogFromUser(auth.name)
    }
    
}
