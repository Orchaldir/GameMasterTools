package at.orchaldir.gm.app.routes.organization

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.formWithPreview
import at.orchaldir.gm.app.html.href
import at.orchaldir.gm.app.html.organization.editOrganization
import at.orchaldir.gm.app.html.organization.parseOrganization
import at.orchaldir.gm.app.html.organization.showOrganization
import at.orchaldir.gm.app.html.showCreatorCount
import at.orchaldir.gm.app.html.simpleHtmlEditor
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.organization.ORGANIZATION_TYPE
import at.orchaldir.gm.core.model.organization.Organization
import at.orchaldir.gm.core.model.organization.OrganizationId
import at.orchaldir.gm.core.model.util.SortOrganization
import at.orchaldir.gm.core.selector.util.sortOrganizations
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

@Resource("/$ORGANIZATION_TYPE")
class OrganizationRoutes : Routes<OrganizationId, SortOrganization> {
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
    override fun all(call: ApplicationCall, sort: SortOrganization) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: OrganizationId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: OrganizationId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
}

fun Application.configureOrganizationRouting() {
    routing {
        get<OrganizationRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                OrganizationRoutes(),
                state.sortOrganizations(all.sort),
                listOf(
                    createNameColumn(call, state),
                    createStartDateColumn(call, state),
                    createAgeColumn(state),
                    createReferenceColumn(call, state, "Founder", Organization::founder),
                    createSkipZeroColumnFromCollection("Ranks", Organization::memberRanks),
                    createSkipZeroColumn("Members", Organization::countAllMembers),
                    createBeliefColumn(call, state),
                ),
            ) {
                showCreatorCount(call, state, it, "Founders")
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
