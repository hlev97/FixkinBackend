package hu.bme.aut.it9p0z.fixkin.skin_condition_log.model

data class ConditionLogStatistics(
    val feelings: HashMap<String,Float>,
    val foodTriggers: HashMap<String,Float>,
    val weatherTriggers: HashMap<String,Float>,
    val mentalHealthTriggers: HashMap<String,Float>,
    val otherTriggers: HashMap<String,Float>
)
