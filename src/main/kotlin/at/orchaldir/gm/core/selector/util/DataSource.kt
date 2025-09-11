package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.util.source.HasDataSources
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage

fun State.canDeleteDataSource(source: DataSourceId) = DeleteResult(source)
    .addElements(getElementsInDataSource(getBusinessStorage(), source))
    .addElements(getElementsInDataSource(getCatastropheStorage(), source))
    .addElements(getElementsInDataSource(getCultureStorage(), source))
    .addElements(getElementsInDataSource(getCharacterStorage(), source))
    .addElements(getElementsInDataSource(getDiseaseStorage(), source))
    .addElements(getElementsInDataSource(getGodStorage(), source))
    .addElements(getElementsInDataSource(getMagicTraditionStorage(), source))
    .addElements(getElementsInDataSource(getOrganizationStorage(), source))
    .addElements(getElementsInDataSource(getPlaneStorage(), source))
    .addElements(getElementsInDataSource(getRaceStorage(), source))
    .addElements(getElementsInDataSource(getRealmStorage(), source))
    .addElements(getElementsInDataSource(getSpellStorage(), source))
    .addElements(getElementsInDataSource(getTextStorage(), source))
    .addElements(getElementsInDataSource(getTownStorage(), source))
    .addElements(getElementsInDataSource(getTreatyStorage(), source))
    .addElements(getElementsInDataSource(getWarStorage(), source))

fun <ID : Id<ID>, ELEMENT> getElementsInDataSource(
    storage: Storage<ID, ELEMENT>,
    source: DataSourceId,
) where
        ELEMENT : Element<ID>,
        ELEMENT : HasDataSources =
    storage
        .getAll()
        .filter { it.sources().contains(source) }
