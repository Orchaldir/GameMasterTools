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
        is CreatedByBusiness -> checkCreatorElement(state, creator.business, created, noun, date)
        is CreatedByCharacter -> checkCreatorElement(state, creator.character, created, noun, date)
        is CreatedByCulture -> checkCreatorElement(state, creator.culture, created, noun, date)
        is CreatedByGod -> checkCreatorElement(state, creator.god, created, noun, date)
        is CreatedByOrganization -> checkCreatorElement(state, creator.organization, created, noun, date)
        is CreatedByRealm -> checkCreatorElement(state, creator.realm, created, noun, date)
        is CreatedByTown -> checkCreatorElement(state, creator.town, created, noun, date)
        UndefinedCreator -> doNothing()
    }
}

private fun <ID0, ID1, ELEMENT> checkCreatorElement(
    state: State,
    creator: ID0,
    created: ID1,
    noun: String,
    date: Date?,
) where ID0 : Id<ID0>, ID1 : Id<ID1>, ELEMENT : Element<ID0>, ELEMENT : HasStartDate {
    val typeNoun = creator.type()

    require(creator != created) { "The $typeNoun cannot create itself!" }
    val element = state
        .getStorage<ID0, ELEMENT>(creator)
        .getOrThrow(creator) { "Cannot use an unknown ${creator.print()} as $noun!" }

    require(state.exists(element, date)) { "$noun (${creator.print()}) does not exist!" }
}

