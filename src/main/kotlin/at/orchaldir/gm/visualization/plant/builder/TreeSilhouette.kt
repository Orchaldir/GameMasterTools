package at.orchaldir.gm.visualization.plant.builder

import at.orchaldir.gm.core.model.ecology.plant.appearance.NoTreeSilhouette
import at.orchaldir.gm.core.model.ecology.plant.appearance.SimpleTreeSilhouette
import at.orchaldir.gm.core.model.ecology.plant.appearance.TreeSilhouette
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Polygon2d
import at.orchaldir.gm.utils.math.Polygon2dBuilder
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Orientation
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

    fun processSegment(
        segment: SegmentData,
    ) {
        processor.startSegment(segment.end, segment.relativeLength)

        if (processor.relativeEnd > silhouette.base) {
            return
        }
        else if (processor.relativeStart < silhouette.base) {
            addPoints(silhouette.base, segment.orientation)
        }

        addPoints(processor.relativeEnd, segment.orientation)

        processor.endSegment()
    }

    fun finish() = SilhouetteData(
        silhouette.color,
        polygonBuilder.build(),
    )

    private fun addPoints(
        relativePosition: Factor,
        orientation: Orientation,
    ) {
        val position = processor.calculatePositionAlongSegment(relativePosition)
        val width = width *
                config.resolveTreeSilhouetteShape(silhouette.shape, relativePosition)

        polygonBuilder.addLeftAndRightPoint(
            position,
            orientation,
            width / 2,
        )
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