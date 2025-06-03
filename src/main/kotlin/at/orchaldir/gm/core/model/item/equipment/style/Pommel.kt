package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class PommelType {
    None,
    Ornament,
}

@Serializable
sealed class Pommel : MadeFromParts {

    fun getType() = when (this) {
        NoPommel -> PommelType.None
        is PommelWithOrnament -> PommelType.Ornament
    }

    override fun parts() = when (this) {
        NoPommel -> emptyList()
        is PommelWithOrnament -> ornament.parts()
    }
}

@Serializable
@SerialName("None")
data object NoPommel : Pommel()

@Serializable
data class PommelWithOrnament(
    val ornament: Ornament = SimpleOrnament(),
    val size: Size = Size.Medium,
) : Pommel()
