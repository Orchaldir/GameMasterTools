package at.orchaldir.gm.core.model.economy.material

import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.math.unit.Weight
import kotlinx.serialization.Serializable

const val MATERIAL_TYPE = "Material"

@JvmInline
@Serializable
value class MaterialId(val value: Int) : Id<MaterialId> {

    override fun next() = MaterialId(value + 1)
    override fun type() = MATERIAL_TYPE
    override fun value() = value

}

@Serializable
data class Material(
    val id: MaterialId,
    val name: Name = Name.init(id),
    val category: MaterialCategory = MaterialCategory.Metal,
    val color: Color = Color.Pink,
    val density: Weight = Weight.fromKilograms(1000),
) : ElementWithSimpleName<MaterialId> {

    override fun id() = id
    override fun name() = name.text

}