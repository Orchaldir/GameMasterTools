package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.core.model.BaseId

interface HasOrigin<ID : BaseId<ID>> : Creation, HasStartDate {

    fun origin(): Origin<ID>
    override fun creator() = origin().creator()
    override fun startDate() = origin().date()

}