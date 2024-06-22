package at.orchaldir.gm.visualization.character

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.getStartX
import at.orchaldir.gm.utils.renderer.Renderer
import at.orchaldir.gm.visualization.RenderConfig
import at.orchaldir.gm.visualization.SizeConfig

data class BodyConfig(
    val torsoHeight: Factor,
    val torsoWidth: SizeConfig,
    val torsoY: Factor,
) {
    fun getTorsoAabb(aabb: AABB, body: Body): AABB {
        val width = Factor(torsoWidth.convert(body.width))
        val startX = getStartX(width)
        val start = aabb.getPoint(startX, torsoY)
        val size = aabb.size.scale(width, torsoHeight)

        return AABB(start, size)
    }

}

fun visualizeBody(renderer: Renderer, config: RenderConfig, aabb: AABB, body: Body) {
    visualizeTorso(renderer, config, aabb, body)
}

fun visualizeTorso(renderer: Renderer, config: RenderConfig, aabb: AABB, body: Body) {
    val torso = config.body.getTorsoAabb(aabb, body)

    renderer.renderRectangle(torso, config.getOptions(body.skin))
}

