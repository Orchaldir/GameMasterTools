package at.orchaldir.gm.core.model.race.appearance

import at.orchaldir.gm.core.model.character.appearance.mouth.BeakShape
import at.orchaldir.gm.core.model.character.appearance.mouth.MouthType
import at.orchaldir.gm.core.model.character.appearance.mouth.SnoutShape
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.OneOf
import kotlinx.serialization.Serializable

@Serializable
data class MouthOptions(
    val beakColors: OneOf<Color> = OneOf(Color.Yellow),
    val beakShapes: OneOf<BeakShape> = OneOf(BeakShape.entries),
    val mouthTypes: OneOf<MouthType> = OneOf(MouthType.NormalMouth),
    val snoutColors: OneOf<Color> = OneOf(Color.Pink),
    val snoutShapes: OneOf<SnoutShape> = OneOf(SnoutShape.entries),
)