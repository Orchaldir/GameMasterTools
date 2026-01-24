package at.orchaldir.gm.core.reducer.rpg

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.RangedAttack

fun validateRangedAttack(
    state: State,
    attack: RangedAttack,
) {
    validateAttackEffect(state, attack.effect)
    validateRange(state, attack.range)
    validateShots(state, attack.shots)
}
