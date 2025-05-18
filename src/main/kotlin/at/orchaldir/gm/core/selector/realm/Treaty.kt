package at.orchaldir.gm.core.selector.realm

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.TreatyId
import at.orchaldir.gm.core.selector.getHolidays

fun State.canDeleteTreaty(treaty: TreatyId) = getHolidays(treaty).isEmpty()
