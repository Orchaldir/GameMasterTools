package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.util.source.HasDataSources
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage

fun State.canDeleteDataSource(id: DataSourceId) = DeleteResult(id)
    .addElements(getElementsInDataSource(getBusinessStorage(), id))
    .addElements(getElementsInDataSource(getCatastropheStorage(), id))
    .addElements(getElementsInDataSource(getCultureStorage(), id))
    .addElements(getElementsInDataSource(getCharacterStorage(), id))
    .addElements(getElementsInDataSource(getCharacterTemplateStorage(), id))
    .addElements(getElementsInDataSource(getDiseaseStorage(), id))
    .addElements(getElementsInDataSource(getGodStorage(), id))
    .addElements(getElementsInDataSource(getMagicTraditionStorage(), id))
    .addElements(getElementsInDataSource(getOrganizationStorage(), id))
    .addElements(getElementsInDataSource(getPlaneStorage(), id))
    .addElements(getElementsInDataSource(getRaceStorage(), id))
    .addElements(getElementsInDataSource(getRealmStorage(), id))
    .addElements(getElementsInDataSource(getSpellStorage(), id))
    .addElements(getElementsInDataSource(getTextStorage(), id))
    .addElements(getElementsInDataSource(getTownStorage(), id))
    .addElements(getElementsInDataSource(getTreatyStorage(), id))
    .addElements(getElementsInDataSource(getWarStorage(), id))

fun <ID : Id<ID>, ELEMENT> getElementsInDataSource(
    storage: Storage<ID, ELEMENT>,
    source: DataSourceId,
) where
        ELEMENT : Element<ID>,
        ELEMENT : HasDataSources =
    storage
        .getAll()
        .filter { it.sources().contains(source) }
