package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.world.town.TownId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class OwnerType {
    None,
    Unknown,
    Character,
    Town,
}

@Serializable
sealed class Owner {

    fun getType() = when (this) {
        NoOwner -> OwnerType.None
        is OwnedByCharacter -> OwnerType.Character
        is OwnedByTown -> OwnerType.Town
        UnknownOwner -> OwnerType.Unknown
    }

    fun canDelete() = when (this) {
        NoOwner -> true
        is OwnedByCharacter -> false
        is OwnedByTown -> false
        UnknownOwner -> true
    }

    fun contains(other: CharacterId) = this is OwnedByCharacter && character == other

    fun contains(other: TownId) = this is OwnedByTown && town == other

}

fun History<Owner>.contains(character: CharacterId) = previousOwners.any { it.entry.contains(character) }

fun History<Owner>.contains(town: TownId) = previousOwners.any { it.entry.contains(town) }

@Serializable
@SerialName("None")
data object NoOwner : Owner()

@Serializable
@SerialName("Unknown")
data object UnknownOwner : Owner()

@Serializable
@SerialName("Character")
data class OwnedByCharacter(val character: CharacterId) : Owner()

@Serializable
@SerialName("Town")
data class OwnedByTown(val town: TownId) : Owner()


