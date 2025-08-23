package at.orchaldir.gm.core.selector.religion

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.religion.GodId
import at.orchaldir.gm.core.model.religion.PantheonId
import at.orchaldir.gm.core.selector.util.getBelievers
import at.orchaldir.gm.core.selector.util.getFormerBelievers

fun State.canDeletePantheon(pantheon: PantheonId) = getBelievers(getCharacterStorage(), pantheon).isEmpty()
        && getFormerBelievers(getCharacterStorage(), pantheon).isEmpty()
        && getBelievers(getOrganizationStorage(), pantheon).isEmpty()
        && getFormerBelievers(getOrganizationStorage(), pantheon).isEmpty()

fun State.getPantheonsContaining(god: GodId) = getPantheonStorage()
    .getAll()
    .filter { it.gods.contains(god) }
