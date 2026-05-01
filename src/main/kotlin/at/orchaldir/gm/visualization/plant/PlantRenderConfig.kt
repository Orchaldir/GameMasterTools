package at.orchaldir.gm.visualization.plant

import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.LineOptions

data class PlantRenderConfig(
    val line: LineOptions,
    val padding: Factor,
) {

    fun getFillAndBorder(color: Color) = FillAndBorder(color.toRender(), line)

}