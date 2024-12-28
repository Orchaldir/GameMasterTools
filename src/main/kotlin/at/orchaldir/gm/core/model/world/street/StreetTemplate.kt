package at.orchaldir.gm.core.model.world.street

import at.orchaldir.gm.core.model.material.MaterialCost
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val STREET_TEMPLATE = "Street Template"

@JvmInline
@Serializable
value class StreetTemplateId(val value: Int) : Id<StreetTemplateId> {

    override fun next() = StreetTemplateId(value + 1)
    override fun type() = STREET_TEMPLATE
    override fun value() = value

}

@Serializable
data class StreetTemplate(
    val id: StreetTemplateId,
    val name: String = "StreetTemplate ${id.value}",
    val color: Color = Color.Gray,
    val materialCost: MaterialCost = MaterialCost(),
) : ElementWithSimpleName<StreetTemplateId> {

    override fun id() = id
    override fun name() = name

}