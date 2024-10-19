package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyleId
import at.orchaldir.gm.core.model.world.building.StreetAddress
import at.orchaldir.gm.core.model.world.building.TownAddress
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.town.TownId

fun State.getRevivedBy(style: ArchitecturalStyleId) = getArchitecturalStyleStorage()
    .getAll()
    .filter { it.revival == style }

