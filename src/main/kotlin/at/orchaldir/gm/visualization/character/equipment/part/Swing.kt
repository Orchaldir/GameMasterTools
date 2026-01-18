package at.orchaldir.gm.visualization.character.equipment.part

import at.orchaldir.gm.utils.math.unit.Orientation
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.TransformRenderer

data class SwingConfig(
    val maxRotation: Orientation,
    val duration: Double,
) {

    fun createSwingGroup(renderer: LayerRenderer, content: (TransformRenderer) -> Unit) {
        val orientations = listOf(maxRotation, -maxRotation, maxRotation)

        renderer.createGroup(maxRotation) { swingRenderer ->
            swingRenderer.animate(orientations, duration)

            content(swingRenderer)
        }
    }
}
