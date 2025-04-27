package at.orchaldir.gm.app.routes.religion

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.religion.editDomain
import at.orchaldir.gm.app.html.model.religion.parseDomain
import at.orchaldir.gm.app.html.model.religion.showDomain
import at.orchaldir.gm.core.action.CreateDomain
import at.orchaldir.gm.core.action.DeleteDomain
import at.orchaldir.gm.core.action.UpdateDomain
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.religion.DOMAIN_TYPE
import at.orchaldir.gm.core.model.religion.Domain
import at.orchaldir.gm.core.model.religion.DomainId
import at.orchaldir.gm.core.model.util.SortDomain
import at.orchaldir.gm.core.selector.religion.canDeleteDomain
import at.orchaldir.gm.core.selector.religion.getGodsWith
import at.orchaldir.gm.core.selector.util.sortDomains
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

@Resource("/$DOMAIN_TYPE")
class DomainRoutes {
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
}

fun Application.configureDomainRouting() {
    routing {
        get<DomainRoutes.All> { all ->
            logger.info { "Get all domains" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllDomains(call, STORE.getState(), all.sort)
            }
        }
        get<DomainRoutes.Details> { details ->
            logger.info { "Get details of domain ${details.id.value}" }

            val state = STORE.getState()
            val domain = state.getDomainStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showDomainDetails(call, state, domain)
            }
        }
        get<DomainRoutes.New> {
            logger.info { "Add new domain" }

            STORE.dispatch(CreateDomain)

            call.respondRedirect(call.application.href(DomainRoutes.Edit(STORE.getState().getDomainStorage().lastId)))

            STORE.getState().save()
        }
        get<DomainRoutes.Delete> { delete ->
            logger.info { "Delete domain ${delete.id.value}" }

            STORE.dispatch(DeleteDomain(delete.id))

            call.respondRedirect(call.application.href(DomainRoutes()))

            STORE.getState().save()
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
            val domain = parseDomain(formParameters, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showDomainEditor(call, state, domain)
            }
        }
        post<DomainRoutes.Update> { update ->
            logger.info { "Update domain ${update.id.value}" }

            val formParameters = call.receiveParameters()
            val domain = parseDomain(formParameters, update.id)

            STORE.dispatch(UpdateDomain(domain))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllDomains(
    call: ApplicationCall,
    state: State,
    sort: SortDomain,
) {
    val domains = state.sortDomains(sort)
    val createLink = call.application.href(DomainRoutes.New())
    val sortNameLink = call.application.href(DomainRoutes.All(SortDomain.Name))

    simpleHtml("Domains") {
        field("Count", domains.size)
        field("Sort") {
            link(sortNameLink, "Name")
        }

        table {
            tr {
                th { +"Name" }
                th { +"Spells" }
                th { +"Jobs" }
                th { +"Gods" }
            }
            domains.forEach { domain ->
                tr {
                    td { link(call, state, domain) }
                    tdSkipZero(domain.spells.getSize())
                    tdSkipZero(domain.jobs.size)
                    tdSkipZero(state.getGodsWith(domain.id).size)
                }
            }
        }

        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showDomainDetails(
    call: ApplicationCall,
    state: State,
    domain: Domain,
) {
    val backLink = call.application.href(DomainRoutes.All())
    val deleteLink = call.application.href(DomainRoutes.Delete(domain.id))
    val editLink = call.application.href(DomainRoutes.Edit(domain.id))

    simpleHtmlDetails(domain) {
        showDomain(call, state, domain)

        fieldList(call, state, state.getGodsWith(domain.id))

        action(editLink, "Edit")

        if (state.canDeleteDomain(domain.id)) {
            action(deleteLink, "Delete")
        }

        back(backLink)
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

