package at.orchaldir.gm.core.model.culture.fashion

import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.render.Color
import kotlinx.serialization.Serializable

@Serializable
data class AppearanceFashion(
    val beard: BeardFashion = BeardFashion(),
    val hair: HairFashion = HairFashion(),
    val lipColors: OneOf<Color> = OneOf(Color.Red),
)
