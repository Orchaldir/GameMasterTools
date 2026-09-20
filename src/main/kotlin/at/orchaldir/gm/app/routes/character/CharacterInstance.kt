package at.orchaldir.gm.app.routes.character

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.Column.Companion.tdColumn
import at.orchaldir.gm.app.html.character.editCharacterInstance
import at.orchaldir.gm.app.html.character.parseCharacterInstance
import at.orchaldir.gm.app.html.character.showCharacterInstance
import at.orchaldir.gm.app.html.createIdColumn
import at.orchaldir.gm.app.html.createNameColumn
import at.orchaldir.gm.app.html.svg
import at.orchaldir.gm.app.html.util.showReference
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CHARACTER_INSTANCE_TYPE
import at.orchaldir.gm.core.model.character.CharacterInstance
import at.orchaldir.gm.core.model.character.CharacterInstanceId
import at.orchaldir.gm.core.model.util.SortCharacterInstance
import at.orchaldir.gm.core.selector.item.equipment.getEquipmentElementMap
import at.orchaldir.gm.core.selector.util.sortCharacterInstances
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.visualization.character.appearance.visualizeCharacter
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$CHARACTER_INSTANCE_TYPE")
class CharacterInstanceRoutes : Routes<CharacterInstanceId, SortCharacterInstance> {
    @Resource("all")
    class All(
        val sort: SortCharacterInstance = SortCharacterInstance.Name,
        val parent: CharacterInstanceRoutes = CharacterInstanceRoutes(),
    )

    @Resource("details")
    class Details(val id: CharacterInstanceId, val parent: CharacterInstanceRoutes = CharacterInstanceRoutes())

    @Resource("new")
    class New(val parent: CharacterInstanceRoutes = CharacterInstanceRoutes())

    @Resource("clone")
    class Clone(val id: CharacterInstanceId, val parent: CharacterInstanceRoutes = CharacterInstanceRoutes())

    @Resource("delete")
    class Delete(val id: CharacterInstanceId, val parent: CharacterInstanceRoutes = CharacterInstanceRoutes())

    @Resource("edit")
    class Edit(val id: CharacterInstanceId, val parent: CharacterInstanceRoutes = CharacterInstanceRoutes())

    @Resource("preview")
    class Preview(val id: CharacterInstanceId, val parent: CharacterInstanceRoutes = CharacterInstanceRoutes())

    @Resource("update")
    class Update(val id: CharacterInstanceId, val parent: CharacterInstanceRoutes = CharacterInstanceRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortCharacterInstance) = call.application.href(All(sort))
    override fun clone(call: ApplicationCall, id: CharacterInstanceId) = call.application.href(Clone(id))
    override fun delete(call: ApplicationCall, id: CharacterInstanceId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: CharacterInstanceId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: CharacterInstanceId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: CharacterInstanceId) = call.application.href(Update(id))
}

fun Application.configureCharacterInstanceRouting() {
    routing {
        get<CharacterInstanceRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                CharacterInstanceRoutes(),
                state.sortCharacterInstances(all.sort),
                listOf(
                    createNameColumn(call, state),
                    tdColumn("Based On") { showReference(call, state, it.basedOn) },
                    createIdColumn(call, state, "Race") { it.race },
                ),
            )
        }
        get<CharacterInstanceRoutes.Details> { details ->
            handleShowElementSplit(
                details.id,
                CharacterInstanceRoutes(),
                HtmlBlockTag::showCharacterInstance,
                HtmlBlockTag::showCharacterInstanceRight,
            )
        }
        get<CharacterInstanceRoutes.New> {
            handleCreateElement(CharacterInstanceRoutes(), STORE.getState().getCharacterInstanceStorage())
        }
        get<CharacterInstanceRoutes.Clone> { clone ->
            handleCloneElement(CharacterInstanceRoutes(), clone.id)
        }
        get<CharacterInstanceRoutes.Delete> { delete ->
            handleDeleteElement(CharacterInstanceRoutes(), delete.id)
        }
        get<CharacterInstanceRoutes.Edit> { edit ->
            handleEditElementSplit(
                edit.id,
                CharacterInstanceRoutes(),
                HtmlBlockTag::editCharacterInstance,
                HtmlBlockTag::showCharacterInstanceRight,
            )
        }
        post<CharacterInstanceRoutes.Preview> { preview ->
            handlePreviewElementSplit(
                preview.id,
                CharacterInstanceRoutes(),
                ::parseCharacterInstance,
                HtmlBlockTag::editCharacterInstance,
                HtmlBlockTag::showCharacterInstanceRight,
            )
        }
        post<CharacterInstanceRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseCharacterInstance)
        }
    }
}

private fun HtmlBlockTag.showCharacterInstanceRight(
    call: ApplicationCall,
    state: State,
    instance: CharacterInstance,
) {
    val equipment = state.getEquipmentElementMap(instance)
    val frontSvg = visualizeCharacter(state, CHARACTER_CONFIG, instance.appearance, equipment)
    val backSvg = visualizeCharacter(state, CHARACTER_CONFIG,instance.appearance, equipment, false)

    svg(frontSvg, 40)
    svg(backSvg, 40)
}
