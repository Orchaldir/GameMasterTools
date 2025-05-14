package at.orchaldir.gm.core.selector.source

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.source.DataSourceId
import at.orchaldir.gm.core.model.source.HasDataSources
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage

fun State.canDeleteDataSource(source: DataSourceId) =
    canDeleteDataSource(getBusinessStorage(), source) &&
            canDeleteDataSource(getCultureStorage(), source) &&
            canDeleteDataSource(getCharacterStorage(), source) &&
            canDeleteDataSource(getGodStorage(), source) &&
            canDeleteDataSource(getMagicTraditionStorage(), source) &&
            canDeleteDataSource(getOrganizationStorage(), source) &&
            canDeleteDataSource(getPlaneStorage(), source) &&
            canDeleteDataSource(getRaceStorage(), source) &&
            canDeleteDataSource(getSpellStorage(), source) &&
            canDeleteDataSource(getTownStorage(), source)

fun <ID : Id<ID>, ELEMENT> canDeleteDataSource(
    storage: Storage<ID, ELEMENT>,
    source: DataSourceId,
) where
        ELEMENT : Element<ID>,
        ELEMENT : HasDataSources =
    storage.getAll()
        .none { it.sources().contains(source) }
