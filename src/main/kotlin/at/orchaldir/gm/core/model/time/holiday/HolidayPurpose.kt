package at.orchaldir.gm.core.model.time.holiday

import at.orchaldir.gm.core.model.realm.CatastropheId
import at.orchaldir.gm.core.model.realm.TreatyId
import at.orchaldir.gm.core.model.religion.GodId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class HolidayPurposeType {
    Anniversary,
    Catastrophe,
    Treaty,
    God,
}

@Serializable
sealed class HolidayPurpose {

    fun getType() = when (this) {
        Anniversary -> HolidayPurposeType.Anniversary
        is HolidayOfTreaty -> HolidayPurposeType.Treaty
        is HolidayOfCatastrophe -> HolidayPurposeType.Catastrophe
        is HolidayOfGod -> HolidayPurposeType.God
    }

}

@SerialName("Anniversary")
data object Anniversary : HolidayPurpose()

@Serializable
@SerialName("Catastrophe")
data class HolidayOfCatastrophe(
    val catastrophe: CatastropheId,
) : HolidayPurpose()

@Serializable
@SerialName("God")
data class HolidayOfGod(
    val god: GodId,
) : HolidayPurpose()

@Serializable
@SerialName("Treaty")
data class HolidayOfTreaty(
    val treaty: TreatyId,
) : HolidayPurpose()