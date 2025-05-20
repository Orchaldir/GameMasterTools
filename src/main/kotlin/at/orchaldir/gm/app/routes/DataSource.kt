package at.orchaldir.gm.app.routes

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.util.editDataSource
import at.orchaldir.gm.app.html.model.util.parseDataSource
import at.orchaldir.gm.app.html.model.util.showDataSource
import at.orchaldir.gm.core.action.CreateDataSource
import at.orchaldir.gm.core.action.DeleteDataSource
import at.orchaldir.gm.core.action.UpdateDataSource
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.source.DATA_SOURCE_TYPE
import at.orchaldir.gm.core.model.util.source.DataSource
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.util.SortDataSource
import at.orchaldir.gm.core.selector.source.canDeleteDataSource
import at.orchaldir.gm.core.selector.util.sortDataSources
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$DATA_SOURCE_TYPE")
class DataSourceRoutes {
    @Resource("all")
    class All(
        val sort: SortDataSource = SortDataSource.Name,
        val parent: DataSourceRoutes = DataSourceRoutes(),
    )

    @Resource("details")
    class Details(val id: DataSourceId, val parent: DataSourceRoutes = DataSourceRoutes())

    @Resource("new")
    class New(val parent: DataSourceRoutes = DataSourceRoutes())

    @Resource("delete")
    class Delete(val id: DataSourceId, val parent: DataSourceRoutes = DataSourceRoutes())

    @Resource("edit")
    class Edit(val id: DataSourceId, val parent: DataSourceRoutes = DataSourceRoutes())

    @Resource("preview")
    class Preview(val id: DataSourceId, val parent: DataSourceRoutes = DataSourceRoutes())

    @Resource("update")
    class Update(val id: DataSourceId, val parent: DataSourceRoutes = DataSourceRoutes())
}

fun Application.configureDataSourceRouting() {
    routing {
        get<DataSourceRoutes.All> { all ->
            logger.info { "Get all source" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllDataSources(call, STORE.getState(), all.sort)
            }
        }
        get<DataSourceRoutes.Details> { details ->
            logger.info { "Get details of source ${details.id.value}" }

            val state = STORE.getState()
            val source = state.getDataSourceStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showDataSourceDetails(call, state, source)
            }
        }
        get<DataSourceRoutes.New> {
            logger.info { "Add new source" }

            STORE.dispatch(CreateDataSource)

            call.respondRedirect(
                call.application.href(
                    DataSourceRoutes.Edit(
                        STORE.getState().getDataSourceStorage().lastId
                    )
                )
            )

            STORE.getState().save()
        }
        get<DataSourceRoutes.Delete> { delete ->
            logger.info { "Delete source ${delete.id.value}" }

            STORE.dispatch(DeleteDataSource(delete.id))

            call.respondRedirect(call.application.href(DataSourceRoutes.All()))

            STORE.getState().save()
        }
        get<DataSourceRoutes.Edit> { edit ->
            logger.info { "Get editor for source ${edit.id.value}" }

            val state = STORE.getState()
            val source = state.getDataSourceStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showDataSourceEditor(call, state, source)
            }
        }
        post<DataSourceRoutes.Preview> { preview ->
            logger.info { "Preview source ${preview.id.value}" }

            val state = STORE.getState()
            val source = parseDataSource(call.receiveParameters(), state, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showDataSourceEditor(call, state, source)
            }
        }
        post<DataSourceRoutes.Update> { update ->
            logger.info { "Update source ${update.id.value}" }

            val source = parseDataSource(call.receiveParameters(), STORE.getState(), update.id)

            STORE.dispatch(UpdateDataSource(source))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllDataSources(
    call: ApplicationCall,
    state: State,
    sort: SortDataSource,
) {
    val qsources = state.sortDataSources(sort)
    val createLink = call.application.href(DataSourceRoutes.New())

    simpleHtml("Data Sources") {
        field("Count", qsources.size)
        showSortTableLinks(call, SortDataSource.entries, DataSourceRoutes(), DataSourceRoutes::All)
        table {
            tr {
                th { +"Name" }
                th { +"Year" }
                th { +"Edition" }
            }
            qsources.forEach { source ->
                tr {
                    tdLink(call, state, source)
                    td { +source.year.toString() }
                    td { +(source.edition?.toString() ?: "") }
                }
            }
        }
        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showDataSourceDetails(
    call: ApplicationCall,
    state: State,
    source: DataSource,
) {
    val backLink = call.application.href(DataSourceRoutes.All())
    val deleteLink = call.application.href(DataSourceRoutes.Delete(source.id))
    val editLink = call.application.href(DataSourceRoutes.Edit(source.id))

    simpleHtmlDetails(source) {
        showDataSource(call, state, source)

        action(editLink, "Edit")
        if (state.canDeleteDataSource(source.id)) {
            action(deleteLink, "Delete")
        }
        back(backLink)
    }
}

private fun HTML.showDataSourceEditor(
    call: ApplicationCall,
    state: State,
    source: DataSource,
) {
    val backLink = href(call, source.id)
    val previewLink = call.application.href(DataSourceRoutes.Preview(source.id))
    val updateLink = call.application.href(DataSourceRoutes.Update(source.id))

    simpleHtmlEditor(source) {
        formWithPreview(previewLink, updateLink, backLink) {
            editDataSource(state, source)
        }
    }
}
