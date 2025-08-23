package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.core.model.religion.GodId
import at.orchaldir.gm.core.model.religion.PantheonId
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class BeliefStatusType {
    Undefined,
    Atheist,
    God,
    Pantheon,
}

@Serializable
sealed class BeliefStatus {

    fun getType() = when (this) {
        UndefinedBeliefStatus -> BeliefStatusType.Undefined
        Atheist -> BeliefStatusType.Atheist
        is WorshipOfGod -> BeliefStatusType.God
        is WorshipOfPantheon -> BeliefStatusType.Pantheon
    }

    fun believesIn(id: GodId) = this is WorshipOfGod && god == id

    fun believesIn(id: PantheonId) = this is WorshipOfPantheon && pantheon == id

}

@Serializable
@SerialName("Undefined")
data object UndefinedBeliefStatus : BeliefStatus()

@Serializable
@SerialName("Atheist")
data object Atheist : BeliefStatus()

@Serializable
@SerialName("God")
data class WorshipOfGod(
    val god: GodId,
) : BeliefStatus()

@Serializable
@SerialName("Pantheon")
data class WorshipOfPantheon(
    val pantheon: PantheonId,
) : BeliefStatus()

fun History<BeliefStatus>.believesIn(god: GodId) = current.believesIn(god)
fun History<BeliefStatus>.believesIn(pantheon: PantheonId) = current.believesIn(pantheon)
fun History<BeliefStatus>.believedIn(god: GodId) = previousEntries.any { it.entry.believesIn(god) }
fun History<BeliefStatus>.believedIn(pantheon: PantheonId) = previousEntries.any { it.entry.believesIn(pantheon) }