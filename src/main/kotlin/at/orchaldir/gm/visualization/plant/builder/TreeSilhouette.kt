package at.orchaldir.gm.visualization.plant.builder

import at.orchaldir.gm.core.logger
import at.orchaldir.gm.core.model.ecology.plant.appearance.NoTreeSilhouette
import at.orchaldir.gm.core.model.ecology.plant.appearance.SimpleTreeSilhouette
import at.orchaldir.gm.core.model.ecology.plant.appearance.TreeSilhouette
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.ONE_PERCENT
import at.orchaldir.gm.utils.math.Polygon2d
import at.orchaldir.gm.utils.math.Polygon2dBuilder
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Orientation
import at.orchaldir.gm.utils.math.unit.ZERO_DISTANCE
import at.orchaldir.gm.visualization.plant.PlantRenderConfig

data class SilhouetteData(
    val color: Color,
    val polygon: Polygon2d,
)

data class SimpleSilhouetteBuilder(
    val config: PlantRenderConfig,
    val silhouette: SimpleTreeSilhouette,
    val processor: StemProcessor,
    val width: Distance,
    val polygonBuilder: Polygon2dBuilder = Polygon2dBuilder(),
) {

    fun processSegment(segment: SegmentData) {
        processor.startSegment(segment.end, segment.relativeLength)

        updatePolygon(segment)

        processor.endSegment()
    }

    fun finish() = SilhouetteData(
        silhouette.color,
        polygonBuilder.build(),
    )

    private fun updatePolygon(segment: SegmentData) {
        if (processor.relativeEnd <= silhouette.base) {
            return
        }
        else if (processor.relativeStart < silhouette.base) {
            addPoints(silhouette.base, segment.orientation)
        }

        addPoints(processor.relativeEnd, segment.orientation)
    }

    private fun addPoints(
        relativePosition: Factor,
        orientation: Orientation,
    ) {
        val position = processor.calculatePositionAlongSegment(relativePosition)
        val relativeWidth = config.resolveTreeSilhouetteShape(silhouette.shape, relativePosition)
        val width = width * relativeWidth
        logger.info { "relativePosition=$relativePosition relativeWidth=$relativeWidth width=$width" }

        if (relativeWidth >= ONE_PERCENT) {
            polygonBuilder.addLeftAndRightPoint(
                position,
                orientation,
                width / 2,
            )
        } else {
            polygonBuilder.addPoint(position, true)
        }
    }
}

fun buildTreeSilhouette(
    config: PlantRenderConfig,
    silhouette: TreeSilhouette,
    trunk: StemData,
): List<SilhouetteData> = when (silhouette) {
    NoTreeSilhouette -> emptyList()
    is SimpleTreeSilhouette -> buildSimpleTreeSilhouette(
        config,
        silhouette,
        trunk,
    )
}

private fun buildSimpleTreeSilhouette(
    config: PlantRenderConfig,
    silhouette: SimpleTreeSilhouette,
    trunk: StemData,
): List<SilhouetteData> {
    val builder = SimpleSilhouetteBuilder(
        config,
        silhouette,
        StemProcessor(
            trunk.start,
            silhouette.base,
        ),
        trunk.length * silhouette.width,
    )
    var segment: SegmentData? = trunk.segment

    while (segment != null) {
        builder.processSegment(segment)

        segment = segment.next.firstOrNull()
    }

    return listOf(builder.finish())
}