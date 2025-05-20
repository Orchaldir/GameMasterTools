package at.orchaldir.gm.core.model.realm

import at.orchaldir.gm.core.model.time.date.Date
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class WarStatusType {
    Ongoing,
    Catastrophe,
    Finished,
}

@Serializable
sealed class WarStatus {

    fun getType() = when (this) {
        OngoingWar -> WarStatusType.Ongoing
        is InterruptedByCatastrophe -> WarStatusType.Catastrophe
        is FinishedWar -> WarStatusType.Finished
    }

    fun endDate() = when (this) {
        OngoingWar -> null
        is FinishedWar -> date
        is InterruptedByCatastrophe -> date
    }

}

@Serializable
@SerialName("Ongoing")
data object OngoingWar : WarStatus()

@Serializable
@SerialName("Catastrophe")
data class InterruptedByCatastrophe(
    val catastrophe: CatastropheId,
    val treaty: TreatyId?,
    val date: Date? = null,
) : WarStatus()


@Serializable
@SerialName("Finished")
data class FinishedWar(
    val result: WarResult,
    val date: Date? = null,
) : WarStatus()
