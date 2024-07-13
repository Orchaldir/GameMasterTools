package at.orchaldir.gm.visualization.character.beard

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.beard.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.renderer.NoBorder
import at.orchaldir.gm.visualization.RenderState
import at.orchaldir.gm.visualization.character.ABOVE_EQUIPMENT_LAYER

data class BeardConfig(
    val goateeWidth: Factor,
    val smallThickness: Factor,
    val mediumThickness: Factor,
    val moustacheOffset: Factor,
)

fun visualizeBeard(state: RenderState, head: Head, beard: Beard) {
    when (beard) {
        NoBeard -> doNothing()
        is NormalBeard -> visualizeNormalBeard(state, head, beard)
    }
}

private fun visualizeNormalBeard(state: RenderState, head: Head, beard: NormalBeard) {
    when (beard.style) {
        is Goatee -> visualizeGoatee(state, head, beard.style.goateeStyle, beard.color)
        is GoateeAndMoustache -> {
            visualizeGoatee(state, head, beard.style.goateeStyle, beard.color)
            visualizeMoustache(state, head, beard.style.moustacheStyle, beard.color)
        }

        is Moustache -> visualizeMoustache(state, head, beard.style.moustacheStyle, beard.color)
        ShavedBeard -> doNothing()
    }
}

private fun visualizeGoatee(
    state: RenderState,
    head: Head,
    goatee: GoateeStyle,
    color: Color,
) {
    val options = NoBorder(color.toRender())
    val polygon = when (goatee) {
        GoateeStyle.ChinPuff -> getChinPuff(state, head)
        GoateeStyle.Goatee -> {
            state.renderer.renderPolygon(getGoatee(state, head), options, ABOVE_EQUIPMENT_LAYER)
            return
        }

        GoateeStyle.LandingStrip -> getLandingStrip(state, head)
        GoateeStyle.SoulPatch -> {
            state.renderer.renderPolygon(getSoulPatch(state, head), options, ABOVE_EQUIPMENT_LAYER)
            return
        }

        GoateeStyle.VanDyke -> getVanDyke(state, head)
    }

    state.renderer.renderRoundedPolygon(polygon, options, ABOVE_EQUIPMENT_LAYER)
}

private fun visualizeMoustache(
    state: RenderState,
    head: Head,
    moustache: MoustacheStyle,
    color: Color,
) {
    val options = NoBorder(color.toRender())
    val polygon = when (moustache) {
        MoustacheStyle.FuManchu -> getFuManchu(state, head)
        MoustacheStyle.Handlebar -> getHandlebar(state, head)
        MoustacheStyle.Pencil -> getPencil(state, head)
        MoustacheStyle.Pyramid -> getPyramid(state, head)
        MoustacheStyle.Toothbrush -> getToothbrush(state, head)
        MoustacheStyle.Walrus -> getWalrus(state, head)
    }

    state.renderer.renderRoundedPolygon(polygon, options, ABOVE_EQUIPMENT_LAYER)
}
