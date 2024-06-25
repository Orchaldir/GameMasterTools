package at.orchaldir.gm.core.model.item

import at.orchaldir.gm.core.model.character.CharacterId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class ItemLocation

@Serializable
@SerialName("Born")
data class InInventory(val character: CharacterId) : ItemLocation()

@Serializable
@SerialName("Undefined")
data object UndefinedItemLocation : ItemLocation()
