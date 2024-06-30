package at.orchaldir.gm.core.model.item

import at.orchaldir.gm.core.model.character.CharacterId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class ItemLocationType {
    Inventory,
    Undefined,
}

@Serializable
sealed class ItemLocation

@Serializable
@SerialName("Inventory")
data class InInventory(val character: CharacterId) : ItemLocation()

@Serializable
@SerialName("Undefined")
data object UndefinedItemLocation : ItemLocation()
