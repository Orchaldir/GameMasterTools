package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.selector.util.exists
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing

fun <ID : Id<ID>> validateCreator(
    state: State,
    creator: Creator,
    created: ID,
    date: Date?,
    noun: String,
) {
    when (creator) {
        is CreatedByBusiness -> checkCreatorElement(state, creator.business, created, noun, "business", date)

        is CreatedByCharacter -> checkCreatorElement(
            state,
            creator.character,
            created,
            noun,
            "character",
            date
        )

        is CreatedByGod -> checkCreatorElement(
            state,
            creator.god,
            created,
            noun,
            "god",
            date
        )

        is CreatedByOrganization -> checkCreatorElement(
            state,
            creator.organization,
            created,
            noun,
            "organization",
            date
        )

        is CreatedByTown -> checkCreatorElement(state, creator.town, created, noun, "town", date)

        UndefinedCreator -> doNothing()
    }
}

private fun <ID0, ID1, ELEMENT> checkCreatorElement(
    state: State,
    creator: ID0,
    created: ID1,
    noun: String,
    typeNoun: String,
    date: Date?,
) where ID0 : Id<ID0>, ID1 : Id<ID1>, ELEMENT : Element<ID0>, ELEMENT : HasStartDate {
    require(creator != created) { "The $typeNoun cannot create itself!" }
    val element = state
        .getStorage<ID0, ELEMENT>(creator)
        .getOrThrow(creator) { "Cannot use an unknown $typeNoun ${creator.value()} as $noun!" }

    require(state.exists(element, date)) { "$noun ($typeNoun ${creator.value()}) does not exist!" }
}

