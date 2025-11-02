package at.orchaldir.gm.app.routes.character

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.character.editPersonalityTrait
import at.orchaldir.gm.app.html.character.parsePersonalityTrait
import at.orchaldir.gm.app.html.character.showPersonalityTrait
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.character.PERSONALITY_TRAIT_TYPE
import at.orchaldir.gm.core.model.character.PersonalityTraitId
import at.orchaldir.gm.core.model.util.SortPersonalityTrait
import at.orchaldir.gm.core.selector.character.getCharacters
import at.orchaldir.gm.core.selector.character.getPersonalityTraitGroups
import at.orchaldir.gm.core.selector.character.getPersonalityTraits
import at.orchaldir.gm.core.selector.religion.getGodsWith
import at.orchaldir.gm.core.selector.util.sortPersonalityTraits
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$PERSONALITY_TRAIT_TYPE")
class PersonalityTraitRoutes : Routes<PersonalityTraitId, SortPersonalityTrait> {
    @Resource("all")
    class All(
        val sort: SortPersonalityTrait = SortPersonalityTrait.Name,
        val parent: PersonalityTraitRoutes = PersonalityTraitRoutes(),
    )

    @Resource("details")
    class Details(val id: PersonalityTraitId, val parent: PersonalityTraitRoutes = PersonalityTraitRoutes())

    @Resource("new")
    class New(val parent: PersonalityTraitRoutes = PersonalityTraitRoutes())

    @Resource("delete")
    class Delete(val id: PersonalityTraitId, val parent: PersonalityTraitRoutes = PersonalityTraitRoutes())

    @Resource("edit")
    class Edit(val id: PersonalityTraitId, val parent: PersonalityTraitRoutes = PersonalityTraitRoutes())

    @Resource("preview")
    class Preview(val id: PersonalityTraitId, val parent: PersonalityTraitRoutes = PersonalityTraitRoutes())

    @Resource("update")
    class Update(val id: PersonalityTraitId, val parent: PersonalityTraitRoutes = PersonalityTraitRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortPersonalityTrait) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: PersonalityTraitId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: PersonalityTraitId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: PersonalityTraitId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: PersonalityTraitId) = call.application.href(Edit(id))
}

fun Application.configurePersonalityRouting() {
    routing {
        get<PersonalityTraitRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                PersonalityTraitRoutes(),
                state.sortPersonalityTraits(all.sort),
                listOf(
                    createNameColumn(call, state),
                    countCollectionColumn("Characters") { state.getCharacters(it.id) },
                    countCollectionColumn("Gods") { state.getGodsWith(it.id) },
                ),
            ) {
                fieldList("By Group", state.getPersonalityTraitGroups()) { group ->
                    state.getPersonalityTraits(group).forEach { trait ->
                        +" "
                        link(call, state, trait)
                    }
                }

                fieldElements(call, state, "Without Group", it.filter { it.group == null })
            }
        }
        get<PersonalityTraitRoutes.Details> { details ->
            handleShowElement(details.id, PersonalityTraitRoutes(), HtmlBlockTag::showPersonalityTrait)
        }
        get<PersonalityTraitRoutes.New> {
            handleCreateElement(STORE.getState().getPersonalityTraitStorage()) { id ->
                PersonalityTraitRoutes.Edit(id)
            }
        }
        get<PersonalityTraitRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, PersonalityTraitRoutes())
        }
        get<PersonalityTraitRoutes.Edit> { edit ->
            handleEditElement(edit.id, PersonalityTraitRoutes(), HtmlBlockTag::editPersonalityTrait)
        }
        post<PersonalityTraitRoutes.Preview> { preview ->
            handlePreviewElement(preview.id, PersonalityTraitRoutes(), ::parsePersonalityTrait, HtmlBlockTag::editPersonalityTrait)
        }
        post<PersonalityTraitRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parsePersonalityTrait)
        }
    }
}
