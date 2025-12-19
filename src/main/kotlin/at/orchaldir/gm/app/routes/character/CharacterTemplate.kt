package at.orchaldir.gm.app.routes.character

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.Column.Companion.tdColumn
import at.orchaldir.gm.app.html.character.editCharacterTemplate
import at.orchaldir.gm.app.html.character.parseCharacterTemplate
import at.orchaldir.gm.app.html.character.showCharacterTemplate
import at.orchaldir.gm.app.html.character.showEquipped
import at.orchaldir.gm.app.html.rpg.statblock.showStatblockLookup
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.app.routes.race.generateAppearance
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CHARACTER_TEMPLATE_TYPE
import at.orchaldir.gm.core.model.character.CharacterTemplate
import at.orchaldir.gm.core.model.character.CharacterTemplateId
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.util.SortCharacterTemplate
import at.orchaldir.gm.core.selector.culture.getAppearanceFashion
import at.orchaldir.gm.core.selector.item.getEquipment
import at.orchaldir.gm.core.selector.util.sortCharacterTemplates
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.visualization.character.appearance.visualizeCharacter
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$CHARACTER_TEMPLATE_TYPE")
class CharacterTemplateRoutes : Routes<CharacterTemplateId, SortCharacterTemplate> {
    @Resource("all")
    class All(
        val sort: SortCharacterTemplate = SortCharacterTemplate.Name,
        val parent: CharacterTemplateRoutes = CharacterTemplateRoutes(),
    )

    @Resource("details")
    class Details(val id: CharacterTemplateId, val parent: CharacterTemplateRoutes = CharacterTemplateRoutes())

    @Resource("new")
    class New(val parent: CharacterTemplateRoutes = CharacterTemplateRoutes())

    @Resource("clone")
    class Clone(val id: CharacterTemplateId, val parent: CharacterTemplateRoutes = CharacterTemplateRoutes())

    @Resource("delete")
    class Delete(val id: CharacterTemplateId, val parent: CharacterTemplateRoutes = CharacterTemplateRoutes())

    @Resource("edit")
    class Edit(val id: CharacterTemplateId, val parent: CharacterTemplateRoutes = CharacterTemplateRoutes())

    @Resource("preview")
    class Preview(val id: CharacterTemplateId, val parent: CharacterTemplateRoutes = CharacterTemplateRoutes())

    @Resource("update")
    class Update(val id: CharacterTemplateId, val parent: CharacterTemplateRoutes = CharacterTemplateRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortCharacterTemplate) = call.application.href(All(sort))
    override fun clone(call: ApplicationCall, id: CharacterTemplateId) = call.application.href(Clone(id))
    override fun delete(call: ApplicationCall, id: CharacterTemplateId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: CharacterTemplateId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: CharacterTemplateId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: CharacterTemplateId) = call.application.href(Update(id))
}

fun Application.configureCharacterTemplateRouting() {
    routing {
        get<CharacterTemplateRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                CharacterTemplateRoutes(),
                state.sortCharacterTemplates(all.sort),
                listOf(
                    createNameColumn(call, state),
                    Column("Race") { tdLink(call, state, it.race) },
                    Column("Culture") { tdLink(call, state, it.culture) },
                    createBeliefColumn(call, state),
                    tdColumn("Statblock") { showStatblockLookup(call, state, it.statblock) },
                    tdColumn("Equipped") { showEquipped(call, state, it.equipped, it.statblock) },
                    countColumn("Cost") { it.statblock.calculateCost(it.race, state) },
                ),
            )
        }
        get<CharacterTemplateRoutes.Details> { details ->
            handleShowElementSplit(
                details.id,
                CharacterTemplateRoutes(),
                HtmlBlockTag::showCharacterTemplate,
                HtmlBlockTag::showCharacterTemplateRight,
            )
        }
        get<CharacterTemplateRoutes.New> {
            handleCreateElement(CharacterTemplateRoutes(), STORE.getState().getCharacterTemplateStorage())
        }
        get<CharacterTemplateRoutes.Clone> { clone ->
            handleCloneElement(CharacterTemplateRoutes(), clone.id)
        }
        get<CharacterTemplateRoutes.Delete> { delete ->
            handleDeleteElement(CharacterTemplateRoutes(), delete.id)
        }
        get<CharacterTemplateRoutes.Edit> { edit ->
            handleEditElementSplit(
                edit.id,
                CharacterTemplateRoutes(),
                HtmlBlockTag::editCharacterTemplate,
                HtmlBlockTag::showCharacterTemplateRight,
            )
        }
        post<CharacterTemplateRoutes.Preview> { preview ->
            handlePreviewElementSplit(
                preview.id,
                CharacterTemplateRoutes(),
                ::parseCharacterTemplate,
                HtmlBlockTag::editCharacterTemplate,
                HtmlBlockTag::showCharacterTemplateRight,
            )
        }
        post<CharacterTemplateRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseCharacterTemplate)
        }
    }
}

private fun HtmlBlockTag.showCharacterTemplateRight(
    call: ApplicationCall,
    state: State,
    template: CharacterTemplate,
) {
    val gender = template.gender ?: Gender.Male
    val appearance = generateAppearance(
        state,
        state.getRaceStorage().getOrThrow(template.race),
        gender,
        state.getAppearanceFashion(gender, template.culture),
    )
    val equipped = state.getEquipment(template)
    val svg = visualizeCharacter(state, CHARACTER_CONFIG, appearance, equipped)

    svg(svg, 80)
}
