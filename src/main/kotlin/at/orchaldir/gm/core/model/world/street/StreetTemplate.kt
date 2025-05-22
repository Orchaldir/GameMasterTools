package at.orchaldir.gm.core.model.world.street

import at.orchaldir.gm.core.model.economy.material.MaterialCost
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val STREET_TEMPLATE_TYPE = "Street Template"

@JvmInline
@Serializable
value class StreetTemplateId(val value: Int) : Id<StreetTemplateId> {

    override fun next() = StreetTemplateId(value + 1)
    override fun type() = STREET_TEMPLATE_TYPE
    override fun value() = value

}

@Serializable
data class StreetTemplate(
    val id: StreetTemplateId,
    val name: Name = Name.init("StreetTemplate ${id.value}"),
    val color: Color = Color.Gray,
    val materialCost: MaterialCost = MaterialCost(),
) : ElementWithSimpleName<StreetTemplateId> {

    override fun id() = id
    override fun name() = name.text

}