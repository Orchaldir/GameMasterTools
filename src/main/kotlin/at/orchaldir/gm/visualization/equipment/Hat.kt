package at.orchaldir.gm.visualization.equipment

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.item.Hat
import at.orchaldir.gm.core.model.item.HatStyle
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.renderer.FillAndBorder
import at.orchaldir.gm.visualization.RenderState

data class HatConfig(
    val heightAnkle: Factor,
    val heightKnee: Factor,
    val heightSole: Factor,
    val paddingShaft: Factor,
)

fun visualizeHat(
    state: RenderState,
    head: Head,
    hat: Hat,
) {
    val options = FillAndBorder(hat.color.toRender(), state.config.line)

    when (hat.style) {
        HatStyle.Beanie -> doNothing()
        HatStyle.TopHat -> doNothing()
    }
}
