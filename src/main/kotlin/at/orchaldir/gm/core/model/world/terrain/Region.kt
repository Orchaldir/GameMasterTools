package at.orchaldir.gm.core.model.world.terrain

import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.name.Name
import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val REGION_TYPE = "Region"

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
    val name: Name = Name.init("Mountain ${id.value}"),
    val data: RegionData = UndefinedRegionData,
    val parent: RegionId? = null,
    val resources: Set<MaterialId> = emptySet(),
) : ElementWithSimpleName<RegionId> {

    override fun id() = id
    override fun name() = name.text

}