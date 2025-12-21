package at.orchaldir.gm.visualization.character.appearance.hair

import at.orchaldir.gm.core.model.character.appearance.hair.Bun
import at.orchaldir.gm.core.model.character.appearance.hair.BunStyle
import at.orchaldir.gm.core.model.character.appearance.hair.NormalHair
import at.orchaldir.gm.utils.math.CENTER
import at.orchaldir.gm.utils.math.END
import at.orchaldir.gm.utils.math.START
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.HAIR_LAYER

fun visualizeBun(state: CharacterRenderState, hair: NormalHair, bun: Bun) {
    val config = state.config
    val options = config.getLineOptions(hair.color)
    val radiusFactor = config.head.hair.bunRadius.convert(bun.size)
    val aabb = state.headAABB()
    val radius = aabb.convertHeight(radiusFactor)
    val renderer = state.getLayer(HAIR_LAYER - 1)

    visualizeBackSideOfHead(state, options, HAIR_LAYER)

    val center = when (bun.style) {
        BunStyle.High -> aabb.getPoint(CENTER, START)
        BunStyle.Low -> aabb.getPoint(CENTER, END - radiusFactor / 2.0f)
        BunStyle.Twin -> aabb.getPoint(END, CENTER - radiusFactor / 2.0f)
    }

    renderer.renderCircle(center, radius, options)

    if (bun.style == BunStyle.Twin) {
        val otherSide = aabb.mirrorVertically(center)

        renderer.renderCircle(otherSide, radius, options)
    }
}
