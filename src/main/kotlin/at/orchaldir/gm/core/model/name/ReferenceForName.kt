package at.orchaldir.gm.core.model.name

import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.world.moon.MoonId
import at.orchaldir.gm.core.model.world.terrain.Mountain
import at.orchaldir.gm.core.model.world.terrain.RiverId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class ReferenceForName

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
data class ReferencedMountain(val id: Mountain) : ReferenceForName()

@Serializable
@SerialName("River")
data class ReferencedRiver(val id: RiverId) : ReferenceForName()

@Serializable
@SerialName("River")
data class ReferencedTown(val id: RiverId) : ReferenceForName()
