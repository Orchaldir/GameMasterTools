package at.orchaldir.gm.visualization.plant.visualization

import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.visualization.plant.PlantRenderState
import at.orchaldir.gm.visualization.plant.builder.PlantData
import at.orchaldir.gm.visualization.plant.builder.TreeData
import at.orchaldir.gm.visualization.plant.builder.UndefinedPlantData

fun visualizePlant(
    state: PlantRenderState,
    plant: PlantData,
    position: Point2d,
) = when (plant) {
    is TreeData -> visualizeTree(state, plant, position)
    UndefinedPlantData -> doNothing()
}

fun visualizeTree(
    state: PlantRenderState,
    tree: TreeData,
    position: Point2d,
) {
    val options = state.config.getFillAndBorder(tree.bark)
    val trunk = createStemPolygon(tree.trunk)

    state.renderer.createGroup(position) { renderer ->
        renderer.renderRoundedPolygon(trunk, options)
    }
}
