package at.orchaldir.gm.core.selector.item.periodical

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.periodical.PeriodicalId
import at.orchaldir.gm.core.model.item.periodical.PeriodicalIssueId

fun State.canDeletePeriodicalIssue(issue: PeriodicalIssueId) = true

fun State.countPeriodicalIssues(id: PeriodicalId) = getPeriodicalIssueStorage()
    .getAll()
    .count { it.periodical == id }

fun State.getPeriodicalIssues(id: PeriodicalId) = getPeriodicalIssueStorage()
    .getAll()
    .filter { it.periodical == id }
