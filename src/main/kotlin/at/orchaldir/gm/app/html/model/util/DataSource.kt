package at.orchaldir.gm.app.html.model.util

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.SOURCE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.source.DataSource
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.util.source.HasDataSources
import at.orchaldir.gm.core.selector.util.sortDataSources
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.showDataSource(
    call: ApplicationCall,
    state: State,
    source: DataSource,
) {
    field("Year", source.year)
    optionalField("Edition", source.edition)

    h2 { +"Content" }

    showDataSourceContent(call, state, state.getBattleStorage(), source.id)
    showDataSourceContent(call, state, state.getBusinessStorage(), source.id)
    showDataSourceContent(call, state, state.getCatastropheStorage(), source.id)
    showDataSourceContent(call, state, state.getCultureStorage(), source.id)
    showDataSourceContent(call, state, state.getCharacterStorage(), source.id)
    showDataSourceContent(call, state, state.getGodStorage(), source.id)
    showDataSourceContent(call, state, state.getMagicTraditionStorage(), source.id)
    showDataSourceContent(call, state, state.getOrganizationStorage(), source.id)
    showDataSourceContent(call, state, state.getPlaneStorage(), source.id)
    showDataSourceContent(call, state, state.getRaceStorage(), source.id)
    showDataSourceContent(call, state, state.getRealmStorage(), source.id)
    showDataSourceContent(call, state, state.getSpellStorage(), source.id)
    showDataSourceContent(call, state, state.getTextStorage(), source.id)
    showDataSourceContent(call, state, state.getTownStorage(), source.id)
    showDataSourceContent(call, state, state.getTreatyStorage(), source.id)
    showDataSourceContent(call, state, state.getWarStorage(), source.id)
}


fun <ID : Id<ID>, ELEMENT> HtmlBlockTag.showDataSourceContent(
    call: ApplicationCall,
    state: State,
    storage: Storage<ID, ELEMENT>,
    source: DataSourceId,
) where
        ELEMENT : Element<ID>,
        ELEMENT : HasDataSources {
    val elements = storage.getAll()
        .filter { it.sources().contains(source) }

    fieldList(call, state, elements)
}

fun HtmlBlockTag.showDataSources(
    call: ApplicationCall,
    state: State,
    sources: Set<DataSourceId>,
) {
    fieldIdList(call, state, sources)
}

// edit

fun HtmlBlockTag.editDataSource(state: State, source: DataSource) {
    selectName(source.name)
    selectInt("Year", source.year, 1900, 3000, 1, DATE)
    selectOptionalInt("Edition", source.edition, 0, 100, 1, NUMBER)
}

fun HtmlBlockTag.editDataSources(state: State, sources: Set<DataSourceId>) {
    selectElements(state, "Data Sources", SOURCE, state.sortDataSources(), sources)
}

// parse

fun parseDataSourceId(value: String) = DataSourceId(value.toInt())
fun parseDataSourceId(parameters: Parameters, param: String) = DataSourceId(parseInt(parameters, param))

fun parseDataSource(parameters: Parameters, state: State, id: DataSourceId) = DataSource(
    id,
    parseName(parameters),
    parseInt(parameters, DATE),
    parseOptionalInt(parameters, NUMBER, 0),
)

fun parseDataSources(parameters: Parameters) =
    parseElements(parameters, SOURCE, ::parseDataSourceId)
