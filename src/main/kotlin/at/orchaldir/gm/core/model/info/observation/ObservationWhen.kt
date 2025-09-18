package at.orchaldir.gm.core.model.info.observation

import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.time.holiday.HolidayId
import at.orchaldir.gm.core.model.util.Rarity
import at.orchaldir.gm.core.model.world.moon.MoonPhase
import at.orchaldir.gm.core.model.world.plane.PlanarAlignment
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class ObservationWhenType {
    Once,
    Random,
    Holiday,
    MoonPhase,
    PlanarAlignment,
    Undefined
}

@Serializable
sealed class ObservationWhen {

    fun getType() = when (this) {
        is ObservedDuringMoonPhase -> ObservationWhenType.MoonPhase
        is ObservedDuringOnHoliday -> ObservationWhenType.Holiday
        is ObservedDuringPlanarAlignment -> ObservationWhenType.PlanarAlignment
        is ObservedOnce -> ObservationWhenType.Once
        is ObservedRandomly -> ObservationWhenType.Random
        UndefinedObservationWhen -> ObservationWhenType.Undefined
    }
}

@Serializable
@SerialName("MoonPhase")
data class ObservedDuringMoonPhase(
    val start: Date?,
    val end: Date?,
    val phase: MoonPhase = MoonPhase.FullMoon,
) : ObservationWhen()

@Serializable
@SerialName("PlanarAlignment")
data class ObservedDuringPlanarAlignment(
    val start: Date?,
    val end: Date?,
    val alignment: PlanarAlignment = PlanarAlignment.Coterminous,
) : ObservationWhen()

@Serializable
@SerialName("Holiday")
data class ObservedDuringOnHoliday(
    val start: Date?,
    val end: Date?,
    val holidays: Set<HolidayId>,
) : ObservationWhen()

@Serializable
@SerialName("Once")
data class ObservedOnce(
    val date: Date,
) : ObservationWhen()

@Serializable
@SerialName("Random")
data class ObservedRandomly(
    val start: Date?,
    val end: Date?,
    val rarity: Rarity = Rarity.Rare,
) : ObservationWhen()

@Serializable
@SerialName("Undefined")
data object UndefinedObservationWhen : ObservationWhen()



