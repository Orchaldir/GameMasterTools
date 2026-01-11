package at.orchaldir.gm.core.reducer.rpg

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.Shots
import at.orchaldir.gm.core.model.rpg.combat.SingleShot
import at.orchaldir.gm.core.model.rpg.combat.Thrown
import at.orchaldir.gm.core.model.rpg.combat.UndefinedShots
import at.orchaldir.gm.utils.doNothing

fun validateShots(
    shots: Shots,
) {
    when (shots) {
        is SingleShot -> validateRoundsOfReload(shots.roundsOfReload)
        is Thrown -> validateRoundsOfReload(shots.roundsOfReload)
        UndefinedShots -> doNothing()
    }
}

private fun validateRoundsOfReload(roundsOfReload: Int) {
    require(roundsOfReload >= 0) { "Rounds of reload must be >= 0!" }
}
