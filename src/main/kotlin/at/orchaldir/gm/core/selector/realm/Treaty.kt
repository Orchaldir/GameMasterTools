package at.orchaldir.gm.core.selector.realm

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.TreatyId
import at.orchaldir.gm.core.selector.time.getHolidays

fun State.canDeleteTreaty(treaty: TreatyId) = DeleteResult(treaty)
    .addElements(getHolidays(treaty))
    .addElements(getWarsEndedBy(treaty))
