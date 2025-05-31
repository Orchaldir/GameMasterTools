package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.visualization.character.CharacterRenderState

fun visualizeSpearHead(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    shaftAabb: AABB,
    head: SpearHead,
) {
    val headAabb = createSpearHeadAabb(shaftAabb, head)
    val polygon = createSpearHeadPolygon(state.config.equipment.polearm, headAabb, head.shape)
    val color = head.part.getColor(state.state, state.colors)
    val options = FillAndBorder(color.toRender(), state.config.line)

    renderer.renderRoundedPolygon(polygon, options)
    renderer.renderLine(
        listOf(
            headAabb.getPoint(CENTER, START),
            headAabb.getPoint(CENTER, END),
        ), state.config.line
    )
}

private fun createSpearHeadAabb(
    shaftAabb: AABB,
    head: SpearHead,
): AABB {
    val width = shaftAabb.size.width * head.width
    val length = shaftAabb.size.height * head.length
    val center = shaftAabb.getPoint(CENTER, -head.length / 2)

    return AABB.fromWidthAndHeight(center, width, length)
}

private fun createSpearHeadPolygon(
    config: PolearmConfig,
    headAabb: AABB,
    shape: SpearShape,
): Polygon2d {
    val builder = Polygon2dBuilder()
        .addLeftPoint(headAabb, CENTER, START, true)

    when (shape) {
        SpearShape.Diamond -> builder
            .addMirroredPoints(headAabb, FULL, END - config.spearHeadBase, true)
            .addMirroredPoints(headAabb, HALF, END, true)

        SpearShape.Leaf -> builder
            .addMirroredPoints(headAabb, FULL, HALF)
            .addMirroredPoints(headAabb, FULL, END)

        SpearShape.Teardrop -> builder
            .addMirroredPoints(headAabb, FULL, END - config.spearHeadBase)
            .addMirroredPoints(headAabb, FULL, END)

        SpearShape.Triangle -> builder
            .addMirroredPoints(headAabb, FULL, END, true)
    }

    return builder.build()
}