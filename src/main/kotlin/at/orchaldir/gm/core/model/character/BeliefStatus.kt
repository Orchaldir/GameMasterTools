package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.core.model.religion.GodId
import at.orchaldir.gm.core.model.religion.PantheonId
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
        is WorshipsGod -> BeliefStatusType.God
        is WorshipsPantheon -> BeliefStatusType.Pantheon
    }

    fun believesIn(id: GodId) = this is WorshipsGod && god == id

    fun believesIn(id: PantheonId) = this is WorshipsPantheon && pantheon == id

}

@Serializable
@SerialName("Undefined")
data object UndefinedBeliefStatus : BeliefStatus()

@Serializable
@SerialName("Atheist")
data object Atheist : BeliefStatus()

@Serializable
@SerialName("God")
data class WorshipsGod(
    val god: GodId,
) : BeliefStatus()

@Serializable
@SerialName("Pantheon")
data class WorshipsPantheon(
    val pantheon: PantheonId,
) : BeliefStatus()