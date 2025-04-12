package at.orchaldir.gm.visualization.character.appearance.hair

import at.orchaldir.gm.core.model.character.appearance.hair.Bun
import at.orchaldir.gm.core.model.character.appearance.hair.BunStyle
import at.orchaldir.gm.core.model.character.appearance.hair.NormalHair
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.BEHIND_LAYER
import at.orchaldir.gm.visualization.character.appearance.HAIR_LAYER

fun visualizeBun(state: CharacterRenderState, hair: NormalHair, bun: Bun) {
    val config = state.config
    val options = config.getLineOptions(hair.color)
    val radius = state.aabb.convertHeight(config.head.hair.bunRadius.convert(bun.size))
    val renderer = state.renderer.getLayer(HAIR_LAYER)

    visualizeBackSideOfHead(state, options)

    val center = when (bun.style) {
        BunStyle.High -> state.aabb.getPoint(CENTER, START)
        BunStyle.Low -> state.aabb.getPoint(CENTER, END)
        BunStyle.Twin -> state.aabb.getPoint(END, CENTER)
    }

    renderer.renderCircle(center, radius, options)

    if (bun.style == BunStyle.Twin) {
        val otherSide = state.aabb.mirrorVertically(center)

        renderer.renderCircle(otherSide, radius, options)
    }
}
