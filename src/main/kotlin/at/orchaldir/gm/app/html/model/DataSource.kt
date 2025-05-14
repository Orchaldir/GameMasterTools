package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.SOURCE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.source.DataSource
import at.orchaldir.gm.core.model.source.DataSourceId
import at.orchaldir.gm.core.selector.util.sortDataSources
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
}

fun HtmlBlockTag.showDataSources(
    call: ApplicationCall,
    state: State,
    sources: Set<DataSourceId>,
) {
    if (sources.isNotEmpty()) {
        h2 { +"Usage" }

        showIdList(call, state, sources)
    }
}

// edit

fun HtmlBlockTag.editDataSource(state: State, source: DataSource) {
    selectName(source.name)
    selectInt("Year", source.year, 1900, 3000, 1, DATE)
    selectOptionalInt("Edition", source.edition, 0, 100, 1, NUMBER)
}

fun HtmlBlockTag.editDataSources(state: State, sources: Set<DataSourceId>) {
    h2 { +"Data Sources" }

    selectElements(state, SOURCE, state.sortDataSources(), sources)
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
