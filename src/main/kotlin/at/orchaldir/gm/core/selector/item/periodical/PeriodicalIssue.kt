package at.orchaldir.gm.core.selector.item.periodical

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.periodical.ArticleId
import at.orchaldir.gm.core.model.item.periodical.PeriodicalId
import at.orchaldir.gm.core.model.item.periodical.PeriodicalIssueId

fun State.canDeletePeriodicalIssue(issue: PeriodicalIssueId) = DeleteResult(issue)

fun State.countPeriodicalIssues(id: PeriodicalId) = getPeriodicalIssueStorage()
    .getAll()
    .count { it.periodical == id }

fun State.getPeriodicalIssues(id: PeriodicalId) = getPeriodicalIssueStorage()
    .getAll()
    .filter { it.periodical == id }

fun State.getPeriodicalIssues(id: ArticleId) = getPeriodicalIssueStorage()
    .getAll()
    .filter { it.articles.contains(id) }
