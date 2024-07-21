package at.orchaldir.gm.core.model.appearance

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class RarityMapPerEnum<Enum, Value> {

    abstract fun get(value: Enum): RarityMap<Value>?

}

@Serializable
@SerialName("Shared")
data class SharedRarityMap<Enum, Value>(
    private val rarityMap: RarityMap<Value>,
) : RarityMapPerEnum<Enum, Value>() {

    override fun get(value: Enum) = rarityMap

}

@Serializable
@SerialName("Individual")
data class IndividualRarityMaps<Enum, Value>(
    private val rarityMaps: Map<Enum, RarityMap<Value>>,
) : RarityMapPerEnum<Enum, Value>() {

    override fun get(value: Enum) = rarityMaps[value]

}


