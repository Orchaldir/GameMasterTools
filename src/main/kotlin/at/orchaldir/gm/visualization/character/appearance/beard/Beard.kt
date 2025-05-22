package at.orchaldir.gm.visualization.character.appearance.beard

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.beard.*
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.renderer.model.NoBorder
import at.orchaldir.gm.visualization.character.CharacterRenderState

data class BeardConfig(
    val goateeWidth: Factor,
    val smallThickness: Factor,
    val mediumThickness: Factor,
    val moustacheOffset: Factor,
    val wideFullBeardWidth: Factor,
)

fun visualizeBeard(state: CharacterRenderState, head: Head, beard: Beard) {
    when (beard) {
        NoBeard -> doNothing()
        is NormalBeard -> visualizeNormalBeard(state, head, beard)
    }
}

private fun visualizeNormalBeard(state: CharacterRenderState, head: Head, beard: NormalBeard) {
    when (val style = beard.style) {
        is FullBeard -> visualizeFullBeard(state, style.style, style.length, beard.color)
        is Goatee -> visualizeGoatee(state, head, style.goateeStyle, beard.color)
        is GoateeAndMoustache -> {
            visualizeGoatee(state, head, style.goateeStyle, beard.color)
            visualizeMoustache(state, head, style.moustacheStyle, beard.color)
        }

        is Moustache -> visualizeMoustache(state, head, style.moustacheStyle, beard.color)
        ShavedBeard -> doNothing()
    }
}

private fun visualizeGoatee(
    state: CharacterRenderState,
    head: Head,
    goatee: GoateeStyle,
    color: Color,
) {
    val layer = state.getBeardLayer()
    val options = NoBorder(color.toRender())
    val polygon = when (goatee) {
        GoateeStyle.ChinPuff -> getChinPuff(state, head)
        GoateeStyle.Goatee -> {
            layer.renderPolygon(getGoatee(state, head), options)
            return
        }

        GoateeStyle.LandingStrip -> getLandingStrip(state, head)
        GoateeStyle.SoulPatch -> {
            layer.renderPolygon(getSoulPatch(state, head), options)
            return
        }

        GoateeStyle.VanDyke -> getVanDyke(state, head)
    }

    layer.renderRoundedPolygon(polygon, options)
}

private fun visualizeMoustache(
    state: CharacterRenderState,
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

    state.getBeardLayer().renderRoundedPolygon(polygon, options)
}
