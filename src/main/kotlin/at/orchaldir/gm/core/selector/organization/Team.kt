package at.orchaldir.gm.core.selector.organization

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.organization.TeamId

fun State.canDeleteTeam(team: TeamId) = false

