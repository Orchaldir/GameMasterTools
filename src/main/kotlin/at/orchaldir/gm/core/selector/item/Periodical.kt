package at.orchaldir.gm.core.selector.item

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.periodical.Periodical
import at.orchaldir.gm.core.model.item.periodical.PeriodicalId
import at.orchaldir.gm.core.model.language.LanguageId

fun State.canDeletePeriodical(text: PeriodicalId) = true

fun State.countPeriodicals(language: LanguageId) = getPeriodicalStorage()
    .getAll()
    .count { b -> b.language == language }

fun countPublicationFrequencies(collection: Collection<Periodical>) = collection
    .groupingBy { it.frequency }
    .eachCount()

fun State.getPeriodicals(language: LanguageId) = getPeriodicalStorage()
    .getAll()
    .filter { b -> b.language == language }