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

    fun <ID : Id<ID>> believesIn(id: ID) = when (this) {
        is WorshipOfGod -> god == id
        is WorshipOfPantheon -> pantheon == id
        else -> false
    }

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

fun <ID : Id<ID>> History<BeliefStatus>.believesIn(id: ID) = current.believesIn(id)
fun <ID : Id<ID>> History<BeliefStatus>.believedIn(id: ID) = previousEntries.any { it.entry.believesIn(id) }
fun <ID : Id<ID>> History<BeliefStatus>.believesOrBelievedIn(id: ID) = believedIn(id) || believedIn(id)