package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.ComplexCreation
import at.orchaldir.gm.core.model.util.Creation
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage

fun <ID : Id<ID>> State.canDeleteCreator(id: ID, result: DeleteResult) = result
    .addElements(getCreatedBy(getArticleStorage(), id))
    .addElements(getCreatedBy(getBuildingStorage(), id))
    .addElements(getCreatedBy(getBusinessStorage(), id))
    .addElements(getCreatedBy(getCatastropheStorage(), id))
    .addElements(getCreatedBy(getDiseaseStorage(), id))
    .addElements(getCreatedBy(getLanguageStorage(), id))
    .addElements(getCreatedBy(getMagicTraditionStorage(), id))
    .addElements(getCreatedBy(getOrganizationStorage(), id))
    .addElements(getCreatedBy(getPlaneStorage(), id))
    .addElements(getCreatedBy(getQuoteStorage(), id))
    .addElements(getCreatedBy(getRealmStorage(), id))
    .addElements(getCreatedBy(getRaceStorage(), id))
    .addElements(getCreatedBy(getSpellStorage(), id))
    .addElements(getCreatedBy(getTextStorage(), id))
    .addElements(getCreatedBy(getTownStorage(), id))
    .addElements(getCreatedBy(getTreatyStorage(), id))

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
