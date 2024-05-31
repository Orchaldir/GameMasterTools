package at.orchaldir.gm.visualization.character

import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.UndefinedAppearance
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Size2d.Companion.square
import at.orchaldir.gm.utils.renderer.BorderOnly
import at.orchaldir.gm.utils.renderer.svg.Svg
import at.orchaldir.gm.utils.renderer.svg.SvgBuilder
import at.orchaldir.gm.visualization.RenderConfig

fun visualizeCharacter(config: RenderConfig, appearance: Appearance): Svg {
    val size = calculateSize(config, appearance)
    val aabb = AABB(size)
    val inner = aabb.shrink(config.padding)
    val builder = SvgBuilder.create(size)

    builder.renderRectangle(aabb, BorderOnly(config.line))

    when (appearance) {
        is HeadOnly -> visualizeHead(builder, config, inner, appearance.head, appearance.skin)
        UndefinedAppearance -> doNothing()
    }

    return builder.finish()
}

fun calculateSize(config: RenderConfig, appearance: Appearance) = when (appearance) {
    is HeadOnly -> square(appearance.height + config.padding * 2.0f)
    UndefinedAppearance -> square(config.padding * 4.0f)
}