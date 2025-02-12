package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.organization.OrganizationId
import at.orchaldir.gm.core.model.world.town.TownId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class OwnerType {
    None,
    Undefined,
    Character,
    Organization,
    Town,
}

@Serializable
sealed class Owner {

    fun getType() = when (this) {
        NoOwner -> OwnerType.None
        is OwnedByCharacter -> OwnerType.Character
        is OwnedByTown -> OwnerType.Town
        is OwnedByOrganization -> OwnerType.Organization
        UndefinedOwner -> OwnerType.Undefined
    }

    fun canDelete() = when (this) {
        NoOwner -> true
        is OwnedByCharacter -> false
        is OwnedByOrganization -> false
        is OwnedByTown -> false
        UndefinedOwner -> true
    }

    fun contains(other: CharacterId) = this is OwnedByCharacter && character == other

    fun contains(other: TownId) = this is OwnedByTown && town == other

}

fun History<Owner>.contains(character: CharacterId) = previousEntries.any { it.entry.contains(character) }

fun History<Owner>.contains(town: TownId) = previousEntries.any { it.entry.contains(town) }

@Serializable
@SerialName("None")
data object NoOwner : Owner()

@Serializable
@SerialName("Undefined")
data object UndefinedOwner : Owner()

@Serializable
@SerialName("Character")
data class OwnedByCharacter(val character: CharacterId) : Owner()

@Serializable
@SerialName("Organization")
data class OwnedByOrganization(val organization: OrganizationId) : Owner()

@Serializable
@SerialName("Town")
data class OwnedByTown(val town: TownId) : Owner()


