package at.orchaldir.gm.core.model.world.terrain

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.util.HasPosition
import at.orchaldir.gm.core.model.util.Position
import at.orchaldir.gm.core.model.util.PositionType
import at.orchaldir.gm.core.model.util.UndefinedPosition
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.reducer.util.checkPosition
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing
import kotlinx.serialization.Serializable

const val REGION_TYPE = "Region"
val ALLOWED_CONTINENT_POSITIONS = listOf(
    PositionType.Undefined,
    PositionType.Moon,
    PositionType.Plane,
    PositionType.World,
)
val ALLOWED_REGION_POSITIONS = ALLOWED_CONTINENT_POSITIONS + PositionType.Region

@JvmInline
@Serializable
value class RegionId(val value: Int) : Id<RegionId> {

    override fun next() = RegionId(value + 1)
    override fun type() = REGION_TYPE

    override fun value() = value

}

@Serializable
data class Region(
    val id: RegionId,
    val name: Name = Name.init(id),
    val data: RegionData = UndefinedRegionData,
    val position: Position = UndefinedPosition,
    val resources: Set<MaterialId> = emptySet(),
) : ElementWithSimpleName<RegionId>, HasPosition {

    override fun id() = id
    override fun name() = name.text
    override fun position() = position

    override fun validate(state: State) {
        when (data) {
            is Battlefield -> state.getBattleStorage().requireOptional(data.battle)
            Continent, Desert, Forrest, Lake, Plains, Mountain, Sea, UndefinedRegionData -> doNothing()
            is Wasteland -> state.getCatastropheStorage().requireOptional(data.catastrophe)
        }

        checkPosition(state, position, "position", null, data.getAllowedRegionTypes())
        state.getMaterialStorage().require(resources)
    }

}