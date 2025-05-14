package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.source.DataSource
import at.orchaldir.gm.core.model.source.DataSourceId
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showDataSource(
    call: ApplicationCall,
    state: State,
    source: DataSource,
) {
    field("Year", source.year)
    optionalField("Edition", source.edition)
}

// edit

fun HtmlBlockTag.editDataSource(state: State, source: DataSource) {
    selectName(source.name)
    selectInt("Year", source.year, 1900, 3000, 1, DATE)
    selectOptionalInt("Edition", source.edition, 0, 100, 1, NUMBER)
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
