package at.orchaldir.gm.core.selector.item

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.periodical.PeriodicalId
import at.orchaldir.gm.core.model.item.periodical.PeriodicalIssueId

fun State.canDeletePeriodicalIssue(issue: PeriodicalIssueId) = true

fun State.getPeriodicalIssues(id: PeriodicalId) = getPeriodicalIssueStorage()
    .getAll()
    .filter { it.periodical == id }
