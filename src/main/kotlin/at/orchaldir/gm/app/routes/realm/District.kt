package at.orchaldir.gm.app.routes.realm

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.createCreatorColumn
import at.orchaldir.gm.app.html.createIdColumn
import at.orchaldir.gm.app.html.createNameColumn
import at.orchaldir.gm.app.html.createPopulationColumn
import at.orchaldir.gm.app.html.createStartDateColumn
import at.orchaldir.gm.app.html.formWithPreview
import at.orchaldir.gm.app.html.href
import at.orchaldir.gm.app.html.realm.editDistrict
import at.orchaldir.gm.app.html.realm.parseDistrict
import at.orchaldir.gm.app.html.realm.showDistrict
import at.orchaldir.gm.app.html.showCreatorCount
import at.orchaldir.gm.app.html.simpleHtmlEditor
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.DISTRICT_TYPE
import at.orchaldir.gm.core.model.realm.District
import at.orchaldir.gm.core.model.realm.DistrictId
import at.orchaldir.gm.core.model.util.SortDistrict
import at.orchaldir.gm.core.selector.util.sortDistricts
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HTML
import kotlinx.html.HtmlBlockTag
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$DISTRICT_TYPE")
class DistrictRoutes : Routes<DistrictId, SortDistrict> {
    @Resource("all")
    class All(
        val sort: SortDistrict = SortDistrict.Name,
        val parent: DistrictRoutes = DistrictRoutes(),
    )

    @Resource("details")
    class Details(val id: DistrictId, val parent: DistrictRoutes = DistrictRoutes())

    @Resource("new")
    class New(val parent: DistrictRoutes = DistrictRoutes())

    @Resource("delete")
    class Delete(val id: DistrictId, val parent: DistrictRoutes = DistrictRoutes())

    @Resource("edit")
    class Edit(val id: DistrictId, val parent: DistrictRoutes = DistrictRoutes())

    @Resource("preview")
    class Preview(val id: DistrictId, val parent: DistrictRoutes = DistrictRoutes())

    @Resource("update")
    class Update(val id: DistrictId, val parent: DistrictRoutes = DistrictRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortDistrict) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: DistrictId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: DistrictId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
}

fun Application.configureDistrictRouting() {
    routing {
        get<DistrictRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                DistrictRoutes(),
                state.sortDistricts(all.sort),
                listOf(
                    createNameColumn(call, state),
                    createIdColumn(call, state, "Town", District::town),
                    createStartDateColumn(call, state),
                    createCreatorColumn(call, state, "Founder"),
                    createPopulationColumn(),
                ),
            ) {
                showCreatorCount(call, state, it, "Creators")
            }
        }
        get<DistrictRoutes.Details> { details ->
            handleShowElement(details.id, DistrictRoutes(), HtmlBlockTag::showDistrict)
        }
        get<DistrictRoutes.New> {
            handleCreateElement(STORE.getState().getDistrictStorage()) { id ->
                DistrictRoutes.Edit(id)
            }
        }
        get<DistrictRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, DistrictRoutes.All())
        }
        get<DistrictRoutes.Edit> { edit ->
            logger.info { "Get editor for legal code ${edit.id.value}" }

            val state = STORE.getState()
            val code = state.getDistrictStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showDistrictEditor(call, state, code)
            }
        }
        post<DistrictRoutes.Preview> { preview ->
            logger.info { "Get preview for legal code ${preview.id.value}" }

            val formParameters = call.receiveParameters()
            val state = STORE.getState()
            val code = parseDistrict(state, formParameters, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showDistrictEditor(call, state, code)
            }
        }
        post<DistrictRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseDistrict)
        }
    }
}

private fun HTML.showDistrictEditor(
    call: ApplicationCall,
    state: State,
    code: District,
) {
    val backLink = href(call, code.id)
    val previewLink = call.application.href(DistrictRoutes.Preview(code.id))
    val updateLink = call.application.href(DistrictRoutes.Update(code.id))

    simpleHtmlEditor(code) {
        formWithPreview(previewLink, updateLink, backLink) {
            editDistrict(call, state, code)
        }
    }
}
