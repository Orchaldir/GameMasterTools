package at.orchaldir.gm.prototypes.visualization.plant

import at.orchaldir.gm.core.model.ecology.plant.Tree
import at.orchaldir.gm.core.model.ecology.plant.appearance.LinearStemThickness
import at.orchaldir.gm.core.model.ecology.plant.appearance.Stem
import at.orchaldir.gm.core.model.ecology.plant.appearance.StemSplitting
import at.orchaldir.gm.core.model.ecology.plant.appearance.StraightStem
import at.orchaldir.gm.core.model.ecology.plant.appearance.Trunk
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.Variance
import at.orchaldir.gm.utils.math.ZERO
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Distribution
import at.orchaldir.gm.utils.math.unit.ZERO_ORIENTATION
import at.orchaldir.gm.utils.renderer.svg.SvgBuilder
import at.orchaldir.gm.visualization.plant.PlantRenderState
import at.orchaldir.gm.visualization.plant.builder.buildPlant
import java.io.File
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.ecology.plant.appearance.CurvedStem
import at.orchaldir.gm.utils.math.END
import at.orchaldir.gm.utils.math.HALF
import at.orchaldir.gm.utils.math.unit.Orientation
import at.orchaldir.gm.utils.math.unit.Orientation.Companion.fromDegrees
import at.orchaldir.gm.utils.renderer.model.BorderOnly
import at.orchaldir.gm.visualization.plant.visualization.visualizePlant

fun main() {
    val height = Distance.fromMeters(1)
    val trunk = Trunk(
        Distribution(height),
        Stem(
3,
            CurvedStem(fromDegrees(30)),
            LinearStemThickness(fromPercentage(5)),
        ),
        Color.SaddleBrown,
    )
    val tree = Tree(trunk)
    val data = buildPlant(tree)
    val size = Size2d.square(height) // TODO
    val aabb = AABB(size)
    val svgBuilder = SvgBuilder(size)
    val state = PlantRenderState(
        State(),
        aabb,
        PLANT_CONFIG,
        svgBuilder,
    )

    svgBuilder.getLayer().renderRectangle(aabb, BorderOnly(PLANT_CONFIG.line))

    visualizePlant(state, data, aabb.getPoint(HALF, END))

    File("tree-trunk.svg").writeText(svgBuilder.finish().export())
}

