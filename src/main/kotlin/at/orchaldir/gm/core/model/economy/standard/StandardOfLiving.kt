package at.orchaldir.gm.core.model.economy.standard


import at.orchaldir.gm.core.model.name.Name
import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val STANDARD_OF_TYPE = "Standard Of Living"

@JvmInline
@Serializable
value class StandardOfLivingId(val value: Int) : Id<StandardOfLivingId> {

    override fun next() = StandardOfLivingId(value + 1)
    override fun type() = STANDARD_OF_TYPE
    override fun plural() = "Standards Of Living"
    override fun value() = value

}

@Serializable
data class StandardOfLiving(
    val id: StandardOfLivingId,
    val name: Name = Name.init("Standard Of Living ${id.value}"),
) : ElementWithSimpleName<StandardOfLivingId> {

    override fun id() = id
    override fun name() = name.text

}