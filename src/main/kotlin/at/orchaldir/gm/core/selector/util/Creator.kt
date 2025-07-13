package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage

fun <ID : Id<ID>> State.isCreator(id: ID) = isCreator(getArticleStorage(), id)
        || isCreator(getBuildingStorage(), id)
        || isCreator(getBusinessStorage(), id)
        || isCreator(getCatastropheStorage(), id)
        || isCreator(getDiseaseStorage(), id)
        || isCreator(getLanguageStorage(), id)
        || isCreator(getMagicTraditionStorage(), id)
        || isCreator(getOrganizationStorage(), id)
        || isCreator(getPlaneStorage(), id)
        || isCreator(getQuoteStorage(), id)
        || isCreator(getRealmStorage(), id)
        || isCreator(getRaceStorage(), id)
        || isCreator(getSpellStorage(), id)
        || isCreator(getTextStorage(), id)
        || isCreator(getTownStorage(), id)
        || isCreator(getTreatyStorage(), id)

fun <ID : Id<ID>, ELEMENT, CREATOR : Id<CREATOR>> isCreator(storage: Storage<ID, ELEMENT>, id: CREATOR) where
        ELEMENT : Element<ID>,
        ELEMENT : ComplexCreation = storage
    .getAll()
    .any { it.isCreatedBy(id) }

fun <ID : Id<ID>, ELEMENT, CREATOR : Id<CREATOR>> getCreatedBy(storage: Storage<ID, ELEMENT>, creator: CREATOR) where
        ELEMENT : Element<ID>,
        ELEMENT : ComplexCreation = storage
    .getAll()
    .filter { it.isCreatedBy(creator) }

fun <ELEMENT : Creation> countEachCreator(collection: Collection<ELEMENT>) = collection
    .groupingBy { it.creator() }
    .eachCount()

fun <ID : Id<ID>> checkIfCreatorCanBeDeleted(
    state: State,
    creator: ID,
) {
    val noun = creator.type()

    checkCreator(state.getArticleStorage(), noun, creator)
    checkCreator(state.getBuildingStorage(), noun, creator)
    checkCreator(state.getBusinessStorage(), noun, creator)
    checkCreator(state.getCatastropheStorage(), noun, creator)
    checkCreator(state.getDiseaseStorage(), noun, creator)
    checkCreator(state.getLanguageStorage(), noun, creator)
    checkCreator(state.getMagicTraditionStorage(), noun, creator)
    checkCreator(state.getQuoteStorage(), noun, creator)
    checkCreator(state.getRaceStorage(), noun, creator)
    checkCreator(state.getRealmStorage(), noun, creator)
    checkCreator(state.getSpellStorage(), noun, creator)
    checkCreator(state.getTextStorage(), noun, creator)
    checkCreator(state.getTownStorage(), noun, creator)
    checkCreator(state.getTreatyStorage(), noun, creator)
}

private fun <ID0, ID1, ELEMENT> checkCreator(
    storage: Storage<ID0, ELEMENT>,
    creatorNoun: String,
    creator: ID1,
) where ID0 : Id<ID0>,
        ID1 : Id<ID1>,
        ELEMENT : Element<ID0>,
        ELEMENT : ComplexCreation {
    val createdNoun = storage.getType()
    require(
        !isCreator(
            storage,
            creator
        )
    ) { "Cannot delete $creatorNoun ${creator.value()}, because of created elements ($createdNoun)!" }
}

fun State.getCreatorName(
    creator: Creator,
) = when (creator) {
    is CreatedByBusiness -> getElementName(creator.business)
    is CreatedByCharacter -> getElementName(creator.character)
    is CreatedByCulture -> getElementName(creator.culture)
    is CreatedByGod -> getElementName(creator.god)
    is CreatedByOrganization -> getElementName(creator.organization)
    is CreatedByRealm -> getElementName(creator.realm)
    is CreatedByTown -> getElementName(creator.town)
    UndefinedCreator -> null
}