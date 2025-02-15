package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.selector.economy.getBusinessesFoundedBy
import at.orchaldir.gm.core.selector.getLanguagesInventedBy
import at.orchaldir.gm.core.selector.getRacesCreatedBy
import at.orchaldir.gm.core.selector.isAlive
import at.orchaldir.gm.core.selector.item.getTextsTranslatedBy
import at.orchaldir.gm.core.selector.item.getTextsWrittenBy
import at.orchaldir.gm.core.selector.util.exists
import at.orchaldir.gm.core.selector.world.getBuildingsBuildBy
import at.orchaldir.gm.core.selector.world.getTownsFoundedBy
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

        is CreatedByCharacter -> {
            state.getCharacterStorage()
                .require(creator.character) { "Cannot use an unknown character ${creator.character.value} as $noun!" }

            if (date != null) {
                require(state.isAlive(creator.character, date)) {
                    "$noun (character ${creator.character.value}) is not alive!"
                }
            }
        }

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

fun <ID : Id<ID>> checkIfCreatorCanBeDeleted(
    state: State,
    id: ID,
    noun: String,
) {
    val buildings = state.getBuildingsBuildBy(id)
    require(buildings.isEmpty()) { "Cannot delete $noun ${id.value()}, because of built buildings!" }
    val businesses = state.getBusinessesFoundedBy(id)
    require(businesses.isEmpty()) { "Cannot delete $noun ${id.value()}, because of founded businesses!" }
    val languages = state.getLanguagesInventedBy(id)
    require(languages.isEmpty()) { "Cannot delete $noun ${id.value()}, because of invented languages!" }
    val races = state.getRacesCreatedBy(id)
    require(races.isEmpty()) { "Cannot delete $noun ${id.value()}, because of created race!" }
    val towns = state.getTownsFoundedBy(id)
    require(towns.isEmpty()) { "Cannot delete $noun ${id.value()}, because of founded towns!" }
    val writtenTexts = state.getTextsWrittenBy(id)
    require(writtenTexts.isEmpty()) { "Cannot delete $noun ${id.value()}, who is an author!" }
    val translatedTexts = state.getTextsTranslatedBy(id)
    require(translatedTexts.isEmpty()) { "Cannot delete $noun ${id.value()}, who is a translator!" }
}
