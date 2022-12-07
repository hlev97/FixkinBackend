package hu.bme.aut.it9p0z.fixkin

import hu.bme.aut.it9p0z.fixkin.skin_condition_log.repository.SkinConditionLogOperationsService
import hu.bme.aut.it9p0z.fixkin.survey_log.repository.SurveyLogOperationsService
import hu.bme.aut.it9p0z.fixkin.user.details.UserMongoRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder

@SpringBootTest
class FixkinBackendApplicationTests @Autowired constructor(
	private val passwordEncoder: PasswordEncoder,
	private val userRepository: UserMongoRepository,
	private val conditionLogService: SkinConditionLogOperationsService,
	private val surveyLogService: SurveyLogOperationsService
) {
	@Test
	fun contextLoads() {
		assertThat(passwordEncoder).isNotNull
		assertThat(userRepository).isNotNull
		assertThat(conditionLogService).isNotNull
		assertThat(surveyLogService).isNotNull
	}

}
