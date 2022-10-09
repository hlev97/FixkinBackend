package hu.bme.aut.it9p0z.fixkin.survey_log.model

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document("surveys")
data class SurveyLog(
    val surveyLogId: Int,
    val userName: String?,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
    @Id val creationDate: LocalDate,
    val result: Double,
)

fun SurveyLog.hideUsername() = SurveyLog(
    surveyLogId = surveyLogId,
    userName = null,
    creationDate = creationDate,
    result = result
)
