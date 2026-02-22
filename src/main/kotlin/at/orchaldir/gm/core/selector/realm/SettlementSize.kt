package at.orchaldir.gm.core.selector.realm

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.SettlementSizeId

fun State.canDeleteSettlementSize(size: SettlementSizeId) = DeleteResult(size)