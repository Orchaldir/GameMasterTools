package at.orchaldir.gm.app.routes.item

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.Column.Companion.tdColumn
import at.orchaldir.gm.app.html.character.showEquipped
import at.orchaldir.gm.app.html.createNameColumn
import at.orchaldir.gm.app.html.item.editUniform
import at.orchaldir.gm.app.html.item.parseUniform
import at.orchaldir.gm.app.html.item.showUniform
import at.orchaldir.gm.app.html.svg
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.item.UNIFORM_TYPE
import at.orchaldir.gm.core.model.item.Uniform
import at.orchaldir.gm.core.model.item.UniformId
import at.orchaldir.gm.core.model.rpg.statblock.UndefinedStatblockLookup
import at.orchaldir.gm.core.model.util.SortUniform
import at.orchaldir.gm.core.selector.item.equipment.getEquipmentElementMap
import at.orchaldir.gm.core.selector.util.sortUniforms
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMeters
import at.orchaldir.gm.visualization.character.appearance.visualizeCharacter
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

private val appearance = HumanoidBody(Body(), Head(), fromMeters(2))

@Resource("/$UNIFORM_TYPE")
class UniformRoutes : Routes<UniformId, SortUniform> {
    @Resource("all")
    class All(
        val sort: SortUniform = SortUniform.Name,
        val parent: UniformRoutes = UniformRoutes(),
    )

    @Resource("gallery")
    class Gallery(
        val sort: SortUniform = SortUniform.Name,
        val parent: UniformRoutes = UniformRoutes(),
    )

    @Resource("details")
    class Details(val id: UniformId, val parent: UniformRoutes = UniformRoutes())

    @Resource("new")
    class New(val parent: UniformRoutes = UniformRoutes())

    @Resource("delete")
    class Delete(val id: UniformId, val parent: UniformRoutes = UniformRoutes())

    @Resource("edit")
    class Edit(val id: UniformId, val parent: UniformRoutes = UniformRoutes())

    @Resource("preview")
    class Preview(val id: UniformId, val parent: UniformRoutes = UniformRoutes())

    @Resource("update")
    class Update(val id: UniformId, val parent: UniformRoutes = UniformRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortUniform) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: UniformId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: UniformId) = call.application.href(Edit(id))
    override fun gallery(call: ApplicationCall) = call.application.href(Gallery())
    override fun gallery(call: ApplicationCall, sort: SortUniform) = call.application.href(Gallery(sort))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: UniformId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: UniformId) = call.application.href(Update(id))
}

fun Application.configureUniformRouting() {
    routing {
        get<UniformRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                UniformRoutes(),
                state.sortUniforms(all.sort),
                listOf(
                    createNameColumn(call, state),
                    tdColumn("Equipped") { showEquipped(call, state, it.equipped, UndefinedStatblockLookup) },
                ),
            )
        }
        get<UniformRoutes.Gallery> { gallery ->
            val state = STORE.getState()
            val routes = UniformRoutes()

            handleShowGallery(
                state,
                routes,
                state.sortUniforms(gallery.sort),
                gallery.sort,
            ) { uniform ->
                val equipped = state.getEquipmentElementMap(uniform)
                visualizeCharacter(state, CHARACTER_CONFIG, appearance, equipped)
            }
        }
        get<UniformRoutes.Details> { details ->
            handleShowElementSplit(
                details.id,
                UniformRoutes(),
                HtmlBlockTag::showUniform,
                HtmlBlockTag::showUniformRight,
            )
        }
        get<UniformRoutes.New> {
            handleCreateElement(UniformRoutes(), STORE.getState().getUniformStorage())
        }
        get<UniformRoutes.Delete> { delete ->
            handleDeleteElement(UniformRoutes(), delete.id)
        }
        get<UniformRoutes.Edit> { edit ->
            handleEditElementSplit(
                edit.id,
                UniformRoutes(),
                HtmlBlockTag::editUniform,
                HtmlBlockTag::showUniformRight,
            )
        }
        post<UniformRoutes.Preview> { preview ->
            handlePreviewElementSplit(
                preview.id,
                UniformRoutes(),
                ::parseUniform,
                HtmlBlockTag::editUniform,
                HtmlBlockTag::showUniformRight,
            )
        }
        post<UniformRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseUniform)
        }
    }
}

private fun HtmlBlockTag.showUniformRight(
    call: ApplicationCall,
    state: State,
    uniform: Uniform,
) {
    val equipped = state.getEquipmentElementMap(uniform)
    val svg = visualizeCharacter(state, CHARACTER_CONFIG, appearance, equipped)

    svg(svg, 50)
}
