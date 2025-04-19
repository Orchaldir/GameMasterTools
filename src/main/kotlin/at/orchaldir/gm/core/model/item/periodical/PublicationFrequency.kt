package at.orchaldir.gm.core.model.item.periodical

import at.orchaldir.gm.core.model.time.date.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class PublicationFrequencyType {
    Daily,
    Weekly,
    Monthly,
    Yearly,
}

@Serializable
sealed class PublicationFrequency {

    fun getType() = when (this) {
        is DailyPublication -> PublicationFrequencyType.Daily
        is WeeklyPublication -> PublicationFrequencyType.Weekly
        is MonthlyPublication -> PublicationFrequencyType.Monthly
        is YearlyPublication -> PublicationFrequencyType.Yearly
    }

    fun getStartDate(): Date = when (this) {
        is DailyPublication -> start
        is WeeklyPublication -> start
        is MonthlyPublication -> start
        is YearlyPublication -> start
    }
}

@Serializable
@SerialName("Daily")
data class DailyPublication(
    val start: Day,
) : PublicationFrequency()

@Serializable
@SerialName("Weekly")
data class WeeklyPublication(
    val start: Week,
) : PublicationFrequency()

@Serializable
@SerialName("Monthly")
data class MonthlyPublication(
    val start: Month,
) : PublicationFrequency()

@Serializable
@SerialName("Yearly")
data class YearlyPublication(
    val start: Year,
) : PublicationFrequency()