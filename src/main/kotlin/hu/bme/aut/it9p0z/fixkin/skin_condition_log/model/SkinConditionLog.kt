package hu.bme.aut.it9p0z.fixkin.skin_condition_log.model

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document(collection = "skin condition logs")
data class SkinConditionLog(
    @Id val scLogId: Int,
    val userName: String?,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
    val creationDate: LocalDate,
    var feeling: String,
    var triggers: HashMap<String,Boolean>,
)

fun SkinConditionLog.hideUsername() = SkinConditionLog(
    scLogId = scLogId,
    userName = null,
    creationDate = creationDate,
    feeling = feeling,
    triggers = triggers,
)


