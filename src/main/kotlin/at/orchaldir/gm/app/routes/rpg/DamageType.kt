package at.orchaldir.gm.app.routes.rpg

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.rpg.editDamageType
import at.orchaldir.gm.app.html.rpg.parseDamageType
import at.orchaldir.gm.app.html.rpg.showDamageType
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.DAMAGE_TYPE_TYPE
import at.orchaldir.gm.core.model.rpg.DamageType
import at.orchaldir.gm.core.model.rpg.DamageTypeId
import at.orchaldir.gm.core.model.util.SortDamageType
import at.orchaldir.gm.core.selector.util.sortDamageTypes
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

@Resource("/$DAMAGE_TYPE_TYPE")
class DamageTypeRoutes : Routes<DamageTypeId, SortDamageType> {
    @Resource("all")
    class All(
        val sort: SortDamageType = SortDamageType.Name,
        val parent: DamageTypeRoutes = DamageTypeRoutes(),
    )

    @Resource("details")
    class Details(val id: DamageTypeId, val parent: DamageTypeRoutes = DamageTypeRoutes())

    @Resource("new")
    class New(val parent: DamageTypeRoutes = DamageTypeRoutes())

    @Resource("delete")
    class Delete(val id: DamageTypeId, val parent: DamageTypeRoutes = DamageTypeRoutes())

    @Resource("edit")
    class Edit(val id: DamageTypeId, val parent: DamageTypeRoutes = DamageTypeRoutes())

    @Resource("preview")
    class Preview(val id: DamageTypeId, val parent: DamageTypeRoutes = DamageTypeRoutes())

    @Resource("update")
    class Update(val id: DamageTypeId, val parent: DamageTypeRoutes = DamageTypeRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortDamageType) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: DamageTypeId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: DamageTypeId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
}

fun Application.configureDamageTypeRouting() {
    routing {
        get<DamageTypeRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                DamageTypeRoutes(),
                state.sortDamageTypes(all.sort),
                listOf(
                    createNameColumn(call, state),
                    Column("Short") { tdString(it.short) },
                ),
            )
        }
        get<DamageTypeRoutes.Details> { details ->
            handleShowElement(details.id, DamageTypeRoutes(), HtmlBlockTag::showDamageType)
        }
        get<DamageTypeRoutes.New> {
            handleCreateElement(STORE.getState().getDamageTypeStorage()) { id ->
                DamageTypeRoutes.Edit(id)
            }
        }
        get<DamageTypeRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, DamageTypeRoutes.All())
        }
        get<DamageTypeRoutes.Edit> { edit ->
            logger.info { "Get editor for type ${edit.id.value}" }

            val state = STORE.getState()
            val type = state.getDamageTypeStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showDamageTypeEditor(call, state, type)
            }
        }
        post<DamageTypeRoutes.Preview> { preview ->
            logger.info { "Get preview for type ${preview.id.value}" }

            val formParameters = call.receiveParameters()
            val state = STORE.getState()
            val type = parseDamageType(state, formParameters, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showDamageTypeEditor(call, state, type)
            }
        }
        post<DamageTypeRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseDamageType)
        }
    }
}

private fun HTML.showDamageTypeEditor(
    call: ApplicationCall,
    state: State,
    type: DamageType,
) {
    val backLink = href(call, type.id)
    val previewLink = call.application.href(DamageTypeRoutes.Preview(type.id))
    val updateLink = call.application.href(DamageTypeRoutes.Update(type.id))

    simpleHtmlEditor(type, true) {
        mainFrame {
            formWithPreview(previewLink, updateLink, backLink) {
                editDamageType(state, type)
            }
        }
    }
}

