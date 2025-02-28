package at.orchaldir.gm.core.model.holiday

import at.orchaldir.gm.core.model.religion.GodId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class HolidayPurposeType {
    Anniversary,
    God,
}

@Serializable
sealed class HolidayPurpose {

    fun getType() = when (this) {
        Anniversary -> HolidayPurposeType.Anniversary
        is HolidayOfGod -> HolidayPurposeType.God
    }

}

@Serializable
@SerialName("Anniversary")
data object Anniversary : HolidayPurpose()

@Serializable
@SerialName("God")
data class HolidayOfGod(
    val god: GodId,
) : HolidayPurpose()