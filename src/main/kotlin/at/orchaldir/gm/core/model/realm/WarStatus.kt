package at.orchaldir.gm.core.model.realm

import at.orchaldir.gm.core.model.time.date.Date
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class WarStatusType {
    Ongoing,
    Finished,
}

@Serializable
sealed class WarStatus {

    fun getType() = when (this) {
        OngoingWar -> WarStatusType.Ongoing
        is FinishedWar -> WarStatusType.Finished
    }

    fun endDate() = when (this) {
        OngoingWar -> null
        is FinishedWar -> date
    }

    fun treaty() = when (this) {
        OngoingWar -> null
        is FinishedWar -> result.treaty()
    }

    fun isEndedBy(catastrophe: CatastropheId) = when (this) {
        OngoingWar -> false
        is FinishedWar -> result.isEndedBy(catastrophe)
    }
}

@Serializable
@SerialName("Ongoing")
data object OngoingWar : WarStatus()


@Serializable
@SerialName("Finished")
data class FinishedWar(
    val result: WarResult = UndefinedWarResult,
    val date: Date? = null,
) : WarStatus()
