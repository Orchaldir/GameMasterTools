package at.orchaldir.gm.app.routes

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.showBeliefStatus
import at.orchaldir.gm.app.html.model.showDate
import at.orchaldir.gm.app.html.model.showEmploymentStatus
import at.orchaldir.gm.app.html.model.showHousingStatus
import at.orchaldir.gm.app.html.model.time.editHolidays
import at.orchaldir.gm.app.html.model.time.showHolidays
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parseCulture
import at.orchaldir.gm.core.action.CloneCulture
import at.orchaldir.gm.core.action.CreateCulture
import at.orchaldir.gm.core.action.DeleteCulture
import at.orchaldir.gm.core.action.UpdateCulture
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Dead
import at.orchaldir.gm.core.model.culture.CULTURE_TYPE
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.culture.name.*
import at.orchaldir.gm.core.model.name.NameListId
import at.orchaldir.gm.core.model.time.calendar.CALENDAR_TYPE
import at.orchaldir.gm.core.model.util.GenderMap
import at.orchaldir.gm.core.model.util.Rarity
import at.orchaldir.gm.core.selector.canDelete
import at.orchaldir.gm.core.selector.getAgeInYears
import at.orchaldir.gm.core.selector.getCharacters
import at.orchaldir.gm.core.selector.organization.getOrganizations
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

@Resource("/$CULTURE_TYPE")
class CultureRoutes {
    @Resource("details")
    class Details(val id: CultureId, val parent: CultureRoutes = CultureRoutes())

    @Resource("new")
    class New(val parent: CultureRoutes = CultureRoutes())

    @Resource("clone")
    class Clone(val id: CultureId, val parent: CultureRoutes = CultureRoutes())

    @Resource("delete")
    class Delete(val id: CultureId, val parent: CultureRoutes = CultureRoutes())

    @Resource("edit")
    class Edit(val id: CultureId, val parent: CultureRoutes = CultureRoutes())

    @Resource("preview")
    class Preview(val id: CultureId, val parent: CultureRoutes = CultureRoutes())

    @Resource("update")
    class Update(val id: CultureId, val parent: CultureRoutes = CultureRoutes())
}

fun Application.configureCultureRouting() {
    routing {
        get<CultureRoutes> {
            logger.info { "Get all cultures" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllCultures(call, STORE.getState())
            }
        }
        get<CultureRoutes.Details> { details ->
            logger.info { "Get details of culture ${details.id.value}" }

            val state = STORE.getState()
            val culture = state.getCultureStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCultureDetails(call, state, culture)
            }
        }
        get<CultureRoutes.New> {
            logger.info { "Add new culture" }

            STORE.dispatch(CreateCulture)

            call.respondRedirect(call.application.href(CultureRoutes.Edit(STORE.getState().getCultureStorage().lastId)))

            STORE.getState().save()
        }
        get<CultureRoutes.Clone> { clone ->
            logger.info { "Clone culture ${clone.id.value}" }

            STORE.dispatch(CloneCulture(clone.id))

            call.respondRedirect(call.application.href(CultureRoutes.Edit(STORE.getState().getCultureStorage().lastId)))

            STORE.getState().save()
        }
        get<CultureRoutes.Delete> { delete ->
            logger.info { "Delete culture ${delete.id.value}" }

            STORE.dispatch(DeleteCulture(delete.id))

            call.respondRedirect(call.application.href(CultureRoutes()))

            STORE.getState().save()
        }
        get<CultureRoutes.Edit> { edit ->
            logger.info { "Get editor for culture ${edit.id.value}" }

            val state = STORE.getState()
            val culture = state.getCultureStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCultureEditor(call, state, culture)
            }
        }
        post<CultureRoutes.Preview> { preview ->
            logger.info { "Get preview for culture ${preview.id.value}" }

            val formParameters = call.receiveParameters()
            val culture = parseCulture(formParameters, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCultureEditor(call, STORE.getState(), culture)
            }
        }
        post<CultureRoutes.Update> { update ->
            logger.info { "Update culture ${update.id.value}" }

            val formParameters = call.receiveParameters()
            val culture = parseCulture(formParameters, update.id)

            STORE.dispatch(UpdateCulture(culture))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllCultures(
    call: ApplicationCall,
    state: State,
) {
    val cultures = STORE.getState().getCultureStorage().getAll().sortedBy { it.name }
    val count = cultures.size
    val createLink = call.application.href(CultureRoutes.New())

    simpleHtml("Cultures") {
        field("Count", count)

        table {
            tr {
                th { +"Name" }
                th { +"Calendar" }
                th { +"Languages" }
                th { +"Holidays" }
                th { +"Characters" }
            }
            cultures.forEach { culture ->
                tr {
                    td { link(call, state, culture.id) }
                    td { link(call, state, culture.calendar) }
                    td {
                        showInlineList(culture.languages.getValuesFor(Rarity.Everyone)) { language ->
                            link(call, state, language)
                        }
                    }
                    tdSkipZero(culture.holidays.size)
                    tdSkipZero(state.getCharacters(culture.id).size)
                }
            }
        }

        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showCultureDetails(
    call: ApplicationCall,
    state: State,
    culture: Culture,
) {
    val namingConvention = culture.namingConvention
    val backLink = call.application.href(CultureRoutes())
    val cloneLink = call.application.href(CultureRoutes.Clone(culture.id))
    val deleteLink = call.application.href(CultureRoutes.Delete(culture.id))
    val editLink = call.application.href(CultureRoutes.Edit(culture.id))

    simpleHtml("Culture: ${culture.name}") {
        field("Name", culture.name)
        fieldLink("Calendar", call, state, culture.calendar)
        showRarityMap("Languages", culture.languages) { l ->
            link(call, state, l)
        }
        showHolidays(call, state, culture.holidays)
        showNamingConvention(namingConvention, call, state)
        showAppearanceOptions(culture)
        showClothingOptions(call, state, culture)
        h2 { +"Usage" }
        showList("Characters", state.getCharacters(culture.id)) { character ->
            link(call, state, character)
        }

        action(editLink, "Edit")
        action(cloneLink, "Clone")

        if (state.canDelete(culture.id)) {
            action(deleteLink, "Delete")
        }

        back(backLink)
    }
}

private fun BODY.showNamingConvention(
    namingConvention: NamingConvention,
    call: ApplicationCall,
    state: State,
) {
    h2 { +"Naming Convention" }
    field("Type", namingConvention.javaClass.simpleName)
    when (namingConvention) {
        is FamilyConvention -> {
            field("Name Order", namingConvention.nameOrder)
            showRarityMap("Middle Name Options", namingConvention.middleNameOptions)
            showNamesByGender(call, state, "Given Names", namingConvention.givenNames)
            fieldLink("Family Names", call, state, namingConvention.familyNames)
        }

        is GenonymConvention -> showGenonymConvention(
            call,
            state,
            namingConvention.lookupDistance,
            namingConvention.style,
            namingConvention.names
        )

        is MatronymConvention -> showGenonymConvention(
            call,
            state,
            namingConvention.lookupDistance,
            namingConvention.style,
            namingConvention.names
        )

        is MononymConvention -> showNamesByGender(call, state, "Names", namingConvention.names)

        NoNamingConvention -> doNothing()
        is PatronymConvention -> showGenonymConvention(
            call,
            state,
            namingConvention.lookupDistance,
            namingConvention.style,
            namingConvention.names
        )
    }
}

private fun BODY.showGenonymConvention(
    call: ApplicationCall,
    state: State,
    lookupDistance: GenonymicLookupDistance,
    style: GenonymicStyle,
    names: GenderMap<NameListId>,
) {
    field("Lookup Distance", lookupDistance)
    field("Genonymic Style", style.javaClass.simpleName)
    when (style) {
        is ChildOfStyle -> showStyleByGender("Words", style.words)
        NamesOnlyStyle -> doNothing()
        is PrefixStyle -> showStyleByGender("Prefix", style.prefix)
        is SuffixStyle -> showStyleByGender("Suffix", style.suffix)
    }
    showNamesByGender(call, state, "Names", names)
}

private fun BODY.showNamesByGender(
    call: ApplicationCall,
    state: State,
    label: String,
    namesByGender: GenderMap<NameListId>,
) {
    showDetails(label) {
        showGenderMap(namesByGender) { gender, id ->
            fieldLink(gender.toString(), call, state, id)
        }
    }
}

private fun BODY.showStyleByGender(
    label: String,
    namesByGender: GenderMap<String>,
) {
    showDetails(label) {
        showGenderMap(namesByGender) { gender, text ->
            field(gender.toString(), text)
        }
    }
}

private fun BODY.showAppearanceOptions(culture: Culture) {
    val appearanceStyle = culture.appearanceStyle

    h2 { +"Appearance Options" }
    showRarityMap("Beard Styles", appearanceStyle.beardStyles)
    showRarityMap("Goatee Styles", appearanceStyle.goateeStyles)
    showRarityMap("Moustache Styles", appearanceStyle.moustacheStyles)
    showRarityMap("Hair Styles", appearanceStyle.hairStyles)
    showRarityMap("Lip Colors", appearanceStyle.lipColors)
}

private fun BODY.showClothingOptions(
    call: ApplicationCall,
    state: State,
    culture: Culture,
) {
    h2 { +"Fashion" }
    showGenderMap(culture.clothingStyles) { gender, id ->
        optionalFieldLink(gender.toString(), call, state, id)
    }
}

private fun HTML.showCultureEditor(
    call: ApplicationCall,
    state: State,
    culture: Culture,
) {
    val namingConvention = culture.namingConvention
    val backLink = href(call, culture.id)
    val previewLink = call.application.href(CultureRoutes.Preview(culture.id))
    val updateLink = call.application.href(CultureRoutes.Update(culture.id))

    simpleHtml("Edit Culture: ${culture.name}") {
        form {
            id = "editor"
            action = previewLink
            method = FormMethod.post
            selectName(culture.name)
            selectElement(state, "Calendar", CALENDAR_TYPE, state.getCalendarStorage().getAll(), culture.calendar)
            selectRarityMap("Languages", LANGUAGES, state.getLanguageStorage(), culture.languages) { it.name }
            editHolidays(state, culture.holidays)
            editNamingConvention(namingConvention, state)
            editAppearanceOptions(culture)
            editClothingOptions(state, culture)
            button("Update", updateLink)
        }
        back(backLink)
    }
}

private fun FORM.editNamingConvention(
    namingConvention: NamingConvention,
    state: State,
) {
    h2 { +"Naming Convention" }
    selectValue("Type", NAMING_CONVENTION, NamingConventionType.entries, namingConvention.getType(), true)

    when (namingConvention) {
        is FamilyConvention -> {
            selectValue("Name Order", combine(NAME, ORDER), NameOrder.entries, namingConvention.nameOrder, true)
            selectRarityMap("Middle Name Options", MIDDLE_NAME, namingConvention.middleNameOptions)
            selectNamesByGender(state, "Given Names", namingConvention.givenNames, NAMES)
            field("Family Names") {
                selectNameList(FAMILY_NAMES, state, namingConvention.familyNames)
            }
        }

        is GenonymConvention -> selectGenonymConvention(
            state,
            namingConvention.lookupDistance,
            namingConvention.style,
            namingConvention.names
        )

        is MatronymConvention -> selectGenonymConvention(
            state,
            namingConvention.lookupDistance,
            namingConvention.style,
            namingConvention.names
        )

        is MononymConvention -> selectNamesByGender(state, "Names", namingConvention.names, NAMES)

        NoNamingConvention -> doNothing()
        is PatronymConvention -> selectGenonymConvention(
            state,
            namingConvention.lookupDistance,
            namingConvention.style,
            namingConvention.names
        )
    }
}

private fun FORM.selectGenonymConvention(
    state: State,
    lookupDistance: GenonymicLookupDistance,
    style: GenonymicStyle,
    names: GenderMap<NameListId>,
) {
    selectValue("Lookup Distance", LOOKUP_DISTANCE, GenonymicLookupDistance.entries, lookupDistance)
    selectValue("Genonymic Style", GENONYMIC_STYLE, GenonymicStyleType.entries, style.getType(), true)

    when (style) {
        is ChildOfStyle -> selectWordsByGender("Words", style.words, WORD)
        NamesOnlyStyle -> doNothing()
        is PrefixStyle -> selectWordsByGender("Prefix", style.prefix, WORD)
        is SuffixStyle -> selectWordsByGender("Suffix", style.suffix, WORD)
    }
    selectNamesByGender(state, "Names", names, NAMES)
}

private fun FORM.selectNamesByGender(
    state: State,
    fieldLabel: String,
    namesByGender: GenderMap<NameListId>,
    param: String,
) {
    selectGenderMap(fieldLabel, namesByGender) { gender, nameListId ->
        val selectId = "$param-$gender"
        selectNameList(selectId, state, nameListId)
    }
}

private fun HtmlBlockTag.selectNameList(
    selectId: String,
    state: State,
    nameListId: NameListId,
) {
    select {
        id = selectId
        name = selectId
        state.getNameListStorage().getAll().forEach { nameList ->
            option {
                label = nameList.name
                value = nameList.id.value.toString()
                selected = nameList.id == nameListId
            }
        }
    }
}

private fun FORM.selectWordsByGender(label: String, genderMap: GenderMap<String>, param: String) {
    selectGenderMap(label, genderMap) { gender, word ->
        textInput(name = "$param-$gender") {
            value = word
        }
    }
}

private fun FORM.editAppearanceOptions(culture: Culture) {
    h2 { +"Appearance Options" }
    selectRarityMap("Beard Styles", combine(BEARD, STYLE), culture.appearanceStyle.beardStyles)
    selectRarityMap("Goatee Styles", GOATEE_STYLE, culture.appearanceStyle.goateeStyles)
    selectRarityMap("Moustache Styles", MOUSTACHE_STYLE, culture.appearanceStyle.moustacheStyles)
    selectRarityMap("Hair Styles", combine(HAIR, STYLE), culture.appearanceStyle.hairStyles)
    selectRarityMap("Lip Colors", LIP_COLORS, culture.appearanceStyle.lipColors)
}

private fun FORM.editClothingOptions(
    state: State,
    culture: Culture,
) {
    h2 { +"Fashion" }

    showMap(culture.clothingStyles.getMap()) { gender, fashionId ->
        field(gender.toString()) {
            val selectId = "$FASHION-$gender"
            select {
                id = selectId
                name = selectId
                option {
                    label = "None"
                    value = ""
                    selected = fashionId == null
                }
                state.getFashionStorage().getAll().forEach { fashion ->
                    option {
                        label = fashion.name
                        value = fashion.id.value.toString()
                        selected = fashion.id == fashionId
                    }
                }
            }
        }
    }
}

