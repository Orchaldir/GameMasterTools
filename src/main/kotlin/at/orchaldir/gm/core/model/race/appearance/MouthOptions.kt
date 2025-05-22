package at.orchaldir.gm.core.model.race.appearance

import at.orchaldir.gm.core.model.character.appearance.mouth.BeakShape
import at.orchaldir.gm.core.model.character.appearance.mouth.MouthType
import at.orchaldir.gm.core.model.character.appearance.mouth.SnoutShape
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.util.OneOf
import kotlinx.serialization.Serializable

val DEFAULT_BEAK_COLOR = Color.Yellow
val DEFAULT_SNOUT_COLOR = Color.Pink

@Serializable
data class MouthOptions(
    val beakColors: OneOf<Color> = OneOf(DEFAULT_BEAK_COLOR),
    val beakShapes: OneOf<BeakShape> = OneOf(BeakShape.entries),
    val mouthTypes: OneOf<MouthType> = OneOf(MouthType.NormalMouth),
    val snoutColors: OneOf<Color> = OneOf(DEFAULT_SNOUT_COLOR),
    val snoutShapes: OneOf<SnoutShape> = OneOf(SnoutShape.entries),
)