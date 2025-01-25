package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.Created
import at.orchaldir.gm.core.selector.economy.getBusinessesFoundedBy
import at.orchaldir.gm.core.selector.getLanguagesInventedBy
import at.orchaldir.gm.core.selector.item.getTextsTranslatedBy
import at.orchaldir.gm.core.selector.item.getTextsWrittenBy
import at.orchaldir.gm.core.selector.organization.getOrganizationsFoundedBy
import at.orchaldir.gm.core.selector.world.getBuildingsBuildBy
import at.orchaldir.gm.core.selector.world.getTownsFoundedBy
import at.orchaldir.gm.utils.Id

fun <ID : Id<ID>> State.isCreator(id: ID) = getBuildingsBuildBy(id).isNotEmpty()
        || getBusinessesFoundedBy(id).isNotEmpty()
        || getLanguagesInventedBy(id).isNotEmpty()
        || getOrganizationsFoundedBy(id).isNotEmpty()
        || getTextsWrittenBy(id).isNotEmpty()
        || getTextsTranslatedBy(id).isNotEmpty()
        || getTownsFoundedBy(id).isNotEmpty()

fun <ELEMENT : Created> countEachCreator(collection: Collection<ELEMENT>) = collection
    .groupingBy { it.creator() }
    .eachCount()