package at.orchaldir.gm.app.routes.world

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.economy.material.selectMaterialCost
import at.orchaldir.gm.app.html.model.economy.material.showMaterialCost
import at.orchaldir.gm.app.parse.world.parseStreetTemplate
import at.orchaldir.gm.core.action.CreateStreetTemplate
import at.orchaldir.gm.core.action.DeleteStreetTemplate
import at.orchaldir.gm.core.action.UpdateStreetTemplate
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Solid
import at.orchaldir.gm.core.model.world.street.STREET_TEMPLATE_TYPE
import at.orchaldir.gm.core.model.world.street.StreetTemplate
import at.orchaldir.gm.core.model.world.street.StreetTemplateId
import at.orchaldir.gm.core.selector.world.canDelete
import at.orchaldir.gm.core.selector.world.getTowns
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.renderer.model.NoBorder
import at.orchaldir.gm.utils.renderer.model.toRender
import at.orchaldir.gm.utils.renderer.svg.Svg
import at.orchaldir.gm.utils.renderer.svg.SvgBuilder
import at.orchaldir.gm.visualization.town.TILE_SIZE
import at.orchaldir.gm.visualization.town.renderStreet
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.HTML
import kotlinx.html.table
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.tr
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$STREET_TEMPLATE_TYPE")
class StreetTemplateRoutes {
    @Resource("details")
    class Details(val id: StreetTemplateId, val parent: StreetTemplateRoutes = StreetTemplateRoutes())

    @Resource("new")
    class New(val parent: StreetTemplateRoutes = StreetTemplateRoutes())

    @Resource("delete")
    class Delete(val id: StreetTemplateId, val parent: StreetTemplateRoutes = StreetTemplateRoutes())

    @Resource("edit")
    class Edit(val id: StreetTemplateId, val parent: StreetTemplateRoutes = StreetTemplateRoutes())

    @Resource("preview")
    class Preview(val id: StreetTemplateId, val parent: StreetTemplateRoutes = StreetTemplateRoutes())

    @Resource("update")
    class Update(val id: StreetTemplateId, val parent: StreetTemplateRoutes = StreetTemplateRoutes())
}

fun Application.configureStreetTemplateRouting() {
    routing {
        get<StreetTemplateRoutes> {
            logger.info { "Get all street templates" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllStreetTemplates(call, STORE.getState())
            }
        }
        get<StreetTemplateRoutes.Details> { details ->
            logger.info { "Get details of street template ${details.id.value}" }

            val state = STORE.getState()
            val street = state.getStreetTemplateStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showStreetTemplateDetails(call, state, street)
            }
        }
        get<StreetTemplateRoutes.New> {
            logger.info { "Add new street template" }

            STORE.dispatch(CreateStreetTemplate)

            call.respondRedirect(
                call.application.href(
                    StreetTemplateRoutes.Edit(
                        STORE.getState().getStreetTemplateStorage().lastId
                    )
                )
            )

            STORE.getState().save()
        }
        get<StreetTemplateRoutes.Delete> { delete ->
            logger.info { "Delete street template ${delete.id.value}" }

            STORE.dispatch(DeleteStreetTemplate(delete.id))

            call.respondRedirect(call.application.href(StreetTemplateRoutes()))

            STORE.getState().save()
        }
        get<StreetTemplateRoutes.Edit> { edit ->
            logger.info { "Get editor for street template ${edit.id.value}" }

            val state = STORE.getState()
            val street = state.getStreetTemplateStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showStreetTemplateEditor(call, state, street)
            }
        }
        post<StreetTemplateRoutes.Preview> { preview ->
            logger.info { "Preview street template ${preview.id.value}" }

            val type = parseStreetTemplate(preview.id, call.receiveParameters())

            call.respondHtml(HttpStatusCode.OK) {
                showStreetTemplateEditor(call, STORE.getState(), type)
            }
        }
        post<StreetTemplateRoutes.Update> { update ->
            logger.info { "Update street template ${update.id.value}" }

            val type = parseStreetTemplate(update.id, call.receiveParameters())

            STORE.dispatch(UpdateStreetTemplate(type))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllStreetTemplates(
    call: ApplicationCall,
    state: State,
) {
    val templates = state.getStreetTemplateStorage().getAll().sortedBy { it.name.text }
    val createLink = call.application.href(StreetTemplateRoutes.New())

    simpleHtml("Street Templates") {
        field("Count", templates.size)

        table {
            tr {
                th { +"Name" }
                th { +"Color" }
                th { +"Materials" }
            }
            templates.forEach { template ->
                tr {
                    tdLink(call, state, template)
                    td { showColor(template.color) }
                    tdInlineIds(call, state, template.materialCost.materials())
                }
            }
        }

        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showStreetTemplateDetails(
    call: ApplicationCall,
    state: State,
    type: StreetTemplate,
) {
    val backLink = call.application.href(StreetTemplateRoutes())
    val deleteLink = call.application.href(StreetTemplateRoutes.Delete(type.id))
    val editLink = call.application.href(StreetTemplateRoutes.Edit(type.id))

    simpleHtmlDetails(type) {
        split({
            fieldName(type.name)
            fieldColor(type.color)
            showMaterialCost(call, state, type.materialCost)
            fieldList(call, state, state.getTowns(type.id))

            action(editLink, "Edit")
            if (state.canDelete(type.id)) {
                action(deleteLink, "Delete")
            }
            back(backLink)
        }, {
            svg(visualizeStreetTemplate(type), 90)
        })
    }
}

private fun HTML.showStreetTemplateEditor(
    call: ApplicationCall,
    state: State,
    type: StreetTemplate,
) {
    val backLink = href(call, type.id)
    val previewLink = call.application.href(StreetTemplateRoutes.Preview(type.id))
    val updateLink = call.application.href(StreetTemplateRoutes.Update(type.id))

    simpleHtmlEditor(type) {
        split({
            formWithPreview(previewLink, updateLink, backLink) {
                selectName(type.name)
                selectColor(type.color)
                selectMaterialCost(call, state, type.materialCost)
            }
        }, {
            svg(visualizeStreetTemplate(type), 90)
        })
    }
}

private fun visualizeStreetTemplate(
    streetTemplate: StreetTemplate,
): Svg {
    val size = Size2d.square(TILE_SIZE)
    val builder = SvgBuilder(size)
    val aabb = AABB(size)
    val option = NoBorder(Solid(Color.Green).toRender())

    builder.getLayer().renderRectangle(aabb, option)
    renderStreet(builder.getLayer(), aabb, streetTemplate.color)

    return builder.finish()
}