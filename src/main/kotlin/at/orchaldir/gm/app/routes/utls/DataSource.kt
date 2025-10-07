package at.orchaldir.gm.app.routes.utls

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.source.editDataSource
import at.orchaldir.gm.app.html.util.source.parseDataSource
import at.orchaldir.gm.app.html.util.source.showDataSource
import at.orchaldir.gm.app.routes.Routes
import at.orchaldir.gm.app.routes.handleCreateElement
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.app.routes.handleShowAllElements
import at.orchaldir.gm.app.routes.handleShowElement
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.app.routes.magic.MagicTraditionRoutes.All
import at.orchaldir.gm.app.routes.magic.MagicTraditionRoutes.New
import at.orchaldir.gm.app.routes.religion.PantheonRoutes
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.SortDataSource
import at.orchaldir.gm.core.model.util.SortMagicTradition
import at.orchaldir.gm.core.model.util.source.DATA_SOURCE_TYPE
import at.orchaldir.gm.core.model.util.source.DataSource
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.selector.util.sortDataSources
import at.orchaldir.gm.core.selector.util.sortPantheons
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$DATA_SOURCE_TYPE")
class DataSourceRoutes : Routes<DataSourceId,SortDataSource> {
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

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortDataSource) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: DataSourceId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: DataSourceId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
}

fun Application.configureDataSourceRouting() {
    routing {
        get<DataSourceRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                DataSourceRoutes(),
                state.sortDataSources(all.sort),
                listOf(
                    createNameColumn(call, state),
                    Column("Year") { tdInt(it.year) },
                    Column("Edition") { tdSkipZero(it.edition) },
                ),
            )
        }
        get<DataSourceRoutes.Details> { details ->
            handleShowElement(details.id, DataSourceRoutes(), HtmlBlockTag::showDataSource)
        }
        get<DataSourceRoutes.New> {
            handleCreateElement(STORE.getState().getDataSourceStorage()) { id ->
                DataSourceRoutes.Edit(id)
            }
        }
        get<DataSourceRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, DataSourceRoutes.All())
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
            val source = parseDataSource(state, call.receiveParameters(), preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showDataSourceEditor(call, state, source)
            }
        }
        post<DataSourceRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseDataSource)
        }
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
