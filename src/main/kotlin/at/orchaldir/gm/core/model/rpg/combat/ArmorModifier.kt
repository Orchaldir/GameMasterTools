package at.orchaldir.gm.core.model.rpg.combat

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing
import kotlinx.serialization.Serializable

const val ARMOR_MODIFIER_TYPE = "Armor Modifier"

@JvmInline
@Serializable
value class ArmorModifierId(val value: Int) : Id<ArmorModifierId> {

    override fun next() = ArmorModifierId(value + 1)
    override fun type() = ARMOR_MODIFIER_TYPE
    override fun value() = value

}

@Serializable
data class ArmorModifier(
    val id: ArmorModifierId,
    val name: Name = Name.init("$ARMOR_MODIFIER_TYPE ${id.value}"),
) : ElementWithSimpleName<ArmorModifierId> {

    override fun id() = id
    override fun name() = name.text

    override fun validate(state: State) = doNothing()
}