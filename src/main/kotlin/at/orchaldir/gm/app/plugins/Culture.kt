package at.orchaldir.gm.app.plugins

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.*
import at.orchaldir.gm.core.action.CreateCulture
import at.orchaldir.gm.core.action.DeleteCulture
import at.orchaldir.gm.core.action.UpdateCulture
import at.orchaldir.gm.core.model.NameListId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.appearance.GenderMap
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.culture.name.*
import at.orchaldir.gm.core.selector.canDelete
import at.orchaldir.gm.core.selector.getCharacters
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/cultures")
class Cultures {
    @Resource("details")
    class Details(val id: CultureId, val parent: Cultures = Cultures())

    @Resource("new")
    class New(val parent: Cultures = Cultures())

    @Resource("delete")
    class Delete(val id: CultureId, val parent: Cultures = Cultures())

    @Resource("edit")
    class Edit(val id: CultureId, val parent: Cultures = Cultures())

    @Resource("update")
    class Update(val id: CultureId, val parent: Cultures = Cultures())
}

fun Application.configureCultureRouting() {
    routing {
        get<Cultures> {
            logger.info { "Get all cultures" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllCultures(call)
            }
        }
        get<Cultures.Details> { details ->
            logger.info { "Get details of culture ${details.id.value}" }

            val state = STORE.getState()
            val culture = state.cultures.getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCultureDetails(call, state, culture)
            }
        }
        get<Cultures.New> {
            logger.info { "Add new culture" }

            STORE.dispatch(CreateCulture)

            call.respondRedirect(call.application.href(Cultures.Edit(STORE.getState().cultures.lastId)))

            STORE.getState().save()
        }
        get<Cultures.Delete> { delete ->
            logger.info { "Delete culture ${delete.id.value}" }

            STORE.dispatch(DeleteCulture(delete.id))

            call.respondRedirect(call.application.href(Cultures()))

            STORE.getState().save()
        }
        get<Cultures.Edit> { edit ->
            logger.info { "Get editor for culture ${edit.id.value}" }

            val state = STORE.getState()
            val culture = state.cultures.getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCultureEditor(call, state, culture)
            }
        }
        post<Cultures.Update> { update ->
            logger.info { "Update culture ${update.id.value}" }

            val formParameters = call.receiveParameters()
            val culture = parseCulture(formParameters, update.id)

            STORE.dispatch(UpdateCulture(culture))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllCultures(call: ApplicationCall) {
    val cultures = STORE.getState().cultures.getAll().sortedBy { it.name }
    val count = cultures.size
    val createLink = call.application.href(Cultures.New(Cultures()))

    simpleHtml("Cultures") {
        field("Count", count.toString())
        showList(cultures) { culture ->
            link(call, culture)
        }
        p { a(createLink) { +"Add" } }
        p { a("/") { +"Back" } }
    }
}

private fun HTML.showCultureDetails(
    call: ApplicationCall,
    state: State,
    culture: Culture,
) {
    val backLink = call.application.href(Cultures())
    val deleteLink = call.application.href(Cultures.Delete(culture.id))
    val editLink = call.application.href(Cultures.Edit(culture.id))

    simpleHtml("Culture: ${culture.name}") {
        field("Id", culture.id.value.toString())
        field("Name", culture.name)
        h2 { +"Naming Convention" }
        field("Type", culture.namingConvention.javaClass.simpleName)
        when (culture.namingConvention) {
            is FamilyConvention -> {
                field("Name Order", culture.namingConvention.nameOrder.toString())
                showRarityMap("Middle Name Options", culture.namingConvention.middleNameOptions)
                showNamesByGender(call, state, "Given Names", culture.namingConvention.givenNames)
                showNamesByGender(call, state, "Family Names", culture.namingConvention.familyNames)
            }

            is GenonymConvention -> showNamesByGender(call, state, "Names", culture.namingConvention.names)
            is MatronymConvention -> showNamesByGender(call, state, "Names", culture.namingConvention.names)
            is MononymConvention -> showNamesByGender(call, state, "Names", culture.namingConvention.names)

            NoNamingConvention -> doNothing()
            is PatronymConvention -> showNamesByGender(call, state, "Names", culture.namingConvention.names)
        }
        h2 { +"Style Options" }
        showRarityMap("Beard Styles", culture.styleOptions.beardStyles)
        showRarityMap("Goatee Styles", culture.styleOptions.goateeStyles)
        showRarityMap("Moustache Styles", culture.styleOptions.moustacheStyle)
        showRarityMap("Hair Styles", culture.styleOptions.hairStyles)
        showRarityMap("Lip Colors", culture.styleOptions.lipColors)
        h2 { +"Characters" }
        showList(state.getCharacters(culture.id)) { character ->
            link(call, character)
        }
        p { a(editLink) { +"Edit" } }

        if (state.canDelete(culture.id)) {
            p { a(deleteLink) { +"Delete" } }
        }

        p { a(backLink) { +"Back" } }
    }
}

private fun BODY.showNamesByGender(
    call: ApplicationCall,
    state: State,
    label: String,
    namesByGender: GenderMap<NameListId>,
) {
    details {
        summary { +label }
        showGenderMap(namesByGender) { gender, id ->
            field(gender.toString()) {
                link(call, state, id)
            }
        }
    }
}

private fun HTML.showCultureEditor(
    call: ApplicationCall,
    state: State,
    culture: Culture,
) {
    val backLink = href(call, culture.id)
    val updateLink = call.application.href(Cultures.Update(culture.id))

    simpleHtml("Edit Culture: ${culture.name}") {
        field("Id", culture.id.value.toString())
        form {
            field("Name") {
                textInput(name = NAME) {
                    value = culture.name
                }
            }
            h2 { +"Naming Convention" }
            selectEnum("Type", NAMING_CONVENTION, NamingConventionType.entries) { type ->
                label = type.toString()
                value = type.toString()
                selected = when (type) {
                    NamingConventionType.None -> culture.namingConvention is NoNamingConvention
                    NamingConventionType.Mononym -> culture.namingConvention is MononymConvention
                    NamingConventionType.Family -> culture.namingConvention is FamilyConvention
                    NamingConventionType.Patronym -> culture.namingConvention is PatronymConvention
                    NamingConventionType.Matronym -> culture.namingConvention is MatronymConvention
                    NamingConventionType.Genonym -> culture.namingConvention is GenonymConvention
                }
            }
            when (culture.namingConvention) {
                is FamilyConvention -> {
                    selectEnum("Name Order", NAME_ORDER, NameOrder.entries, true) { o ->
                        label = o.name
                        value = o.toString()
                        selected = culture.namingConvention.nameOrder == o
                    }
                    selectRarityMap("Middle Name Options", MIDDLE_NAME, culture.namingConvention.middleNameOptions)
                    selectNamesByGender(state, "Given Names", culture.namingConvention.givenNames, NAMES)
                    selectNamesByGender(state, "Family Names", culture.namingConvention.familyNames, FAMILY_NAMES)
                }

                is GenonymConvention -> selectNamesByGender(state, "Names", culture.namingConvention.names, NAMES)
                is MatronymConvention -> selectNamesByGender(state, "Names", culture.namingConvention.names, NAMES)
                is MononymConvention -> selectNamesByGender(state, "Names", culture.namingConvention.names, NAMES)

                NoNamingConvention -> doNothing()
                is PatronymConvention -> selectNamesByGender(state, "Names", culture.namingConvention.names, NAMES)
            }
            h2 { +"Style Options" }
            selectRarityMap("Beard Styles", BEARD_STYLE, culture.styleOptions.beardStyles)
            selectRarityMap("Goatee Styles", GOATEE_STYLE, culture.styleOptions.goateeStyles)
            selectRarityMap("Moustache Styles", MOUSTACHE_STYLE, culture.styleOptions.moustacheStyle)
            selectRarityMap("Hair Styles", HAIR_STYLE, culture.styleOptions.hairStyles)
            selectRarityMap("Lip Colors", LIP_COLORS, culture.styleOptions.lipColors)
            p {
                submitInput {
                    value = "Update"
                    formAction = updateLink
                    formMethod = InputFormMethod.post
                }
            }
        }
        p { a(backLink) { +"Back" } }
    }
}

private fun FORM.selectNamesByGender(
    state: State,
    fieldLabel: String,
    namesByGender: GenderMap<NameListId>,
    param: String,
) {
    selectGenderMap(fieldLabel, namesByGender) { gender, nameListId ->
        val selectId = "$param-$gender"
        select {
            id = selectId
            name = selectId
            state.nameLists.getAll().forEach { nameList ->
                option {
                    label = nameList.name
                    value = nameList.id.value.toString()
                    selected = nameList.id == nameListId
                }
            }
        }
    }
}