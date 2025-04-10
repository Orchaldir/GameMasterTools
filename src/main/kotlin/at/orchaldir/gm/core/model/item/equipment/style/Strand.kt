package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Size
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class StrandType {
    Chain,
    Ornament,
    Wire,
}

@Serializable
sealed class Strand {

    fun getType() = when (this) {
        is Chain -> StrandType.Chain
        is OrnamentChain -> StrandType.Ornament
        is Wire -> StrandType.Wire
    }

    fun getSizeOfSub() = when (this) {
        is Chain -> thickness
        is OrnamentChain -> size
        is Wire -> thickness
    }

    fun contains(id: MaterialId) = when (this) {
        is Chain -> material == id
        is OrnamentChain -> ornament.contains(id)
        is Wire -> material == id
    }

    fun getMaterials() = when (this) {
        is Chain -> setOf(material)
        is OrnamentChain -> ornament.getMaterials()
        is Wire -> setOf(material)
    }
}

@Serializable
@SerialName("Chain")
data class Chain(
    val thickness: Size = Size.Medium,
    val color: Color = Color.Gold,
    val material: MaterialId = MaterialId(0),
) : Strand()

@Serializable
@SerialName("Ornament")
data class OrnamentChain(
    val ornament: Ornament,
    val size: Size = Size.Medium,
) : Strand()

@Serializable
@SerialName("Drop")
data class Wire(
    val thickness: Size = Size.Medium,
    val color: Color = Color.Gold,
    val material: MaterialId = MaterialId(0),
) : Strand()

