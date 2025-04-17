package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.Created
import at.orchaldir.gm.core.selector.economy.getBusinessesFoundedBy
import at.orchaldir.gm.core.selector.getLanguagesInventedBy
import at.orchaldir.gm.core.selector.getRacesCreatedBy
import at.orchaldir.gm.core.selector.item.getTextsTranslatedBy
import at.orchaldir.gm.core.selector.item.getTextsWrittenBy
import at.orchaldir.gm.core.selector.magic.getSpellsCreatedBy
import at.orchaldir.gm.core.selector.organization.getOrganizationsFoundedBy
import at.orchaldir.gm.core.selector.world.getBuildingsBuildBy
import at.orchaldir.gm.core.selector.world.getPrisonPlanesCreatedBy
import at.orchaldir.gm.core.selector.world.getTownsFoundedBy
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage

fun <ID : Id<ID>> State.isCreator(id: ID) = getBuildingsBuildBy(id).isNotEmpty()
        || getBusinessesFoundedBy(id).isNotEmpty()
        || getLanguagesInventedBy(id).isNotEmpty()
        || getOrganizationsFoundedBy(id).isNotEmpty()
        || isCreator(getPeriodicalStorage(), id)
        || getPrisonPlanesCreatedBy(id).isNotEmpty()
        || getRacesCreatedBy(id).isNotEmpty()
        || getSpellsCreatedBy(id).isNotEmpty()
        || getTextsWrittenBy(id).isNotEmpty()
        || getTextsTranslatedBy(id).isNotEmpty()
        || getTownsFoundedBy(id).isNotEmpty()

fun <ID : Id<ID>, ELEMENT, CREATOR : Id<CREATOR>> isCreator(storage: Storage<ID, ELEMENT>, id: CREATOR) where
        ELEMENT : Element<ID>,
        ELEMENT : Created = storage
    .getAll()
    .any { it.creator().isId(id) }

fun <ID : Id<ID>, ELEMENT, CREATOR : Id<CREATOR>> getCreatedBy(storage: Storage<ID, ELEMENT>, creator: CREATOR) where
        ELEMENT : Element<ID>,
        ELEMENT : Created = storage
    .getAll()
    .filter { it.creator().isId(creator) }

fun <ELEMENT : Created> countEachCreator(collection: Collection<ELEMENT>) = collection
    .groupingBy { it.creator() }
    .eachCount()