package at.orchaldir.gm.core.model.world.building

import at.orchaldir.gm.core.model.name.Name
import at.orchaldir.gm.core.model.time.date.Year
import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.HasStartDate
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val ARCHITECTURAL_STYLE_TYPE = "Architectural Style"

@JvmInline
@Serializable
value class ArchitecturalStyleId(val value: Int) : Id<ArchitecturalStyleId> {

    override fun next() = ArchitecturalStyleId(value + 1)
    override fun type() = ARCHITECTURAL_STYLE_TYPE
    override fun value() = value

}

@Serializable
data class ArchitecturalStyle(
    val id: ArchitecturalStyleId,
    val name: Name = Name.init("Architectural Style ${id.value}"),
    val start: Year? = null,
    val end: Year? = null,
    val revival: ArchitecturalStyleId? = null,
) : ElementWithSimpleName<ArchitecturalStyleId>, HasStartDate {

    override fun id() = id
    override fun name() = name.text
    override fun startDate() = start

}