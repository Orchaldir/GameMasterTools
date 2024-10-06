package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.street.StreetTypeId

fun State.canDelete(type: StreetTypeId) = getStreets(type).isEmpty()
