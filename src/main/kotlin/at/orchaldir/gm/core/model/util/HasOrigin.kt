package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.utils.Id

interface HasOrigin<ID : Id<ID>> : Creation, HasStartDate {

    fun origin(): Origin<ID>
    override fun creator() = origin().creator()
    override fun startDate() = origin().date()

}