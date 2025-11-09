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
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

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
    override fun preview(call: ApplicationCall, id: StreetTemplateId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: StreetTemplateId) = call.application.href(Update(id))
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
                HtmlBlockTag::showStreetTemplate,
                HtmlBlockTag::showStreetTemplateEditorRight,
            )
        }
        get<StreetTemplateRoutes.New> {
            handleCreateElement(StreetTemplateRoutes(), STORE.getState().getStreetTemplateStorage())
        }
        get<StreetTemplateRoutes.Delete> { delete ->
            handleDeleteElement(StreetTemplateRoutes(), delete.id)
        }
        get<StreetTemplateRoutes.Edit> { edit ->
            handleEditElementSplit(
                edit.id,
                StreetTemplateRoutes(),
                HtmlBlockTag::editStreetTemplate,
                HtmlBlockTag::showStreetTemplateEditorRight,
            )
        }
        post<StreetTemplateRoutes.Preview> { preview ->
            handlePreviewElementSplit(
                preview.id,
                StreetTemplateRoutes(),
                ::parseStreetTemplate,
                HtmlBlockTag::editStreetTemplate,
                HtmlBlockTag::showStreetTemplateEditorRight,
            )
        }
        post<StreetTemplateRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseStreetTemplate)
        }
    }
}

private fun HtmlBlockTag.showStreetTemplateEditorRight(
    call: ApplicationCall,
    state: State,
    template: StreetTemplate,
) {
    svg(visualizeStreetTemplate(template), 90)
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