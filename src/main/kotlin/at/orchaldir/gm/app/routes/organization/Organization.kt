package at.orchaldir.gm.app.routes.organization

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.organization.editOrganization
import at.orchaldir.gm.app.html.organization.parseOrganization
import at.orchaldir.gm.app.html.organization.showOrganization
import at.orchaldir.gm.app.html.util.showBeliefStatus
import at.orchaldir.gm.app.html.util.showOptionalDate
import at.orchaldir.gm.app.html.util.showReference
import at.orchaldir.gm.app.routes.Routes
import at.orchaldir.gm.app.routes.handleCreateElement
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.app.routes.handleShowElement
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.organization.ORGANIZATION_TYPE
import at.orchaldir.gm.core.model.organization.Organization
import at.orchaldir.gm.core.model.organization.OrganizationId
import at.orchaldir.gm.core.model.util.SortOrganization
import at.orchaldir.gm.core.selector.time.getAgeInYears
import at.orchaldir.gm.core.selector.util.sortOrganizations
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

@Resource("/$ORGANIZATION_TYPE")
class OrganizationRoutes: Routes<OrganizationId> {
    @Resource("all")
    class All(
        val sort: SortOrganization = SortOrganization.Name,
        val parent: OrganizationRoutes = OrganizationRoutes(),
    )

    @Resource("details")
    class Details(val id: OrganizationId, val parent: OrganizationRoutes = OrganizationRoutes())

    @Resource("new")
    class New(val parent: OrganizationRoutes = OrganizationRoutes())

    @Resource("delete")
    class Delete(val id: OrganizationId, val parent: OrganizationRoutes = OrganizationRoutes())

    @Resource("edit")
    class Edit(val id: OrganizationId, val parent: OrganizationRoutes = OrganizationRoutes())

    @Resource("preview")
    class Preview(val id: OrganizationId, val parent: OrganizationRoutes = OrganizationRoutes())

    @Resource("update")
    class Update(val id: OrganizationId, val parent: OrganizationRoutes = OrganizationRoutes())


    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun delete(call: ApplicationCall, id: OrganizationId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: OrganizationId) = call.application.href(Edit(id))
}

fun Application.configureOrganizationRouting() {
    routing {
        get<OrganizationRoutes.All> { all ->
            logger.info { "Get all organizations" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllOrganizations(call, STORE.getState(), all.sort)
            }
        }
        get<OrganizationRoutes.Details> { details ->
            handleShowElement(details.id, OrganizationRoutes(), HtmlBlockTag::showOrganization)
        }
        get<OrganizationRoutes.New> {
            handleCreateElement(STORE.getState().getOrganizationStorage()) { id ->
                OrganizationRoutes.Edit(id)
            }
        }
        get<OrganizationRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, OrganizationRoutes.All())
        }
        get<OrganizationRoutes.Edit> { edit ->
            logger.info { "Get editor for organization ${edit.id.value}" }

            val state = STORE.getState()
            val organization = state.getOrganizationStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showOrganizationEditor(call, state, organization)
            }
        }
        post<OrganizationRoutes.Preview> { preview ->
            logger.info { "Get preview for organization ${preview.id.value}" }

            val formParameters = call.receiveParameters()
            val state = STORE.getState()
            val organization = parseOrganization(state, formParameters, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showOrganizationEditor(call, state, organization)
            }
        }
        post<OrganizationRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseOrganization)
        }
    }
}

private fun HTML.showAllOrganizations(
    call: ApplicationCall,
    state: State,
    sort: SortOrganization,
) {
    val organizations = state.sortOrganizations(sort)
    val createLink = call.application.href(OrganizationRoutes.New())

    simpleHtml("Organizations") {
        field("Count", organizations.size)
        showSortTableLinks(call, SortOrganization.entries, OrganizationRoutes(), OrganizationRoutes::All)

        table {
            tr {
                th { +"Name" }
                th { +"Date" }
                th { +"Age" }
                th { +"Founder" }
                th { +"Ranks" }
                th { +"Members" }
                th { +"Belief" }
            }
            organizations.forEach { organization ->
                tr {
                    tdLink(call, state, organization)
                    td { showOptionalDate(call, state, organization.date) }
                    tdSkipZero(state.getAgeInYears(organization.date))
                    td { showReference(call, state, organization.founder, false) }
                    tdSkipZero(organization.memberRanks)
                    tdSkipZero(organization.countAllMembers())
                    td { showBeliefStatus(call, state, organization.beliefStatus.current, false) }
                }
            }
        }

        showCreatorCount(call, state, organizations, "Founders")

        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showOrganizationEditor(
    call: ApplicationCall,
    state: State,
    organization: Organization,
) {
    val backLink = href(call, organization.id)
    val previewLink = call.application.href(OrganizationRoutes.Preview(organization.id))
    val updateLink = call.application.href(OrganizationRoutes.Update(organization.id))

    simpleHtmlEditor(organization) {
        formWithPreview(previewLink, updateLink, backLink) {
            editOrganization(state, organization)
        }
    }
}
