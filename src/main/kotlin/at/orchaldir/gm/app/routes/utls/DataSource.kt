package at.orchaldir.gm.app.routes.utls

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.Column
import at.orchaldir.gm.app.html.createNameColumn
import at.orchaldir.gm.app.html.tdInt
import at.orchaldir.gm.app.html.tdSkipZero
import at.orchaldir.gm.app.html.util.source.editDataSource
import at.orchaldir.gm.app.html.util.source.parseDataSource
import at.orchaldir.gm.app.html.util.source.showDataSource
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.util.SortDataSource
import at.orchaldir.gm.core.model.util.source.DATA_SOURCE_TYPE
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.selector.util.sortDataSources
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$DATA_SOURCE_TYPE")
class DataSourceRoutes : Routes<DataSourceId, SortDataSource> {
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
    override fun preview(call: ApplicationCall, id: DataSourceId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: DataSourceId) = call.application.href(Update(id))
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
            handleCreateElement(DataSourceRoutes(), STORE.getState().getDataSourceStorage())
        }
        get<DataSourceRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, DataSourceRoutes())
        }
        get<DataSourceRoutes.Edit> { edit ->
            handleEditElement(edit.id, DataSourceRoutes(), HtmlBlockTag::editDataSource)
        }
        post<DataSourceRoutes.Preview> { preview ->
            handlePreviewElement(preview.id, DataSourceRoutes(), ::parseDataSource, HtmlBlockTag::editDataSource)
        }
        post<DataSourceRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseDataSource)
        }
    }
}
