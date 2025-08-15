package at.orchaldir.gm.core.model.time.holiday

import at.orchaldir.gm.core.model.realm.CatastropheId
import at.orchaldir.gm.core.model.realm.TreatyId
import at.orchaldir.gm.core.model.realm.WarId
import at.orchaldir.gm.core.model.religion.GodId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class HolidayPurposeType {
    Anniversary,
    Catastrophe,
    Fasting,
    Festival,
    God,
    War,
    Treaty,
}

@Serializable
sealed class HolidayPurpose {

    fun getType() = when (this) {
        Anniversary -> HolidayPurposeType.Anniversary
        Fasting -> HolidayPurposeType.Fasting
        Festival -> HolidayPurposeType.Festival
        is HolidayOfCatastrophe -> HolidayPurposeType.Catastrophe
        is HolidayOfGod -> HolidayPurposeType.God
        is HolidayOfTreaty -> HolidayPurposeType.Treaty
        is HolidayOfWar -> HolidayPurposeType.War
    }

}

@Serializable
@SerialName("Anniversary")
data object Anniversary : HolidayPurpose()

@Serializable
@SerialName("Fasting")
data object Fasting : HolidayPurpose()

@Serializable
@SerialName("Festival")
data object Festival : HolidayPurpose()

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

@Serializable
@SerialName("War")
data class HolidayOfWar(
    val war: WarId,
) : HolidayPurpose()