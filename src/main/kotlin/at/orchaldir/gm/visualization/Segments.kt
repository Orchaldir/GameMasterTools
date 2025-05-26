package at.orchaldir.gm.visualization

import at.orchaldir.gm.core.model.util.part.Segment
import at.orchaldir.gm.core.model.util.part.SegmentShape
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.CENTER
import at.orchaldir.gm.utils.math.END
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Polygon2d
import at.orchaldir.gm.utils.math.Polygon2dBuilder
import at.orchaldir.gm.utils.math.START
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.FillAndBorder

fun visualizeSegments(
    renderer: LayerRenderer,
    options: FillAndBorder,
    aabb: AABB,
    segment: Segment,
) = when (segment.shape) {
    SegmentShape.Cone -> {
        val polygon = Polygon2dBuilder()
            .addMirroredPoints(aabb, FULL, END)
            .addLeftPoint(aabb, CENTER, START)
            .build()

        renderer.renderPolygon(polygon, options)
    }

    SegmentShape.Cylinder -> renderer.renderRectangle(aabb, options)
    SegmentShape.RoundedCylinder -> renderer.renderRoundedPolygon(Polygon2d(aabb.getCorners()), options)
    SegmentShape.Sphere -> renderer.renderEllipse(aabb, options)
}