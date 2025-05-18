package at.orchaldir.gm.core.model.event

import at.orchaldir.gm.core.model.time.date.Date
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

class SameStartAndEndEvent<ID : Id<ID>>(
    date: Date,
    id: ID,
) : Event<ID>(date, id)

enum class HistoryEventType {
    Capital,
    Currency,
    Employment,
    LegalCode,
    OwnerRealm,
    Ownership,
}

class HistoryEvent<ID : Id<ID>, T>(
    date: Date,
    id: ID,
    val type: HistoryEventType,
    val from: T,
    val to: T,
) : Event<ID>(date, id)
