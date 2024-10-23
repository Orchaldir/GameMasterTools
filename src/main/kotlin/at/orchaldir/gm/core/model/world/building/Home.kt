package at.orchaldir.gm.core.model.world.building

import at.orchaldir.gm.core.model.character.CharacterId
import kotlinx.serialization.Serializable

@Serializable
data class Home(
    val inhabitant: Set<CharacterId> = emptySet(),
)
