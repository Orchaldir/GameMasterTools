package at.orchaldir.gm.core.selector.item

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.periodical.Periodical
import at.orchaldir.gm.core.model.item.periodical.PeriodicalId

fun State.canDeletePeriodical(text: PeriodicalId) = true

fun countPublicationFrequencies(collection: Collection<Periodical>) = collection
    .groupingBy { it.frequency }
    .eachCount()