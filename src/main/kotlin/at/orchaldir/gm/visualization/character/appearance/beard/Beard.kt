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

fun visualizeBeard(state: CharacterRenderState<Head>, beard: Beard) {
    when (beard) {
        NoBeard -> doNothing()
        is NormalBeard -> visualizeNormalBeard(state, beard)
    }
}

private fun visualizeNormalBeard(state: CharacterRenderState<Head>, beard: NormalBeard) {
    when (val style = beard.style) {
        is FullBeard -> visualizeFullBeard(state, style.style, style.length, beard.color)
        is Goatee -> visualizeGoatee(state, style.goateeStyle, beard.color)
        is GoateeAndMoustache -> {
            visualizeGoatee(state, style.goateeStyle, beard.color)
            visualizeMoustache(state, style.moustacheStyle, beard.color)
        }

        is Moustache -> visualizeMoustache(state, style.moustacheStyle, beard.color)
        ShavedBeard -> doNothing()
    }
}

private fun visualizeGoatee(
    state: CharacterRenderState<Head>,
    goatee: GoateeStyle,
    color: Color,
) {
    val layer = state.getBeardLayer()
    val options = NoBorder(color.toRender())
    val polygon = when (goatee) {
        GoateeStyle.ChinPuff -> getChinPuff(state)
        GoateeStyle.Goatee -> {
            layer.renderPolygon(getGoatee(state), options)
            return
        }

        GoateeStyle.LandingStrip -> getLandingStrip(state)
        GoateeStyle.SoulPatch -> {
            layer.renderPolygon(getSoulPatch(state), options)
            return
        }

        GoateeStyle.VanDyke -> getVanDyke(state)
    }

    layer.renderRoundedPolygon(polygon, options)
}

private fun visualizeMoustache(
    state: CharacterRenderState<Head>,
    moustache: MoustacheStyle,
    color: Color,
) {
    val options = NoBorder(color.toRender())
    val polygon = when (moustache) {
        MoustacheStyle.FuManchu -> getFuManchu(state)
        MoustacheStyle.Handlebar -> getHandlebar(state)
        MoustacheStyle.Pencil -> getPencil(state)
        MoustacheStyle.Pyramid -> getPyramid(state)
        MoustacheStyle.Toothbrush -> getToothbrush(state)
        MoustacheStyle.Walrus -> getWalrus(state)
    }

    state.getBeardLayer().renderRoundedPolygon(polygon, options)
}
