package at.orchaldir.gm.core.model.event

import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.Owner
import at.orchaldir.gm.utils.Id

sealed class Event<ID : Id<ID>>(
    val date: Date,
    val id: ID,
)

class StartEvent<ID : Id<ID>>(
    date: Date,
    id: ID,
) : Event<ID>(date, id)

class EndEvent<ID : Id<ID>>(
    date: Date,
    id: ID,
) : Event<ID>(date, id)

class OwnershipChangedEvent<ID : Id<ID>>(
    date: Date,
    id: ID,
    val from: Owner,
    val to: Owner,
) : Event<ID>(date, id)
