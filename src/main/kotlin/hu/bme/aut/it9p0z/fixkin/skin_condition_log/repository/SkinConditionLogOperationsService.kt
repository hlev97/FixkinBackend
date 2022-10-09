package hu.bme.aut.it9p0z.fixkin.skin_condition_log.repository

import hu.bme.aut.it9p0z.fixkin.skin_condition_log.model.SkinConditionLog
import hu.bme.aut.it9p0z.fixkin.skin_condition_log.model.ConditionLogStatistics
import hu.bme.aut.it9p0z.fixkin.skin_condition_log.model.hideUsername
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Repository
interface SkinConditionLogMongoRepository : MongoRepository<SkinConditionLog,LocalDate>

@Service
class SkinConditionLogOperationsService @Autowired constructor(
    private val repository: SkinConditionLogMongoRepository,
    private val mongoTemplate: MongoTemplate
) : SkinConditionLogOperations {

    override fun getAllLogs(): ResponseEntity<List<SkinConditionLog>> = ResponseEntity.ok(
        repository.findAll().map { log -> log.hideUsername() }
    )

    override fun getAllLogsByUser(userName: String): ResponseEntity<List<SkinConditionLog>> {
        return try {
            val query = Query()
            query.addCriteria(Criteria.where("userName").`is`(userName))
            val queryResult = mongoTemplate.find(query, SkinConditionLog::class.java)
            ResponseEntity.ok(queryResult)
        } catch (e: Exception) {
            ResponseEntity.notFound().build()
        }
    }

    override fun insertLog(userName: String, log: SkinConditionLog): ResponseEntity<SkinConditionLog> {
        val logsByUser = repository.findAll()
        val logToInsert = SkinConditionLog(
            scLogId = logsByUser.size+1,
            userName = userName,
            creationDate = log.creationDate,
            feeling = log.feeling,
            foodTriggers = log.foodTriggers,
            weatherTriggers = log.weatherTriggers,
            mentalHealthTriggers = log.mentalHealthTriggers,
            otherTriggers = log.otherTriggers
        )
        val responseLog = repository.insert(logToInsert).hideUsername()
        return ResponseEntity.ok(responseLog)
    }

    override fun updateLog(userName: String, scLogId: Int, log: SkinConditionLog): ResponseEntity<SkinConditionLog> {
        val query = Query()
        query.addCriteria(Criteria.where("scLogId").`is`(scLogId))
        val update = Update()
        update.set("feeling",log.feeling)
        update.set("foodTriggers",log.foodTriggers)
        update.set("weatherTriggers",log.weatherTriggers)
        update.set("mentalHealthTriggers",log.mentalHealthTriggers)
        update.set("otherTriggers",log.otherTriggers)
        val updateResult = mongoTemplate.updateFirst(query,update,SkinConditionLog::class.java)
        if (!updateResult.wasAcknowledged()) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build()
        }
        return ResponseEntity.ok(log)
    }

    override fun deleteLog(userName: String, scLogId: Int): ResponseEntity<Any> {
        return try {
            val query = Query()
            query.addCriteria(Criteria.where("userName").`is`(userName))
            val logs = mongoTemplate.find(query, SkinConditionLog::class.java)
            val logToDelete = logs[scLogId]
            if (logToDelete.userName == userName) {
                repository.delete(logToDelete)
                ResponseEntity.ok().build()
            } else ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.CONFLICT).build()
        }
    }

    override fun deleteAllLogsOfUser(userName: String): ResponseEntity<Any> {
        return try {
            val query = Query()
            query.addCriteria(Criteria.where("userName").`is`(userName))
            val logs = mongoTemplate.find(query, SkinConditionLog::class.java)
            repository.deleteAll(logs)
            ResponseEntity.ok().build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.CONFLICT).build()
        }
    }

    override fun getStatistics(userName: String): ResponseEntity<ConditionLogStatistics> {
        return try {
            val logs = repository.findAll()
            return ResponseEntity.ok(getStatisticsOfTriggers(logs))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.CONFLICT).build()
        }
    }

    override fun getCreationDates(userName: String): ResponseEntity<List<LocalDate>> {
        return try {
            val creationDates = repository.findAll().map { it.creationDate }
            return ResponseEntity.ok(creationDates)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.CONFLICT).build()
        }
    }

    private fun getStatisticsOfTriggers(logs: List<SkinConditionLog>): ConditionLogStatistics {
        val feelingProb = getFeelingsFrequency(logs).countTriggerProbabilities()
        val foodTriggerProb = getTriggerFrequencyByCategory(logs, TriggerCategory.FOOD).countTriggerProbabilities()
        val weatherTriggerProb = getTriggerFrequencyByCategory(logs, TriggerCategory.WEATHER).countTriggerProbabilities()
        val mentalTriggerProb = getTriggerFrequencyByCategory(logs, TriggerCategory.MENTAL).countTriggerProbabilities()
        val otherTriggerProb = getTriggerFrequencyByCategory(logs, TriggerCategory.OTHER).countTriggerProbabilities()

        return ConditionLogStatistics(
            feelings = feelingProb,
            foodTriggers = getTopTriggers(foodTriggerProb, 5),
            weatherTriggers = getTopTriggers(weatherTriggerProb, 5),
            mentalHealthTriggers = getTopTriggers(mentalTriggerProb, 5),
            otherTriggers = getTopTriggers(otherTriggerProb, 3)
        )
    }

    private fun HashMap<String,Int>.countTriggerProbabilities(): HashMap<String,Float> {
        val triggerProbability = hashMapOf<String,Float>()
        this.forEach { (key, value) ->
            triggerProbability[key] = value.toFloat() / this.values.sum()
        }
        return triggerProbability
    }

    private fun getTriggerFrequencyByCategory(logs: List<SkinConditionLog>, category: TriggerCategory): HashMap<String,Int> {
        val triggerFrequency = hashMapOf<String,Int>()

        logs.forEachIndexed { index, log ->
            when (category) {
                TriggerCategory.FOOD -> {
                    if (index == 0) {
                        for (trigger in foodTriggers) {
                            triggerFrequency[trigger.name] = 0
                        }
                    }
                    log.foodTriggers.forEach { (key, isTrigger) ->
                        if (isTrigger) {
                            triggerFrequency[key] = (triggerFrequency[key] ?: 0) + 1
                        }
                    }
                }
                TriggerCategory.WEATHER -> {
                    if (index == 0) {
                        for (trigger in weatherTriggers) {
                            triggerFrequency[trigger.name] = 0
                        }
                    }
                    log.weatherTriggers.forEach { (key, isTrigger) ->
                        if (isTrigger) {
                            triggerFrequency[key] = (triggerFrequency[key] ?: 0) + 1
                        }
                    }
                }
                TriggerCategory.MENTAL -> {
                    if (index == 0) {
                        for (trigger in mentalTriggers) {
                            triggerFrequency[trigger.name] = 0
                        }
                    }
                    log.mentalHealthTriggers.forEach { (key, isTrigger) ->
                        if (isTrigger) {
                            triggerFrequency[key] = (triggerFrequency[key] ?: 0) + 1
                        }
                    }
                }
                TriggerCategory.OTHER -> {
                    if (index == 0) {
                        for (trigger in otherTriggers) {
                            triggerFrequency[trigger.name] = 0
                        }
                    }
                    log.otherTriggers.forEach { (key, isTrigger) ->
                        if (isTrigger) {
                            triggerFrequency[key] = (triggerFrequency[key] ?: 0) + 1
                        }
                    }
                }
            }
        }
        return triggerFrequency
    }

    private fun getTopTriggers(triggers: HashMap<String,Float>, N: Int): HashMap<String,Float> {
        val topTriggers = hashMapOf<String,Float>()
        for (i in 0 until N) {
            var max = 0f
            var keyToRemove: String = ""
            if (triggers.size != 0) {
                triggers.forEach { (key, value) ->
                    if (value >= max) {
                        keyToRemove = key
                        max = value
                    }
                }
                triggers.remove(keyToRemove,max)
                topTriggers[keyToRemove] = max
            } else return topTriggers
        }
        return topTriggers
    }

    private fun getFeelingsFrequency(logs: List<SkinConditionLog>): HashMap<String,Int> {
        val feelingFrequency = hashMapOf<String,Int>()
        for (feeling in typeOfFeelings) {
            feelingFrequency[feeling.name] = 0
        }

        for (log in logs) {
            when (log.feeling) {
                Feeling.Sad.name -> {
                    feelingFrequency[Feeling.Sad.name] = (feelingFrequency[Feeling.Sad.name] ?: 0) + 1
                }
                Feeling.Unhappy.name -> {
                    feelingFrequency[Feeling.Unhappy.name] = (feelingFrequency[Feeling.Sad.name] ?: 0) + 1
                }
                Feeling.Neutral.name -> {
                    feelingFrequency[Feeling.Neutral.name] = (feelingFrequency[Feeling.Sad.name] ?: 0) + 1
                }
                Feeling.Happy.name -> {
                    feelingFrequency[Feeling.Happy.name] = (feelingFrequency[Feeling.Sad.name] ?: 0) + 1
                }
                Feeling.Joyful.name -> {
                    feelingFrequency[Feeling.Joyful.name] = (feelingFrequency[Feeling.Sad.name] ?: 0) + 1
                }
            }
        }
        return feelingFrequency
    }

    companion object {
        private enum class TriggerCategory {
            FOOD, WEATHER, MENTAL, OTHER;
        }

        private sealed class FoodTrigger(val name: String) {
            object Wheat: FoodTrigger(name = "wheat")
            object Milk: FoodTrigger(name = "milk")
            object Egg: FoodTrigger(name = "egg")
            object SeaFood: FoodTrigger(name = "sea food")
            object NightShade: FoodTrigger(name = "night shade")
            object Soy: FoodTrigger(name = "soy")
            object Citrus: FoodTrigger(name = "citrus")
            object FastFood: FoodTrigger(name = "fast food")
            object FattyFood: FoodTrigger(name = "fatty food")
            object Alcohol: FoodTrigger(name = "alcohol")
        }

        private val foodTriggers = listOf(
            FoodTrigger.Wheat,
            FoodTrigger.Milk,
            FoodTrigger.Egg,
            FoodTrigger.SeaFood,
            FoodTrigger.NightShade,
            FoodTrigger.Soy,
            FoodTrigger.Citrus,
            FoodTrigger.FastFood,
            FoodTrigger.FattyFood,
            FoodTrigger.Alcohol
        )

        private sealed class WeatherTrigger(val name: String) {
            object Hot: WeatherTrigger(name = "hot")
            object Dry: WeatherTrigger(name = "dry")
            object Cold: WeatherTrigger(name = "cold")
            object Rainy: WeatherTrigger(name = "rainy")
            object Windy: WeatherTrigger(name = "windy")
            object Snowy: WeatherTrigger(name = "snowy")
        }

        private val weatherTriggers = listOf(
            WeatherTrigger.Hot,
            WeatherTrigger.Dry,
            WeatherTrigger.Cold,
            WeatherTrigger.Rainy,
            WeatherTrigger.Windy,
            WeatherTrigger.Snowy
        )

        private sealed class MentalTrigger(val name: String) {
            object Anxiety: MentalTrigger(name = "anxiety")
            object Depression: MentalTrigger(name = "depression")
            object Insomnia: MentalTrigger(name = "insomnia")
        }

        private val mentalTriggers = listOf(
            MentalTrigger.Anxiety,
            MentalTrigger.Depression,
            MentalTrigger.Insomnia
        )

        private sealed class OtherTrigger(val name: String) {
            object Medicine: OtherTrigger(name = "medicine")
            object Infection: OtherTrigger(name = "infection")
            object Sweat: OtherTrigger(name = "sweat")
            object Smoking: OtherTrigger(name = "smoking")
        }

        private val otherTriggers = listOf(
            OtherTrigger.Medicine,
            OtherTrigger.Infection,
            OtherTrigger.Sweat,
            OtherTrigger.Smoking
        )

        private sealed class Feeling(val name: String) {
            object Sad: Feeling(name = "sad")
            object Unhappy: Feeling(name = "unhappy")
            object Neutral: Feeling(name = "neutral")
            object Happy: Feeling(name = "happy")
            object Joyful: Feeling(name = "joyful")
        }

        private val typeOfFeelings = listOf(Feeling.Sad, Feeling.Unhappy, Feeling.Neutral, Feeling.Happy, Feeling.Joyful)
    }
}
