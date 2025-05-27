package at.orchaldir.gm.visualization.utils

import at.orchaldir.gm.utils.isEven
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.CENTER
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.shape.ComplexShape
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.RenderOptions

fun visualizeRowOfShapes(
    renderer: LayerRenderer,
    options: RenderOptions,
    aabb: AABB,
    y: Factor,
    shape: ComplexShape,
    size: Size2d,
    number: Int,
) {
    val rowCenter = aabb.getPoint(CENTER, y)
    val rowStart = rowCenter.minusWidth(size.width * (number - 1) / 2.0f)
    var center = rowStart

    repeat(number) {
        val scaleAabb = AABB.fromCenter(center, size)

        visualizeComplexShape(renderer, scaleAabb, shape, options)

        center = center.addWidth(size.width)
    }
}