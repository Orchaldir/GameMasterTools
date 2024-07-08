package at.orchaldir.gm.core.model.material

import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class MaterialId(val value: Int) : Id<MaterialId> {

    override fun next() = MaterialId(value + 1)
    override fun value() = value

}

@Serializable
data class Material(
    val id: MaterialId,
    val name: String = "Material ${id.value}",
) : Element<MaterialId> {

    override fun id() = id

}