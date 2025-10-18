package at.orchaldir.gm.app.routes.religion

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.religion.editDomain
import at.orchaldir.gm.app.html.religion.parseDomain
import at.orchaldir.gm.app.html.religion.showDomain
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.religion.DOMAIN_TYPE
import at.orchaldir.gm.core.model.religion.Domain
import at.orchaldir.gm.core.model.religion.DomainId
import at.orchaldir.gm.core.model.util.SortDomain
import at.orchaldir.gm.core.selector.religion.getGodsWith
import at.orchaldir.gm.core.selector.util.sortDomains
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

@Resource("/$DOMAIN_TYPE")
class DomainRoutes : Routes<DomainId, SortDomain> {
    @Resource("all")
    class All(
        val sort: SortDomain = SortDomain.Name,
        val parent: DomainRoutes = DomainRoutes(),
    )

    @Resource("details")
    class Details(val id: DomainId, val parent: DomainRoutes = DomainRoutes())

    @Resource("new")
    class New(val parent: DomainRoutes = DomainRoutes())

    @Resource("delete")
    class Delete(val id: DomainId, val parent: DomainRoutes = DomainRoutes())

    @Resource("edit")
    class Edit(val id: DomainId, val parent: DomainRoutes = DomainRoutes())

    @Resource("preview")
    class Preview(val id: DomainId, val parent: DomainRoutes = DomainRoutes())

    @Resource("update")
    class Update(val id: DomainId, val parent: DomainRoutes = DomainRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortDomain) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: DomainId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: DomainId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
}

fun Application.configureDomainRouting() {
    routing {
        get<DomainRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                DomainRoutes(),
                state.sortDomains(all.sort),
                listOf(
                    createNameColumn(call, state),
                    Column("Spells") { tdSkipZero(it.spells.getSize()) },
                    Column("Jobs") { tdSkipZero(it.jobs) },
                    Column("Gods") { tdSkipZero(state.getGodsWith(it.id())) },
                ),
            )
        }
        get<DomainRoutes.Details> { details ->
            handleShowElement(details.id, DomainRoutes(), HtmlBlockTag::showDomain)
        }
        get<DomainRoutes.New> {
            handleCreateElement(STORE.getState().getDomainStorage()) { id ->
                DomainRoutes.Edit(id)
            }
        }
        get<DomainRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, DomainRoutes.All())
        }
        get<DomainRoutes.Edit> { edit ->
            logger.info { "Get editor for domain ${edit.id.value}" }

            val state = STORE.getState()
            val domain = state.getDomainStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showDomainEditor(call, state, domain)
            }
        }
        post<DomainRoutes.Preview> { preview ->
            logger.info { "Get preview for domain ${preview.id.value}" }

            val formParameters = call.receiveParameters()
            val state = STORE.getState()
            val domain = parseDomain(state, formParameters, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showDomainEditor(call, state, domain)
            }
        }
        post<DomainRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseDomain)
        }
    }
}

private fun HTML.showDomainEditor(
    call: ApplicationCall,
    state: State,
    domain: Domain,
) {
    val backLink = href(call, domain.id)
    val previewLink = call.application.href(DomainRoutes.Preview(domain.id))
    val updateLink = call.application.href(DomainRoutes.Update(domain.id))

    simpleHtmlEditor(domain) {
        formWithPreview(previewLink, updateLink, backLink) {
            editDomain(call, state, domain)
        }
    }
}

