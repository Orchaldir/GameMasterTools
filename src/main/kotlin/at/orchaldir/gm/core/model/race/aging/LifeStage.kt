package at.orchaldir.gm.core.model.race.aging

import at.orchaldir.gm.utils.math.Factor

interface LifeStage {
    fun name(): String
    fun maxAge(): Int
    fun relativeHeight(): Factor
}