package at.orchaldir.gm.app.routes.item

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.item.editUniform
import at.orchaldir.gm.app.html.item.parseUniform
import at.orchaldir.gm.app.html.item.showUniform
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.item.UNIFORM_TYPE
import at.orchaldir.gm.core.model.item.Uniform
import at.orchaldir.gm.core.model.item.UniformId
import at.orchaldir.gm.core.model.util.SortUniform
import at.orchaldir.gm.core.selector.item.getEquipment
import at.orchaldir.gm.core.selector.util.sortUniforms
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMeters
import at.orchaldir.gm.visualization.character.appearance.visualizeCharacter
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HTML
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}
private val appearance = HumanoidBody(Body(), Head(), fromMeters(2))

@Resource("/$UNIFORM_TYPE")
class UniformRoutes : Routes<UniformId, SortUniform> {
    @Resource("all")
    class All(
        val sort: SortUniform = SortUniform.Name,
        val parent: UniformRoutes = UniformRoutes(),
    )

    @Resource("gallery")
    class Gallery(val parent: UniformRoutes = UniformRoutes())

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
    override fun gallery(call: ApplicationCall) = call.application.href(Gallery())
    override fun delete(call: ApplicationCall, id: UniformId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: UniformId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
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
                    createSkipZeroColumnFromCollection("Parts") { it.equipmentMap.getAllEquipment() }
                ),
            )
        }
        get<UniformRoutes.Gallery> {
            logger.info { "Show gallery" }

            call.respondHtml(HttpStatusCode.OK) {
                showGallery(call, STORE.getState())
            }
        }
        get<UniformRoutes.Details> { details ->
            handleShowElement<UniformId, Uniform, SortUniform>(details.id, UniformRoutes()) { call, state, uniform ->
                val equipped = state.getEquipment(uniform.equipmentMap)
                val svg = visualizeCharacter(state, CHARACTER_CONFIG, appearance, equipped)
                svg(svg, 20)
                showUniform(call, state, uniform)
            }
        }
        get<UniformRoutes.New> {
            handleCreateElement(STORE.getState().getUniformStorage()) { id ->
                UniformRoutes.Edit(id)
            }
        }
        get<UniformRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, UniformRoutes.All())
        }
        get<UniformRoutes.Edit> { edit ->
            logger.info { "Get editor for uniform ${edit.id.value}" }

            val state = STORE.getState()
            val uniform = state.getUniformStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showUniformEditor(call, state, uniform)
            }
        }
        post<UniformRoutes.Preview> { preview ->
            logger.info { "Get preview for uniform ${preview.id.value}" }

            val formParameters = call.receiveParameters()
            val state = STORE.getState()
            val uniform = parseUniform(state, formParameters, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showUniformEditor(call, state, uniform)
            }
        }
        post<UniformRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseUniform)
        }
    }
}

private fun HTML.showGallery(
    call: ApplicationCall,
    state: State,
) {
    val uniforms = state.sortUniforms()
    val backLink = call.application.href(UniformRoutes.All())

    simpleHtml("Uniforms") {
        showGallery(call, state, uniforms) { uniform ->
            val equipped = state.getEquipment(uniform.equipmentMap)
            visualizeCharacter(state, CHARACTER_CONFIG, appearance, equipped)
        }

        back(backLink)
    }
}

private fun HTML.showUniformEditor(
    call: ApplicationCall,
    state: State,
    uniform: Uniform,
) {
    val backLink = href(call, uniform.id)
    val previewLink = call.application.href(UniformRoutes.Preview(uniform.id))
    val updateLink = call.application.href(UniformRoutes.Update(uniform.id))
    val equipped = state.getEquipment(uniform.equipmentMap)
    val svg = visualizeCharacter(state, CHARACTER_CONFIG, appearance, equipped)

    simpleHtmlEditor(uniform, true) {
        split({
            formWithPreview(previewLink, updateLink, backLink) {
                editUniform(state, uniform)
            }
        }, {
            svg(svg, 50)
        })
    }
}
