package at.orchaldir.gm.core.model.world.moon

import at.orchaldir.gm.core.model.time.date.Day
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.name.NotEmptyString
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.world.plane.PlaneId
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.math.modulo
import kotlinx.serialization.Serializable

const val MOON_TYPE = "Moon"

@JvmInline
@Serializable
value class MoonId(val value: Int) : Id<MoonId> {

    override fun next() = MoonId(value + 1)
    override fun type() = MOON_TYPE
    override fun value() = value

}

@Serializable
data class Moon(
    val id: MoonId,
    val name: Name = Name.init("Moon ${id.value}"),
    val title: NotEmptyString? = null,
    val daysPerQuarter: Int = 1,
    val color: Color = Color.White,
    val plane: PlaneId? = null,
) : ElementWithSimpleName<MoonId> {

    override fun id() = id
    override fun name() = name.text

    fun getCycle() = daysPerQuarter * 4

    fun getPhase(date: Day): MoonPhase {
        val day = date.day.modulo(getCycle())
        val twoQuarters = daysPerQuarter * 2
        val threeQuarters = daysPerQuarter * 3

        return when {
            day == 0 -> MoonPhase.NewMoon
            day < daysPerQuarter -> MoonPhase.WaxingCrescent
            day == daysPerQuarter -> MoonPhase.FirstQuarter
            day < twoQuarters -> MoonPhase.WaxingGibbous
            day == twoQuarters -> MoonPhase.FullMoon
            day < threeQuarters -> MoonPhase.WaningGibbous
            day == threeQuarters -> MoonPhase.LastQuarter
            else -> MoonPhase.WaningCrescent
        }
    }

    fun getNextNewMoon(date: Day): Day {
        val cycle = getCycle()
        val day = date.day.modulo(cycle)

        if (day == 0) {
            return date
        }

        val diff = cycle - day

        return date + diff
    }

    fun getNextFullMoon(date: Day): Day {
        val cycle = getCycle()
        val day = date.day.modulo(cycle)
        val twoQuarters = daysPerQuarter * 2

        if (day == twoQuarters) {
            return date
        }

        val target = if (day < twoQuarters) {
            twoQuarters
        } else {
            cycle + twoQuarters
        }

        val diff = target - day

        return date + diff
    }

}