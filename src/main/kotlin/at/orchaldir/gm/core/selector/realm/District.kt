package at.orchaldir.gm.core.selector.realm

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.DistrictId
import at.orchaldir.gm.core.model.realm.TownId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.character.getCharactersLivingIn
import at.orchaldir.gm.core.selector.character.getCharactersPreviouslyLivingIn
import at.orchaldir.gm.core.selector.util.getExistingElements
import at.orchaldir.gm.core.selector.util.hasNoHasPositionsIn

fun State.canDeleteDistrict(district: DistrictId) = getCharactersLivingIn(district).isEmpty()
        && getCharactersPreviouslyLivingIn(district).isEmpty()
        && hasNoHasPositionsIn(district)

fun State.getDistricts(town: TownId) = getDistrictStorage()
    .getAll()
    .filter { it.town == town }

fun State.getExistingDistricts(date: Date?) = getExistingElements(getDistrictStorage().getAll(), date)
