package at.orchaldir.gm.core.reducer.rpg

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.*
import at.orchaldir.gm.core.model.rpg.statistic.StatisticDataType
import at.orchaldir.gm.utils.doNothing

fun validateRangedAttack(
    state: State,
    attack: RangedAttack,
) {
    validateAttackEffect(state, attack.effect)
    validateRange(state, attack.range)
    validateShots(attack.shots)
}
