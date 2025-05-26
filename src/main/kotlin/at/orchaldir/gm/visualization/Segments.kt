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

fun visualizeSegment(
    renderer: LayerRenderer,
    options: FillAndBorder,
    aabb: AABB,
    isUp: Boolean,
    segment: Segment,
) = when (segment.shape) {
    SegmentShape.Cone -> {
        val builder = Polygon2dBuilder()

        if (isUp) {
            builder.addMirroredPoints(aabb, FULL, END)
                .addLeftPoint(aabb, CENTER, START)
        } else {
            builder.addMirroredPoints(aabb, FULL, START)
                .addLeftPoint(aabb, CENTER, END)
        }

        renderer.renderPolygon(builder.build(), options)
    }

    SegmentShape.Cylinder -> renderer.renderRectangle(aabb, options)
    SegmentShape.RoundedCylinder -> renderer.renderRoundedPolygon(Polygon2d(aabb.getCorners()), options)
    SegmentShape.Sphere -> renderer.renderEllipse(aabb, options)
}