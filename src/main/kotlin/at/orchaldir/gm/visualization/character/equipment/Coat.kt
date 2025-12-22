package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.Coat
import at.orchaldir.gm.core.model.item.equipment.style.GloveStyle
import at.orchaldir.gm.core.model.item.equipment.style.NecklineStyle
import at.orchaldir.gm.core.model.item.equipment.style.OuterwearLength
import at.orchaldir.gm.core.model.item.equipment.style.SleeveStyle
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.toRender
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.ICharacterConfig
import at.orchaldir.gm.visualization.character.appearance.BodyConfig
import at.orchaldir.gm.visualization.character.appearance.addHip
import at.orchaldir.gm.visualization.character.appearance.addTorso
import at.orchaldir.gm.visualization.character.equipment.part.addNeckline
import at.orchaldir.gm.visualization.character.equipment.part.visualizeOpening
import at.orchaldir.gm.visualization.character.equipment.part.visualizeSleeves
import at.orchaldir.gm.visualization.character.equipment.part.visualizeTopPockets
import at.orchaldir.gm.visualization.renderBuilder

data class CoatConfig(
    val thickness: Factor,
    val widthPadding: Factor,
) {
    fun getHipWidth(config: ICharacterConfig<Body>) =
        config.body().getTorsoWidth(config) * config.body().getHipWidth(config) * getPaddedWidth()

    fun getPaddedWidth() = FULL + widthPadding

    fun getVolume(
        config: ICharacterConfig<Body>,
        length: OuterwearLength,
        sleeveStyle: SleeveStyle,
    ) = config.equipment().getSleevesVolume(config, sleeveStyle, thickness) +
            config.equipment().getOuterwearBodyVolume(config, length, thickness)
}

fun getOuterwearBottomY(
    config: ICharacterConfig<Body>,
    length: OuterwearLength,
    ankleFactor: Factor = FULL,
): Factor {
    val bottomHeight = config.equipment().getOuterwearHeightFactor(length, ankleFactor)
    return config.body().getLegY(config, bottomHeight)
}

fun visualizeCoat(
    state: CharacterRenderState<Body>,
    coat: Coat,
    layer: Int,
) {
    val fill = coat.main.getFill(state.state, state.colors)
    val options = FillAndBorder(fill.toRender(), state.config.line)

    visualizeSleeves(state, options, coat.sleeveStyle, layer)
    visualizeCoatBody(state, options, coat, layer)

    if (state.renderFront) {
        val necklineHeight = state.config.equipment.neckline.getHeight(coat.necklineStyle)
        val bottomY = getOuterwearBottomY(state, coat.length)
        val topY = state.config.body.torsoY + state.config.body.torsoHeight * necklineHeight
        val torsoWidth = state.config.body.getTorsoWidth(state)
        val size = state.fullAABB.size.scale(torsoWidth, FULL)
        val aabb = AABB.fromCenter(state.fullAABB.getCenter(), size)

        visualizeOpening(state, aabb, HALF, topY, bottomY, coat.openingStyle, layer)
        visualizeTopPockets(state, options, coat.pocketStyle, layer)
    }
}

private fun visualizeCoatBody(
    state: CharacterRenderState<Body>,
    options: FillAndBorder,
    coat: Coat,
    layer: Int,
) {
    val paddedWidth = state.config.equipment.coat.getPaddedWidth()
    val builder = createOuterwearBuilder(state, coat.length, coat.necklineStyle, paddedWidth)
    addNeckline(state, builder, coat.necklineStyle)

    renderBuilder(state.renderer, builder, options, layer)
}

fun createOuterwearBuilder(
    state: CharacterRenderState<Body>,
    length: OuterwearLength,
    necklineStyle: NecklineStyle = NecklineStyle.None,
    paddedWidth: Factor = FULL,
): Polygon2dBuilder {
    val builder = createOuterwearBottom(state, length, paddedWidth)
    addTorso(state, builder, necklineStyle.addTop(), paddedWidth)
    return builder
}

fun createOuterwearBottom(
    state: CharacterRenderState<Body>,
    length: OuterwearLength,
    paddedWidth: Factor,
): Polygon2dBuilder {
    val builder = Polygon2dBuilder()

    if (length != OuterwearLength.Hip) {
        val config = state.config.body
        val width = config.getTorsoWidth(state) * config.getHipWidth(state) * paddedWidth
        val bottomY = getOuterwearBottomY(state, length)

        builder.addMirroredPoints(state.fullAABB, width, bottomY)
    }

    addHip(state, builder, paddedWidth)

    return builder
}