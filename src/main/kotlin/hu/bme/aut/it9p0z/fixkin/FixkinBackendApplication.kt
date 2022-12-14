package hu.bme.aut.it9p0z.fixkin

import hu.bme.aut.it9p0z.fixkin.skin_condition_log.model.SkinConditionLog
import hu.bme.aut.it9p0z.fixkin.skin_condition_log.repository.SkinConditionLogOperationsService
import hu.bme.aut.it9p0z.fixkin.survey_log.model.SurveyLog
import hu.bme.aut.it9p0z.fixkin.survey_log.repository.SurveyLogOperationsService
import hu.bme.aut.it9p0z.fixkin.user.details.UserMongoRepository
import hu.bme.aut.it9p0z.fixkin.user.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDate

@SpringBootApplication
class FixkinBackendApplication @Autowired constructor(
	private val passwordEncoder: PasswordEncoder,
	private val userRepository: UserMongoRepository,
	private val conditionLogService: SkinConditionLogOperationsService,
	private val surveyLogService: SurveyLogOperationsService
) : CommandLineRunner {

	override fun run(vararg args: String?) {
		addUser()
		addConditionLogs()
		addSurveyLogs()
	}

	private fun addUser() {
		val user = User(
			userName = "hlev97",
			fullName = "Levente Heizer",
			height = 178.0,
			weight = 74.0,
			averageLifeQualityIndex = null,
			diseases = arrayListOf("inverse psoriasis", "plaque psoriasis"),
			medicines = arrayListOf("methotrexate"),
			roles = mutableListOf(User.ROLE_USER, User.ROLE_ADMIN),
			password = passwordEncoder.encode("password"),
			enabled = true
		)

		userRepository.save(user)
	}

	private fun addConditionLogs() {
		val logs = listOf(
			SkinConditionLog(
				id = 1,
				scLogId = 1,
				userName = "hlev97",
				creationDate = LocalDate.of(2022, 12, 4),
				feeling = "sad",
				foodTriggers = hashMapOf(
					"wheat" to true,
					"milk" to false,
					"egg" to true,
					"sea food" to false,
					"nightshade" to true,
					"soy" to false,
					"citrus" to false,
					"fast food" to true,
					"fatty food" to true,
					"alcohol" to false
				),
				weatherTriggers = hashMapOf(
					"hot" to false,
					"dry" to false,
					"cold" to true,
					"rainy" to true,
					"windy" to true,
					"snowy" to false
				),
				mentalHealthTriggers = hashMapOf(
					"anxiety" to false,
					"depression" to true,
					"insomnia" to false
				),
				otherTriggers = hashMapOf(
					"medicine" to true,
					"infection" to false,
					"sweat" to false,
					"smoking" to true
				)
			),
			SkinConditionLog(
				id = 2,
				scLogId = 2,
				userName = "hlev97",
				creationDate = LocalDate.of(2022, 12, 5),
				feeling = "unhappy",
				foodTriggers = hashMapOf(
					"wheat" to false,
					"milk" to true,
					"egg" to true,
					"sea food" to false,
					"nightshade" to true,
					"soy" to true,
					"citrus" to false,
					"fast food" to false,
					"fatty food" to false,
					"alcohol" to false
				),
				weatherTriggers = hashMapOf(
					"hot" to false,
					"dry" to false,
					"cold" to true,
					"rainy" to true,
					"windy" to true,
					"snowy" to false
				),
				mentalHealthTriggers = hashMapOf(
					"anxiety" to false,
					"depression" to true,
					"insomnia" to false
				),
				otherTriggers = hashMapOf(
					"medicine" to true,
					"infection" to false,
					"sweat" to false,
					"smoking" to true
				)
			),
			SkinConditionLog(
				id = 3,
				scLogId = 3,
				userName = "hlev97",
				creationDate = LocalDate.of(2022, 12, 6),
				feeling = "neutral",
				foodTriggers = hashMapOf(
					"wheat" to true,
					"milk" to true,
					"egg" to true,
					"sea food" to false,
					"nightshade" to false,
					"soy" to false,
					"citrus" to false,
					"fast food" to false,
					"fatty food" to false,
					"alcohol" to false
				),
				weatherTriggers = hashMapOf(
					"hot" to false,
					"dry" to false,
					"cold" to true,
					"rainy" to true,
					"windy" to true,
					"snowy" to false
				),
				mentalHealthTriggers = hashMapOf(
					"anxiety" to false,
					"depression" to true,
					"insomnia" to false
				),
				otherTriggers = hashMapOf(
					"medicine" to true,
					"infection" to false,
					"sweat" to false,
					"smoking" to true
				)
			),
			SkinConditionLog(
				id = 4,
				scLogId = 4,
				userName = "hlev97",
				creationDate = LocalDate.of(2022, 12, 7),
				feeling = "neutral",
				foodTriggers = hashMapOf(
					"wheat" to false,
					"milk" to false,
					"egg" to true,
					"sea food" to true,
					"nightshade" to false,
					"soy" to false,
					"citrus" to false,
					"fast food" to false,
					"fatty food" to false,
					"alcohol" to false
				),
				weatherTriggers = hashMapOf(
					"hot" to false,
					"dry" to false,
					"cold" to true,
					"rainy" to false,
					"windy" to true,
					"snowy" to false
				),
				mentalHealthTriggers = hashMapOf(
					"anxiety" to false,
					"depression" to false,
					"insomnia" to false
				),
				otherTriggers = hashMapOf(
					"medicine" to false,
					"infection" to false,
					"sweat" to false,
					"smoking" to true
				)
			),
			SkinConditionLog(
				id = 5,
				scLogId = 5,
				userName = "hlev97",
				creationDate = LocalDate.of(2022, 12, 8),
				feeling = "unhappy",
				foodTriggers = hashMapOf(
					"wheat" to true,
					"milk" to true,
					"egg" to true,
					"sea food" to false,
					"nightshade" to false,
					"soy" to false,
					"citrus" to false,
					"fast food" to false,
					"fatty food" to true,
					"alcohol" to false
				),
				weatherTriggers = hashMapOf(
					"hot" to false,
					"dry" to false,
					"cold" to false,
					"rainy" to false,
					"windy" to false,
					"snowy" to false
				),
				mentalHealthTriggers = hashMapOf(
					"anxiety" to false,
					"depression" to false,
					"insomnia" to true
				),
				otherTriggers = hashMapOf(
					"medicine" to false,
					"infection" to false,
					"sweat" to false,
					"smoking" to true
				)
			),
			SkinConditionLog(
				id = 6,
				scLogId = 6,
				userName = "hlev97",
				creationDate = LocalDate.of(2022, 12, 9),
				feeling = "unhappy",
				foodTriggers = hashMapOf(
					"wheat" to true,
					"milk" to true,
					"egg" to true,
					"sea food" to false,
					"nightshade" to false,
					"soy" to false,
					"citrus" to false,
					"fast food" to false,
					"fatty food" to true,
					"alcohol" to false
				),
				weatherTriggers = hashMapOf(
					"hot" to false,
					"dry" to false,
					"cold" to false,
					"rainy" to false,
					"windy" to false,
					"snowy" to false
				),
				mentalHealthTriggers = hashMapOf(
					"anxiety" to false,
					"depression" to false,
					"insomnia" to true
				),
				otherTriggers = hashMapOf(
					"medicine" to false,
					"infection" to false,
					"sweat" to false,
					"smoking" to true
				)
			),
			SkinConditionLog(
				id = 7,
				scLogId = 7,
				userName = "hlev97",
				creationDate = LocalDate.of(2022, 12, 10),
				feeling = "unhappy",
				foodTriggers = hashMapOf(
					"wheat" to true,
					"milk" to true,
					"egg" to true,
					"sea food" to false,
					"nightshade" to false,
					"soy" to false,
					"citrus" to false,
					"fast food" to false,
					"fatty food" to true,
					"alcohol" to false
				),
				weatherTriggers = hashMapOf(
					"hot" to false,
					"dry" to false,
					"cold" to false,
					"rainy" to false,
					"windy" to false,
					"snowy" to false
				),
				mentalHealthTriggers = hashMapOf(
					"anxiety" to false,
					"depression" to false,
					"insomnia" to true
				),
				otherTriggers = hashMapOf(
					"medicine" to false,
					"infection" to false,
					"sweat" to false,
					"smoking" to true
				)
			),
			SkinConditionLog(
				id = 8,
				scLogId = 8,
				userName = "hlev97",
				creationDate = LocalDate.of(2022, 12, 11),
				feeling = "unhappy",
				foodTriggers = hashMapOf(
					"wheat" to true,
					"milk" to true,
					"egg" to true,
					"sea food" to false,
					"nightshade" to false,
					"soy" to false,
					"citrus" to false,
					"fast food" to false,
					"fatty food" to true,
					"alcohol" to false
				),
				weatherTriggers = hashMapOf(
					"hot" to false,
					"dry" to false,
					"cold" to false,
					"rainy" to false,
					"windy" to false,
					"snowy" to false
				),
				mentalHealthTriggers = hashMapOf(
					"anxiety" to false,
					"depression" to false,
					"insomnia" to true
				),
				otherTriggers = hashMapOf(
					"medicine" to false,
					"infection" to false,
					"sweat" to false,
					"smoking" to true
				)
			),
			SkinConditionLog(
				id = 9,
				scLogId = 9,
				userName = "hlev97",
				creationDate = LocalDate.of(2022, 12, 12),
				feeling = "unhappy",
				foodTriggers = hashMapOf(
					"wheat" to true,
					"milk" to true,
					"egg" to true,
					"sea food" to false,
					"nightshade" to false,
					"soy" to false,
					"citrus" to false,
					"fast food" to false,
					"fatty food" to true,
					"alcohol" to false
				),
				weatherTriggers = hashMapOf(
					"hot" to false,
					"dry" to false,
					"cold" to false,
					"rainy" to false,
					"windy" to false,
					"snowy" to false
				),
				mentalHealthTriggers = hashMapOf(
					"anxiety" to false,
					"depression" to false,
					"insomnia" to true
				),
				otherTriggers = hashMapOf(
					"medicine" to false,
					"infection" to false,
					"sweat" to false,
					"smoking" to true
				)
			),
			SkinConditionLog(
				id = 10,
				scLogId = 10,
				userName = "hlev97",
				creationDate = LocalDate.of(2022, 12, 13),
				feeling = "unhappy",
				foodTriggers = hashMapOf(
					"wheat" to true,
					"milk" to true,
					"egg" to true,
					"sea food" to false,
					"nightshade" to false,
					"soy" to false,
					"citrus" to false,
					"fast food" to false,
					"fatty food" to true,
					"alcohol" to false
				),
				weatherTriggers = hashMapOf(
					"hot" to false,
					"dry" to false,
					"cold" to false,
					"rainy" to false,
					"windy" to false,
					"snowy" to false
				),
				mentalHealthTriggers = hashMapOf(
					"anxiety" to false,
					"depression" to false,
					"insomnia" to true
				),
				otherTriggers = hashMapOf(
					"medicine" to false,
					"infection" to false,
					"sweat" to false,
					"smoking" to true
				)
			),
			SkinConditionLog(
				id = 11,
				scLogId = 11,
				userName = "hlev97",
				creationDate = LocalDate.of(2022, 12, 14),
				feeling = "unhappy",
				foodTriggers = hashMapOf(
					"wheat" to true,
					"milk" to true,
					"egg" to true,
					"sea food" to false,
					"nightshade" to false,
					"soy" to false,
					"citrus" to false,
					"fast food" to false,
					"fatty food" to true,
					"alcohol" to false
				),
				weatherTriggers = hashMapOf(
					"hot" to false,
					"dry" to false,
					"cold" to false,
					"rainy" to false,
					"windy" to false,
					"snowy" to false
				),
				mentalHealthTriggers = hashMapOf(
					"anxiety" to false,
					"depression" to false,
					"insomnia" to true
				),
				otherTriggers = hashMapOf(
					"medicine" to false,
					"infection" to false,
					"sweat" to false,
					"smoking" to true
				)
			)
		)
		for (log in logs) {
			conditionLogService.insertLog("hlev97",log)
		}
	}

	private fun addSurveyLogs() {
		val logs = listOf(
			SurveyLog(
				id = 1,
				surveyLogId = 1,
				userName = "hlev97",
				creationDate = LocalDate.of(2022,12,1),
				result = 14.0
			),
			SurveyLog(
				id = 2,
				surveyLogId = 2,
				userName = "hlev97",
				creationDate = LocalDate.of(2022,12,2),
				result = 12.0
			),
			SurveyLog(
				id = 3,
				surveyLogId = 3,
				userName = "hlev97",
				creationDate = LocalDate.of(2022,12,3),
				result = 13.0
			),
			SurveyLog(
				id = 4,
				surveyLogId = 4,
				userName = "hlev97",
				creationDate = LocalDate.of(2022,12,4),
				result = 15.0
			),
			SurveyLog(
				id = 5,
				surveyLogId = 5,
				userName = "hlev97",
				creationDate = LocalDate.of(2022,12,5),
				result = 12.0
			)
		)
		for (log in logs) {
			surveyLogService.insertLog("hlev97",log)
		}
	}

}

fun main(args: Array<String>) {
	runApplication<FixkinBackendApplication>(*args)
}
