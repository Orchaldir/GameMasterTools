package at.orchaldir.gm.app.routes.rpg

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.Column.Companion.tdColumn
import at.orchaldir.gm.app.html.rpg.combat.displayAttackEffect
import at.orchaldir.gm.app.html.rpg.combat.displayParrying
import at.orchaldir.gm.app.html.rpg.combat.displayReach
import at.orchaldir.gm.app.html.rpg.combat.editMeleeWeapon
import at.orchaldir.gm.app.html.rpg.combat.parseMeleeWeapon
import at.orchaldir.gm.app.html.rpg.combat.showMeleeWeapon
import at.orchaldir.gm.app.html.rpg.statistic.displayBaseValue
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.MELEE_WEAPON_TYPE
import at.orchaldir.gm.core.model.rpg.combat.MeleeWeapon
import at.orchaldir.gm.core.model.rpg.combat.MeleeWeaponId
import at.orchaldir.gm.core.model.util.SortMeleeWeapon
import at.orchaldir.gm.core.selector.util.sortMeleeWeapons
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

@Resource("/$MELEE_WEAPON_TYPE")
class MeleeWeaponRoutes : Routes<MeleeWeaponId, SortMeleeWeapon> {
    @Resource("all")
    class All(
        val sort: SortMeleeWeapon = SortMeleeWeapon.Name,
        val parent: MeleeWeaponRoutes = MeleeWeaponRoutes(),
    )

    @Resource("details")
    class Details(val id: MeleeWeaponId, val parent: MeleeWeaponRoutes = MeleeWeaponRoutes())

    @Resource("new")
    class New(val parent: MeleeWeaponRoutes = MeleeWeaponRoutes())

    @Resource("delete")
    class Delete(val id: MeleeWeaponId, val parent: MeleeWeaponRoutes = MeleeWeaponRoutes())

    @Resource("edit")
    class Edit(val id: MeleeWeaponId, val parent: MeleeWeaponRoutes = MeleeWeaponRoutes())

    @Resource("preview")
    class Preview(val id: MeleeWeaponId, val parent: MeleeWeaponRoutes = MeleeWeaponRoutes())

    @Resource("update")
    class Update(val id: MeleeWeaponId, val parent: MeleeWeaponRoutes = MeleeWeaponRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortMeleeWeapon) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: MeleeWeaponId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: MeleeWeaponId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
}

fun Application.configureMeleeWeaponRouting() {
    routing {
        get<MeleeWeaponRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                MeleeWeaponRoutes(),
                state.sortMeleeWeapons(all.sort),
                listOf(
                    createNameColumn(call, state),
                    tdColumn("Damage") {
                        showMultiLine(it.attacks) { attack ->
                            displayAttackEffect(call, state, attack.effect)
                        }
                    },
                    tdColumn("Reach") {
                        showMultiLine(it.attacks) { attack ->
                            displayReach(attack.reach)
                        }
                    },
                    tdColumn("Parrying") {
                        showMultiLine(it.attacks) { attack ->
                            displayParrying(attack.parrying)
                        }
                    },
                ),
            )
        }
        get<MeleeWeaponRoutes.Details> { details ->
            handleShowElement(details.id, MeleeWeaponRoutes(), HtmlBlockTag::showMeleeWeapon)
        }
        get<MeleeWeaponRoutes.New> {
            handleCreateElement(STORE.getState().getMeleeWeaponStorage()) { id ->
                MeleeWeaponRoutes.Edit(id)
            }
        }
        get<MeleeWeaponRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, MeleeWeaponRoutes.All())
        }
        get<MeleeWeaponRoutes.Edit> { edit ->
            logger.info { "Get editor for type ${edit.id.value}" }

            val state = STORE.getState()
            val type = state.getMeleeWeaponStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showMeleeWeaponEditor(call, state, type)
            }
        }
        post<MeleeWeaponRoutes.Preview> { preview ->
            logger.info { "Get preview for type ${preview.id.value}" }

            val formParameters = call.receiveParameters()
            val state = STORE.getState()
            val type = parseMeleeWeapon(state, formParameters, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showMeleeWeaponEditor(call, state, type)
            }
        }
        post<MeleeWeaponRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseMeleeWeapon)
        }
    }
}

private fun HTML.showMeleeWeaponEditor(
    call: ApplicationCall,
    state: State,
    type: MeleeWeapon,
) {
    val backLink = href(call, type.id)
    val previewLink = call.application.href(MeleeWeaponRoutes.Preview(type.id))
    val updateLink = call.application.href(MeleeWeaponRoutes.Update(type.id))

    simpleHtmlEditor(type, true) {
        mainFrame {
            formWithPreview(previewLink, updateLink, backLink) {
                editMeleeWeapon(state, type)
            }
        }
    }
}

