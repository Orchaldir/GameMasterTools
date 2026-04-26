package at.orchaldir.gm.prototypes.visualization.plant

import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMicrometers
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.visualization.plant.PlantRenderConfig

val PLANT_CONFIG = PlantRenderConfig(
    LineOptions(Color.Black.toRender(), fromMicrometers(200)),
    fromPercentage(200),
)
