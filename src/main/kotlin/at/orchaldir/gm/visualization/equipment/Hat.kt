package at.orchaldir.gm.visualization.equipment

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.item.Hat
import at.orchaldir.gm.core.model.item.HatStyle
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Polygon2dBuilder
import at.orchaldir.gm.utils.math.START
import at.orchaldir.gm.utils.renderer.FillAndBorder
import at.orchaldir.gm.visualization.RenderState
import at.orchaldir.gm.visualization.character.EQUIPMENT_LAYER

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
    when (hat.style) {
        HatStyle.Beanie -> doNothing()
        HatStyle.TopHat -> visualizeTopHat(state, head, hat)
    }
}

fun visualizeTopHat(
    state: RenderState,
    head: Head,
    hat: Hat,
) {
    visualizeTopHadCrown(hat, state)
    visualizeTopHadBrim(hat, state)
}

private fun visualizeTopHadCrown(hat: Hat, state: RenderState) {
    val options = FillAndBorder(hat.color.toRender(), state.config.line)

    val builder = Polygon2dBuilder()

    builder.addMirroredPoints(state.aabb, FULL, state.config.head.hairlineY)
    builder.addMirroredPoints(state.aabb, FULL + Factor(0.1f), START - Factor(0.6f))

    val polygon = builder.build()

    state.renderer.renderPolygon(polygon, options, EQUIPMENT_LAYER)
}

private fun visualizeTopHadBrim(hat: Hat, state: RenderState) {
    val options = FillAndBorder(hat.color.toRender(), state.config.line)

    val builder = Polygon2dBuilder()
    val width = Factor(1.5f)
    val height = Factor(0.1f)
    val half = height * 0.5f

    builder.addMirroredPoints(state.aabb, width, state.config.head.hairlineY + half)
    builder.addMirroredPoints(state.aabb, width, state.config.head.hairlineY - half)

    val polygon = builder.build()

    state.renderer.renderPolygon(polygon, options, EQUIPMENT_LAYER)
}

