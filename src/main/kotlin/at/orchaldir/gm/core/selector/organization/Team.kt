package at.orchaldir.gm.core.selector.organization

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.organization.TeamId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.util.getExistingElements

fun State.canDeleteTeam(team: TeamId) = false

fun State.getExistingTeams(date: Date?) = getExistingElements(getTeamStorage().getAll(), date)

