package at.orchaldir.gm.visualization.plant.visualization

import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.renderer.TransformRenderer
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.visualization.plant.PlantRenderState
import at.orchaldir.gm.visualization.plant.builder.PlantData
import at.orchaldir.gm.visualization.plant.builder.SilhouetteData
import at.orchaldir.gm.visualization.plant.builder.StemData
import at.orchaldir.gm.visualization.plant.builder.TreeData
import at.orchaldir.gm.visualization.plant.builder.UndefinedPlantData
import io.ktor.http.BadContentTypeFormatException

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

    state.renderer.createGroup(position) { renderer ->
        visualizeSilhouettes(state, renderer, tree.silhouette, false)

        tree.trunk.getBranches().forEach { branch ->
            visualizeStem(renderer, options, branch)
        }

        visualizeStem(renderer, options, tree.trunk)

        visualizeSilhouettes(state, renderer, tree.silhouette, true)
    }
}

private fun visualizeStem(
    renderer: TransformRenderer,
    options: FillAndBorder,
    stem: StemData,
) {
    val trunk = createStemPolygon(stem)
    renderer.renderRoundedPolygon(trunk, options)
}

private fun visualizeSilhouettes(
    state: PlantRenderState,
    renderer: TransformRenderer,
    silhouettes: List<SilhouetteData>,
    isFront: Boolean,
) {
    silhouettes.forEach {
        visualizeSilhouette(state, renderer, it, isFront)
    }
}

private fun visualizeSilhouette(
    state: PlantRenderState,
    renderer: TransformRenderer,
    data: SilhouetteData,
    isFront: Boolean,
) {
    if (isFront == data.showStems) {
        return
    }

    val options = state.config.getFillAndBorder(data.color)

    renderer.renderRoundedPolygon(data.polygon, options)
}
