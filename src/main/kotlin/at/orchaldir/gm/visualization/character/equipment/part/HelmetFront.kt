package at.orchaldir.gm.visualization.character.equipment.part

import at.orchaldir.gm.core.model.item.equipment.style.EyeProtection
import at.orchaldir.gm.core.model.item.equipment.style.HelmetFront
import at.orchaldir.gm.core.model.item.equipment.style.NoHelmetFront
import at.orchaldir.gm.core.model.item.equipment.style.NoseProtection
import at.orchaldir.gm.core.model.item.equipment.style.NoseProtectionShape
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.CENTER
import at.orchaldir.gm.utils.math.Polygon2d
import at.orchaldir.gm.utils.math.Polygon2dBuilder
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.equipment.HelmetConfig

fun visualizeHelmetFront(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    config: HelmetConfig,
    front: HelmetFront,
) = when (front) {
    NoHelmetFront -> doNothing()
    is NoseProtection -> visualizeNoseProtection(state, renderer, config, front)
    is EyeProtection -> doNothing()
}

private fun visualizeNoseProtection(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    config: HelmetConfig,
    protection: NoseProtection,
) {
    val color = protection.part.getColor(state.state, state.colors)
    val options = state.config.getLineOptions(color)
    val polygon = createNoseProtectionPolygon(state.aabb, config, protection)

    renderer.renderRoundedPolygon(polygon, options)
}

private fun createNoseProtectionPolygon(
    aabb: AABB,
    config: HelmetConfig,
    protection: NoseProtection,
): Polygon2d {
    val helmWidth = config.getHelmWidth()
    val builder = Polygon2dBuilder()

    when (protection.shape) {
        NoseProtectionShape.Hexagon -> builder
            .addMirroredPoints(aabb, config.noseWidth, config.noseTopY, true)
            .addMirroredPoints(aabb, config.noseWidth, config.noseBottomY, true)

        NoseProtectionShape.Rectangle -> builder
            .addMirroredPoints(aabb, config.noseWidth, config.noseTopY, true)
            .addMirroredPoints(aabb, config.noseWidth, config.noseBottomY, true)

        NoseProtectionShape.RoundedRectangle -> builder
            .addMirroredPoints(aabb, config.noseWidth, config.noseTopY)
            .addMirroredPoints(aabb, config.noseWidth, config.noseBottomY)

        NoseProtectionShape.Triangle -> builder
            .addLeftPoint(aabb, CENTER, config.noseTopY, true)
            .addMirroredPoints(aabb, config.noseWidth, config.noseBottomY, true)
    }

    return builder.build()
}
