package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date

interface HasStartDate {

    fun startDate(): Date?

}

interface HasComplexStartDate {

    fun startDate(state: State): Date?

}