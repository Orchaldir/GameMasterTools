package at.orchaldir.gm.core.model.item

import kotlinx.serialization.Serializable

@Serializable
data class Inventory(val items: Set<ItemId> = emptySet())
