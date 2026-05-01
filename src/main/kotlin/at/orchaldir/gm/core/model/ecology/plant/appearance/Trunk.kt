package at.orchaldir.gm.core.model.ecology.plant.appearance

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.ecology.plant.Tree
import at.orchaldir.gm.core.model.ecology.plant.UndefinedPlantAppearance
import at.orchaldir.gm.core.model.race.MAX_RACE_HEIGHT
import at.orchaldir.gm.core.model.race.MIN_RACE_HEIGHT
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.reducer.util.checkIsInside
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Distribution
import at.orchaldir.gm.utils.math.unit.checkDistance
import kotlinx.serialization.Serializable

val MIN_TRUNK_HEIGHT = Distance.fromMeters(1)
val MAX_TRUNK_HEIGHT = Distance.fromMeters(200)

@Serializable
data class Trunk(
    val height: Distribution<Distance> = Distribution(MIN_TRUNK_HEIGHT),
    val stem: Stem = Stem(),
    val bark: Color = Color.SaddleBrown,
) {

    fun validate(state: State) {
        checkDistance(height.center, "trunk's height", MIN_TRUNK_HEIGHT, MAX_TRUNK_HEIGHT)
    }

}
