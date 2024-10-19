package at.orchaldir.gm.core.model.world.building

import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.time.Year
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val ARCHITECTURAL_STYLE = "Architectural Style"

@JvmInline
@Serializable
value class ArchitecturalStyleId(val value: Int) : Id<ArchitecturalStyleId> {

    override fun next() = ArchitecturalStyleId(value + 1)
    override fun type() = ARCHITECTURAL_STYLE
    override fun value() = value

}

@Serializable
data class ArchitecturalStyle(
    val id: ArchitecturalStyleId,
    val name: String = "ArchitecturalStyle ${id.value}",
    val startDate: Date = Year(0),
    val endDate: Date? = null,
    val revival: ArchitecturalStyleId? = null,
) : Element<ArchitecturalStyleId> {

    override fun id() = id
    override fun name() = name

}