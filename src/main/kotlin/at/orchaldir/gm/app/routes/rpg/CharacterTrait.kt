package at.orchaldir.gm.app.routes.rpg

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.rpg.trait.editCharacterTrait
import at.orchaldir.gm.app.html.rpg.trait.parseCharacterTrait
import at.orchaldir.gm.app.html.rpg.trait.showCharacterTrait
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.rpg.trait.CHARACTER_TRAIT_TYPE
import at.orchaldir.gm.core.model.rpg.trait.CharacterTraitId
import at.orchaldir.gm.core.model.util.SortCharacterTrait
import at.orchaldir.gm.core.selector.character.getCharacters
import at.orchaldir.gm.core.selector.rpg.getCharacterTraitGroups
import at.orchaldir.gm.core.selector.rpg.getCharacterTraits
import at.orchaldir.gm.core.selector.religion.getGodsWith
import at.orchaldir.gm.core.selector.util.sortCharacterTraits
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$CHARACTER_TRAIT_TYPE")
class CharacterTraitRoutes : Routes<CharacterTraitId, SortCharacterTrait> {
    @Resource("all")
    class All(
        val sort: SortCharacterTrait = SortCharacterTrait.Name,
        val parent: CharacterTraitRoutes = CharacterTraitRoutes(),
    )

    @Resource("details")
    class Details(val id: CharacterTraitId, val parent: CharacterTraitRoutes = CharacterTraitRoutes())

    @Resource("new")
    class New(val parent: CharacterTraitRoutes = CharacterTraitRoutes())

    @Resource("delete")
    class Delete(val id: CharacterTraitId, val parent: CharacterTraitRoutes = CharacterTraitRoutes())

    @Resource("edit")
    class Edit(val id: CharacterTraitId, val parent: CharacterTraitRoutes = CharacterTraitRoutes())

    @Resource("preview")
    class Preview(val id: CharacterTraitId, val parent: CharacterTraitRoutes = CharacterTraitRoutes())

    @Resource("update")
    class Update(val id: CharacterTraitId, val parent: CharacterTraitRoutes = CharacterTraitRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortCharacterTrait) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: CharacterTraitId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: CharacterTraitId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: CharacterTraitId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: CharacterTraitId) = call.application.href(Update(id))
}

fun Application.configureCharacterTraitRouting() {
    routing {
        get<CharacterTraitRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                CharacterTraitRoutes(),
                state.sortCharacterTraits(all.sort),
                listOf(
                    createNameColumn(call, state),
                    Column("Cost") { tdInt(it.cost) },
                    countCollectionColumn("Characters") { state.getCharacters(it.id) },
                    countCollectionColumn("Gods") { state.getGodsWith(it.id) },
                ),
            ) {
                fieldList("By Group", state.getCharacterTraitGroups()) { group ->
                    state.getCharacterTraits(group).forEach { trait ->
                        +" "
                        link(call, state, trait)
                    }
                }

                fieldElements(call, state, "Without Group", it.filter { it.group == null })
            }
        }
        get<CharacterTraitRoutes.Details> { details ->
            handleShowElement(details.id, CharacterTraitRoutes(), HtmlBlockTag::showCharacterTrait)
        }
        get<CharacterTraitRoutes.New> {
            handleCreateElement(CharacterTraitRoutes(), STORE.getState().getCharacterTraitStorage())
        }
        get<CharacterTraitRoutes.Delete> { delete ->
            handleDeleteElement(CharacterTraitRoutes(), delete.id)
        }
        get<CharacterTraitRoutes.Edit> { edit ->
            handleEditElement(edit.id, CharacterTraitRoutes(), HtmlBlockTag::editCharacterTrait)
        }
        post<CharacterTraitRoutes.Preview> { preview ->
            handlePreviewElement(
                preview.id,
                CharacterTraitRoutes(),
                ::parseCharacterTrait,
                HtmlBlockTag::editCharacterTrait
            )
        }
        post<CharacterTraitRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseCharacterTrait)
        }
    }
}
