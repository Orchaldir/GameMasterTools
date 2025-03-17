package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Fill
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.Solid
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class BuckleType {
    NoBuckle,
    Simple,
}

@Serializable
sealed class Buckle {

    fun getType() = when (this) {
        NoBuckle -> BuckleType.NoBuckle
        is SimpleBuckle -> BuckleType.Simple
    }

    open fun contains(id: MaterialId) = false
    open fun getMaterials() = emptySet<MaterialId>()
}

@Serializable
@SerialName("NoBuckle")
data object NoBuckle : Buckle()

@Serializable
@SerialName("Simple")
data class SimpleBuckle(
    val shape: BuckleShape = BuckleShape.Rectangle,
    val size: Size = Size.Small,
    val fill: Fill = Solid(Color.Gray),
    val material: MaterialId = MaterialId(0),
) : Buckle() {

    override fun contains(id: MaterialId) = material == id
    override fun getMaterials() = setOf(material)
}
