package at.orchaldir.gm.core.model.name

import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.world.moon.MoonId
import at.orchaldir.gm.core.model.world.terrain.MountainId
import at.orchaldir.gm.core.model.world.terrain.RiverId
import at.orchaldir.gm.core.model.world.town.TownId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class ReferenceForNameType {
    FamilyName,
    FullName,
    Moon,
    Mountain,
    River,
    Town,
}

@Serializable
sealed class ReferenceForName {

    fun getId() = when (this) {
        is ReferencedFamilyName -> id
        is ReferencedFullName -> id
        is ReferencedMoon -> id
        is ReferencedMountain -> id
        is ReferencedRiver -> id
        is ReferencedTown -> id
    }

    fun getType() = when (this) {
        is ReferencedFamilyName -> ReferenceForNameType.FamilyName
        is ReferencedFullName -> ReferenceForNameType.FullName
        is ReferencedMoon -> ReferenceForNameType.Moon
        is ReferencedMountain -> ReferenceForNameType.Mountain
        is ReferencedRiver -> ReferenceForNameType.River
        is ReferencedTown -> ReferenceForNameType.Town
    }
}

@Serializable
@SerialName("FullName")
data class ReferencedFullName(val id: CharacterId) : ReferenceForName()

@Serializable
@SerialName("FamilyName")
data class ReferencedFamilyName(val id: CharacterId) : ReferenceForName()

@Serializable
@SerialName("Moon")
data class ReferencedMoon(val id: MoonId) : ReferenceForName()

@Serializable
@SerialName("Mountain")
data class ReferencedMountain(val id: MountainId) : ReferenceForName()

@Serializable
@SerialName("River")
data class ReferencedRiver(val id: RiverId) : ReferenceForName()

@Serializable
@SerialName("Town")
data class ReferencedTown(val id: TownId) : ReferenceForName()
