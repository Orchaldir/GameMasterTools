package at.orchaldir.gm.app.routes.item

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.item.editUniform
import at.orchaldir.gm.app.html.item.parseUniform
import at.orchaldir.gm.app.html.item.showUniform
import at.orchaldir.gm.core.action.CreateUniform
import at.orchaldir.gm.core.action.DeleteUniform
import at.orchaldir.gm.core.action.UpdateUniform
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.item.UNIFORM_TYPE
import at.orchaldir.gm.core.model.item.Uniform
import at.orchaldir.gm.core.model.item.UniformId
import at.orchaldir.gm.core.model.util.SortUniform
import at.orchaldir.gm.core.selector.item.canDeleteUniform
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
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.HTML
import kotlinx.html.table
import kotlinx.html.th
import kotlinx.html.tr
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}
private val appearance = HumanoidBody(Body(), Head(), fromMeters(2))

@Resource("/$UNIFORM_TYPE")
class UniformRoutes {
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
}

fun Application.configureUniformRouting() {
    routing {
        get<UniformRoutes.All> { all ->
            logger.info { "Get all uniforms" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllUniforms(call, STORE.getState(), all.sort)
            }
        }
        get<UniformRoutes.Gallery> {
            logger.info { "Show gallery" }

            call.respondHtml(HttpStatusCode.OK) {
                showGallery(call, STORE.getState())
            }
        }
        get<UniformRoutes.Details> { details ->
            logger.info { "Get details of uniform ${details.id.value}" }

            val state = STORE.getState()
            val uniform = state.getUniformStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showUniformDetails(call, state, uniform)
            }
        }
        get<UniformRoutes.New> {
            logger.info { "Add new uniform" }

            STORE.dispatch(CreateUniform)

            call.respondRedirect(call.application.href(UniformRoutes.Edit(STORE.getState().getUniformStorage().lastId)))

            STORE.getState().save()
        }
        get<UniformRoutes.Delete> { delete ->
            logger.info { "Delete uniform ${delete.id.value}" }

            STORE.dispatch(DeleteUniform(delete.id))

            call.respondRedirect(call.application.href(UniformRoutes.All()))

            STORE.getState().save()
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
            val uniform = parseUniform(formParameters, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showUniformEditor(call, STORE.getState(), uniform)
            }
        }
        post<UniformRoutes.Update> { update ->
            logger.info { "Update uniform ${update.id.value}" }

            val formParameters = call.receiveParameters()
            val uniform = parseUniform(formParameters, update.id)

            STORE.dispatch(UpdateUniform(uniform))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllUniforms(
    call: ApplicationCall,
    state: State,
    sort: SortUniform,
) {
    val uniforms = state.sortUniforms(sort)
    val createLink = call.application.href(UniformRoutes.New())
    val galleryLink = call.application.href(UniformRoutes.Gallery())

    simpleHtml("Uniforms") {
        action(galleryLink, "Gallery")
        field("Count", uniforms.size)
        showSortTableLinks(call, SortUniform.entries, UniformRoutes(), UniformRoutes::All)
        table {
            tr {
                th { +"Name" }
                th { +"Parts" }
            }
            uniforms.forEach { uniform ->
                tr {
                    tdLink(call, state, uniform)
                    tdSkipZero(uniform.equipmentMap.getAllEquipment().size)
                }
            }
        }

        action(createLink, "Add")
        back("/")
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

private fun HTML.showUniformDetails(
    call: ApplicationCall,
    state: State,
    uniform: Uniform,
) {
    val backLink = call.application.href(UniformRoutes.All())
    val deleteLink = call.application.href(UniformRoutes.Delete(uniform.id))
    val editLink = call.application.href(UniformRoutes.Edit(uniform.id))
    val equipped = state.getEquipment(uniform.equipmentMap)
    val svg = visualizeCharacter(state, CHARACTER_CONFIG, appearance, equipped)

    simpleHtml("Uniform: ${uniform.name(state)}") {
        svg(svg, 20)
        showUniform(call, state, uniform)

        action(editLink, "Edit")

        if (state.canDeleteUniform(uniform.id)) {
            action(deleteLink, "Delete")
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
