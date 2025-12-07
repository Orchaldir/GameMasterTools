package at.orchaldir.gm.core.model.world.moon

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.realm.ALLOWED_CAUSES_OF_DEATH_FOR_REALM
import at.orchaldir.gm.core.model.realm.ALLOWED_VITAL_STATUS_FOR_REALM
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.time.date.Day
import at.orchaldir.gm.core.model.util.Alive
import at.orchaldir.gm.core.model.util.CauseOfDeathType
import at.orchaldir.gm.core.model.util.HasPosition
import at.orchaldir.gm.core.model.util.HasVitalStatus
import at.orchaldir.gm.core.model.util.NoReference
import at.orchaldir.gm.core.model.util.Position
import at.orchaldir.gm.core.model.util.PositionType
import at.orchaldir.gm.core.model.util.UndefinedPosition
import at.orchaldir.gm.core.model.util.VitalStatus
import at.orchaldir.gm.core.model.util.VitalStatusType
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.name.NotEmptyString
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.world.plane.PlaneId
import at.orchaldir.gm.core.reducer.util.checkPosition
import at.orchaldir.gm.core.reducer.util.validateCreator
import at.orchaldir.gm.core.reducer.util.validateHasStartAndEnd
import at.orchaldir.gm.core.reducer.util.validateVitalStatus
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.math.modulo
import kotlinx.serialization.Serializable

const val MOON_TYPE = "Moon"
val ALLOWED_VITAL_STATUS_FOR_MOON = setOf(
    VitalStatusType.Alive,
    VitalStatusType.Destroyed,
    VitalStatusType.Vanished,
)
val ALLOWED_CAUSES_OF_DEATH_FOR_MOON = setOf(
    CauseOfDeathType.Battle,
    CauseOfDeathType.Catastrophe,
    CauseOfDeathType.War,
    CauseOfDeathType.Undefined,
)
val ALLOWED_MOON_POSITIONS = setOf(
    PositionType.Undefined,
    PositionType.Plane,
    PositionType.World,
)

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
    val name: Name = Name.init(id),
    val title: NotEmptyString? = null,
    val status: VitalStatus = Alive,
    val position: Position = UndefinedPosition,
    val daysPerQuarter: Int = 1,
    val color: Color = Color.White,
    val plane: PlaneId? = null,
    val resources: Set<MaterialId> = emptySet(),
) : ElementWithSimpleName<MoonId>, HasPosition, HasVitalStatus {

    override fun id() = id
    override fun name() = name.text
    override fun position() = position
    override fun startDate() = null
    override fun vitalStatus() = status

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

    override fun validate(state: State) {
        validateVitalStatus(
            state,
            id,
            status,
            null,
            ALLOWED_VITAL_STATUS_FOR_MOON,
            ALLOWED_CAUSES_OF_DEATH_FOR_MOON,
        )
        validateHasStartAndEnd(state, this)
        state.getPlaneStorage().requireOptional(plane)
        checkPosition(state, position, "position", null, ALLOWED_MOON_POSITIONS)
        require(daysPerQuarter > 0) { "Days per quarter most be greater than 0!" }
    }

}