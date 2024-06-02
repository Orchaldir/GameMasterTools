package at.orchaldir.gm.core.model.appearance

import kotlinx.serialization.Serializable

@Serializable
data class EnumRarity<T>(val map: Map<T, Rarity>) {
    constructor(values: Collection<T>) : this(values.associateWith { Rarity.Common })
}