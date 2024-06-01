package at.orchaldir.gm.visualization.character

import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.UndefinedAppearance
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Size2d.Companion.square
import at.orchaldir.gm.utils.renderer.BorderOnly
import at.orchaldir.gm.utils.renderer.Renderer
import at.orchaldir.gm.utils.renderer.svg.Svg
import at.orchaldir.gm.utils.renderer.svg.SvgBuilder
import at.orchaldir.gm.visualization.RenderConfig

fun visualizeCharacter(config: RenderConfig, appearance: Appearance): Svg {
    val size = calculateSize(config, appearance)
    val aabb = AABB(size)
    val builder = SvgBuilder.create(size)

    visualizeAppearance(builder, config, aabb, appearance)

    return builder.finish()
}

fun visualizeAppearance(
    renderer: Renderer,
    config: RenderConfig,
    aabb: AABB,
    appearance: Appearance,
) {
    val inner = aabb.shrink(config.padding)

    renderer.renderRectangle(aabb, BorderOnly(config.line))

    when (appearance) {
        is HeadOnly -> visualizeHead(renderer, config, inner, appearance.head)
        UndefinedAppearance -> doNothing()
    }
}

fun calculateSize(config: RenderConfig, appearance: Appearance) = when (appearance) {
    is HeadOnly -> calculateSizeFromHeight(config, appearance.height)
    UndefinedAppearance -> square(config.padding * 4.0f)
}

fun calculateSizeFromHeight(
    config: RenderConfig,
    height: Distance,
) = square(height + config.padding * 2.0f)