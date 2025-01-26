package at.orchaldir.gm.app.routes.organization

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.*
import at.orchaldir.gm.app.html.model.organization.parseOrganization
import at.orchaldir.gm.app.html.model.organization.editOrganization
import at.orchaldir.gm.app.html.model.organization.showOrganization
import at.orchaldir.gm.core.action.CreateOrganization
import at.orchaldir.gm.core.action.DeleteOrganization
import at.orchaldir.gm.core.action.UpdateOrganization
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.organization.ORGANIZATION_TYPE
import at.orchaldir.gm.core.model.organization.Organization
import at.orchaldir.gm.core.model.organization.OrganizationId
import at.orchaldir.gm.core.model.util.SortOrganization
import at.orchaldir.gm.core.selector.organization.canDeleteOrganization
import at.orchaldir.gm.core.selector.util.sortOrganizations
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

@Resource("/$ORGANIZATION_TYPE")
class OrganizationRoutes {
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
            logger.info { "Get details of organization ${details.id.value}" }

            val state = STORE.getState()
            val organization = state.getOrganizationStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showOrganizationDetails(call, state, organization)
            }
        }
        get<OrganizationRoutes.New> {
            logger.info { "Add new organization" }

            STORE.dispatch(CreateOrganization)

            call.respondRedirect(
                call.application.href(
                    OrganizationRoutes.Edit(
                        STORE.getState().getOrganizationStorage().lastId
                    )
                )
            )

            STORE.getState().save()
        }
        get<OrganizationRoutes.Delete> { delete ->
            logger.info { "Delete organization ${delete.id.value}" }

            STORE.dispatch(DeleteOrganization(delete.id))

            call.respondRedirect(call.application.href(OrganizationRoutes()))

            STORE.getState().save()
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
            val organization = parseOrganization(formParameters, STORE.getState(), preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showOrganizationEditor(call, STORE.getState(), organization)
            }
        }
        post<OrganizationRoutes.Update> { update ->
            logger.info { "Update organization ${update.id.value}" }

            val formParameters = call.receiveParameters()
            val organization = parseOrganization(formParameters, STORE.getState(), update.id)

            STORE.dispatch(UpdateOrganization(organization))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
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
    val sortNameLink = call.application.href(OrganizationRoutes.All(SortOrganization.Name))
    val sortAgeLink = call.application.href(OrganizationRoutes.All(SortOrganization.Age))

    simpleHtml("Organizations") {
        field("Count", organizations.size)
        field("Sort") {
            link(sortNameLink, "Name")
            +" "
            link(sortAgeLink, "Age")
        }

        table {
            tr {
                th { +"Name" }
                th { +"Date" }
                th { +"Founder" }
            }
            organizations.forEach { organization ->
                tr {
                    td { link(call, state, organization) }
                    td { showOptionalDate(call, state, organization.date) }
                    td { showCreator(call, state, organization.founder, false) }
                }
            }
        }

        showCreatorCount(call, state, organizations, "Founders")

        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showOrganizationDetails(
    call: ApplicationCall,
    state: State,
    organization: Organization,
) {
    val backLink = call.application.href(OrganizationRoutes.All())
    val deleteLink = call.application.href(OrganizationRoutes.Delete(organization.id))
    val editLink = call.application.href(OrganizationRoutes.Edit(organization.id))

    simpleHtml("Organization: ${organization.name(state)}") {
        showOrganization(call, state, organization)

        action(editLink, "Edit")

        if (state.canDeleteOrganization(organization.id)) {
            action(deleteLink, "Delete")
        }

        back(backLink)
    }
}

private fun HTML.showOrganizationEditor(
    call: ApplicationCall,
    state: State,
    organization: Organization,
) {
    val name = organization.name(state)
    val backLink = href(call, organization.id)
    val previewLink = call.application.href(OrganizationRoutes.Preview(organization.id))
    val updateLink = call.application.href(OrganizationRoutes.Update(organization.id))

    simpleHtml("Edit Organization: $name") {
        form {
            id = "editor"
            action = previewLink
            method = FormMethod.post

            editOrganization(state, organization)

            button("Update", updateLink)
        }
        back(backLink)
    }
}
