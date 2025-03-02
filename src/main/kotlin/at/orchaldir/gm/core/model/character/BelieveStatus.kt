package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.core.model.religion.GodId
import at.orchaldir.gm.core.model.religion.PantheonId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class BelieveStatusType {
    Undefined,
    God,
    Pantheon,
}

@Serializable
sealed class BelieveStatus {

    fun getType() = when (this) {
        UndefinedBelieveStatus -> BelieveStatusType.Undefined
        is WorshipsGod -> BelieveStatusType.God
        is WorshipsPantheon -> BelieveStatusType.Pantheon
    }

}

@Serializable
@SerialName("Undefined")
data object UndefinedBelieveStatus : BelieveStatus()

@Serializable
@SerialName("God")
data class WorshipsGod(
    val god: GodId,
) : BelieveStatus()

@Serializable
@SerialName("Pantheon")
data class WorshipsPantheon(
    val pantheon: PantheonId,
) : BelieveStatus()