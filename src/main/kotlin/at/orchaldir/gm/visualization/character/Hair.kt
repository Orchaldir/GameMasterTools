package at.orchaldir.gm.visualization.character

import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.hair.NoHair
import at.orchaldir.gm.core.model.character.appearance.hair.ShortHair
import at.orchaldir.gm.core.model.character.appearance.hair.ShortHairStyle
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.renderer.FillAndBorder
import at.orchaldir.gm.utils.renderer.Renderer
import at.orchaldir.gm.visualization.RenderConfig
import at.orchaldir.gm.visualization.renderPolygon

data class HairConfig(
    val flatTopY: Factor,
)

fun visualizeHair(renderer: Renderer, config: RenderConfig, aabb: AABB, head: Head) {
    when (head.hair) {
        NoHair -> doNothing()
        is ShortHair -> visualizeShortHair(renderer, config, aabb, head.hair)
    }
}

fun visualizeShortHair(renderer: Renderer, config: RenderConfig, aabb: AABB, shortHair: ShortHair) {
    val options = FillAndBorder(shortHair.color.toRender(), config.line)

    when (shortHair.style) {
        ShortHairStyle.Afro -> doNothing()
        ShortHairStyle.BuzzCut ->
            visualizeRectangleHair(renderer, config, options, aabb, Factor(0.0f))

        ShortHairStyle.Curly -> doNothing()
        ShortHairStyle.FlatTop ->
            visualizeRectangleHair(renderer, config, options, aabb, config.head.hair.flatTopY)
        ShortHairStyle.MiddlePart -> doNothing()
        ShortHairStyle.LeftSidePart -> doNothing()
        ShortHairStyle.RightSidePart -> doNothing()
        ShortHairStyle.Spiked -> doNothing()
    }
}

private fun visualizeRectangleHair(
    renderer: Renderer,
    config: RenderConfig,
    options: FillAndBorder,
    aabb: AABB,
    topY: Factor,
) {
    val (bottomLeft, bottomRight) = aabb.getMirroredPoints(Factor(1.0f), config.head.hairlineY)
    val (topLeft, topRight) = aabb.getMirroredPoints(Factor(1.0f), topY)

    renderPolygon(renderer, options, listOf(bottomLeft, bottomRight, topRight, topLeft))
}
