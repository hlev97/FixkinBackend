package hu.bme.aut.it9p0z.fixkin.survey_log.controller

import hu.bme.aut.it9p0z.fixkin.survey_log.model.SurveyLog
import hu.bme.aut.it9p0z.fixkin.survey_log.repository.SurveyLogOperationsService
import hu.bme.aut.it9p0z.fixkin.user.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException.Unauthorized

@RestController
@RequestMapping("/survey_logs")
class SurveyLogController @Autowired constructor(
    private val operationsService: SurveyLogOperationsService
) {

    @GetMapping("/all")
    @Secured(User.ROLE_ADMIN)
    fun getAllLogs(): ResponseEntity<List<SurveyLog>> = operationsService.getAllLogs()

    @PostMapping
    @Secured(User.ROLE_USER)
    fun addNewLog(
        @RequestBody log: SurveyLog
    ): ResponseEntity<SurveyLog> {
        val auth: Authentication = SecurityContextHolder.getContext().authentication
        return operationsService.insertLog(auth.name, log)
    }

    @PutMapping("/{surveyLogId}")
    @Secured(User.ROLE_USER)
    fun updateLog(
        @PathVariable("surveyLogId") surveyLogId: Int,
        @RequestBody log: SurveyLog
    ): ResponseEntity<SurveyLog> {
        val auth: Authentication = SecurityContextHolder.getContext().authentication
        return operationsService.updateLog(auth.name, surveyLogId, log)
    }

    @GetMapping("/all/me")
    @Secured(User.ROLE_USER)
    fun getAllLogsByUser(): ResponseEntity<List<SurveyLog>> {
        val auth: Authentication = SecurityContextHolder.getContext().authentication
        return operationsService.getAllLogsByUser(auth.name)
    }

    @GetMapping("/all/{userName}/")
    @Secured(User.ROLE_ADMIN)
    fun getAllLogsByUser(
        @PathVariable("userName") userName: String
    ): ResponseEntity<List<SurveyLog>> = operationsService.getAllLogsByUser(userName)

    @DeleteMapping("/{surveyLogId}")
    @Secured(User.ROLE_USER)
    fun deleteLogById(
        @PathVariable("surveyLogId") surveyLogId: Int
    ): ResponseEntity<Any> {
        val auth: Authentication = SecurityContextHolder.getContext().authentication
        return operationsService.deleteLog(auth.name, surveyLogId)
    }

}
