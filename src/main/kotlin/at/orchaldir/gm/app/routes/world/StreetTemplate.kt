package at.orchaldir.gm.app.routes.world

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.Column.Companion.tdColumn
import at.orchaldir.gm.app.html.world.editStreetTemplate
import at.orchaldir.gm.app.html.world.parseStreetTemplate
import at.orchaldir.gm.app.html.world.showStreetTemplate
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.SortStreetTemplate
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.util.render.Solid
import at.orchaldir.gm.core.model.world.street.STREET_TEMPLATE_TYPE
import at.orchaldir.gm.core.model.world.street.StreetTemplate
import at.orchaldir.gm.core.model.world.street.StreetTemplateId
import at.orchaldir.gm.core.selector.util.sortStreetTemplates
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
import io.ktor.server.routing.*
import kotlinx.html.HTML
import kotlinx.html.HtmlBlockTag
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$STREET_TEMPLATE_TYPE")
class StreetTemplateRoutes : Routes<StreetTemplateId, SortStreetTemplate> {
    @Resource("all")
    class All(
        val sort: SortStreetTemplate = SortStreetTemplate.Name,
        val parent: StreetTemplateRoutes = StreetTemplateRoutes(),
    )

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

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortStreetTemplate) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: StreetTemplateId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: StreetTemplateId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
}

fun Application.configureStreetTemplateRouting() {
    routing {
        get<StreetTemplateRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                StreetTemplateRoutes(),
                state.sortStreetTemplates(all.sort),
                listOf(
                    createNameColumn(call, state),
                    tdColumn("Color") { showColor(it.color) },
                    Column("Materials") { tdInlineIds(call, state, it.materialCost.materials()) },
                ),
            )
        }
        get<StreetTemplateRoutes.Details> { details ->
            handleShowElementSplit(
                details.id,
                StreetTemplateRoutes(),
                HtmlBlockTag::showStreetTemplate
            ) { _, _, template ->
                svg(visualizeStreetTemplate(template), 90)
            }
        }
        get<StreetTemplateRoutes.New> {
            handleCreateElement(STORE.getState().getStreetTemplateStorage()) { id ->
                StreetTemplateRoutes.Edit(id)
            }
        }
        get<StreetTemplateRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, StreetTemplateRoutes())
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

            val state = STORE.getState()
            val type = parseStreetTemplate(state, call.receiveParameters(), preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showStreetTemplateEditor(call, state, type)
            }
        }
        post<StreetTemplateRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseStreetTemplate)
        }
    }
}


private fun HTML.showStreetTemplateEditor(
    call: ApplicationCall,
    state: State,
    template: StreetTemplate,
) {
    val backLink = href(call, template.id)
    val previewLink = call.application.href(StreetTemplateRoutes.Preview(template.id))
    val updateLink = call.application.href(StreetTemplateRoutes.Update(template.id))

    simpleHtmlEditor(template) {
        split({
            formWithPreview(previewLink, updateLink, backLink) {
                editStreetTemplate(call, state, template)
            }
        }, {
            svg(visualizeStreetTemplate(template), 90)
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