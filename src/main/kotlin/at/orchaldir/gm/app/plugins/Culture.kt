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
import at.orchaldir.gm.core.model.calendar.CALENDAR
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

    @Resource("preview")
    class Preview(val id: CultureId, val parent: Cultures = Cultures())

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
            val culture = state.getCultureStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCultureDetails(call, state, culture)
            }
        }
        get<Cultures.New> {
            logger.info { "Add new culture" }

            STORE.dispatch(CreateCulture)

            call.respondRedirect(call.application.href(Cultures.Edit(STORE.getState().getCultureStorage().lastId)))

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
            val culture = state.getCultureStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCultureEditor(call, state, culture)
            }
        }
        post<Cultures.Preview> { preview ->
            logger.info { "Get preview for race ${preview.id.value}" }

            val formParameters = call.receiveParameters()
            val culture = parseCulture(formParameters, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCultureEditor(call, STORE.getState(), culture)
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
    val cultures = STORE.getState().getCultureStorage().getAll().sortedBy { it.name }
    val count = cultures.size
    val createLink = call.application.href(Cultures.New())

    simpleHtml("Cultures") {
        field("Count", count.toString())
        showList(cultures) { culture ->
            link(call, culture)
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
    val backLink = call.application.href(Cultures())
    val deleteLink = call.application.href(Cultures.Delete(culture.id))
    val editLink = call.application.href(Cultures.Edit(culture.id))

    simpleHtml("Culture: ${culture.name}") {
        field("Id", culture.id.value.toString())
        field("Name", culture.name)
        field("Calendar") {
            link(call, state, culture.calendar)
        }
        showRarityMap("Languages", culture.languages) { l ->
            link(call, state, l)
        }
        showDetails("Holidays") {
            showList(culture.holidays) { holiday ->
                link(call, state, holiday)
            }
        }
        showNamingConvention(namingConvention, call, state)
        showAppearanceOptions(culture)
        showClothingOptions(call, state, culture)
        h2 { +"Usage" }
        showList("Characters", state.getCharacters(culture.id)) { character ->
            link(call, state, character)
        }
        action(editLink, "Edit")

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
            field("Name Order", namingConvention.nameOrder.toString())
            showRarityMap("Middle Name Options", namingConvention.middleNameOptions)
            showNamesByGender(call, state, "Given Names", namingConvention.givenNames)
            field("Family Names") {
                link(call, state, namingConvention.familyNames)
            }
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
    field("Lookup Distance", lookupDistance.toString())
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
            field(gender.toString()) {
                link(call, state, id)
            }
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
        field(gender.toString()) {
            link(call, state, id)
        }
    }
}

private fun HTML.showCultureEditor(
    call: ApplicationCall,
    state: State,
    culture: Culture,
) {
    val namingConvention = culture.namingConvention
    val backLink = href(call, culture.id)
    val previewLink = call.application.href(Cultures.Preview(culture.id))
    val updateLink = call.application.href(Cultures.Update(culture.id))

    simpleHtml("Edit Culture: ${culture.name}") {
        field("Id", culture.id.value.toString())
        form {
            id = "editor"
            action = previewLink
            method = FormMethod.post
            field("Name") {
                textInput(name = NAME) {
                    value = culture.name
                }
            }
            selectEnum("Calendar", CALENDAR, state.getCalendarStorage().getAll()) { c ->
                label = c.name
                value = c.id.value.toString()
                selected = culture.calendar == c.id
            }
            selectRarityMap("Languages", LANGUAGES, state.getLanguageStorage(), culture.languages) { it.name }
            editHolidays(state, culture)
            editNamingConvention(namingConvention, state)
            editAppearanceOptions(culture)
            editClothingOptions(state, culture)
            p {
                submitInput {
                    value = "Update"
                    formAction = updateLink
                    formMethod = InputFormMethod.post
                }
            }
        }
        back(backLink)
    }
}

private fun FORM.editHolidays(
    state: State,
    culture: Culture,
) {
    showDetails("Holidays") {
        state.getHolidayStorage().getAll().forEach { holiday ->
            p {
                checkBoxInput {
                    name = HOLIDAY
                    value = holiday.id.value.toString()
                    checked = culture.holidays.contains(holiday.id)
                    +holiday.name
                }
            }
        }
    }
}

private fun FORM.editNamingConvention(
    namingConvention: NamingConvention,
    state: State,
) {
    h2 { +"Naming Convention" }
    selectEnum("Type", NAMING_CONVENTION, NamingConventionType.entries, true) { type ->
        label = type.toString()
        value = type.toString()
        selected = when (type) {
            NamingConventionType.None -> namingConvention is NoNamingConvention
            NamingConventionType.Mononym -> namingConvention is MononymConvention
            NamingConventionType.Family -> namingConvention is FamilyConvention
            NamingConventionType.Patronym -> namingConvention is PatronymConvention
            NamingConventionType.Matronym -> namingConvention is MatronymConvention
            NamingConventionType.Genonym -> namingConvention is GenonymConvention
        }
    }
    when (namingConvention) {
        is FamilyConvention -> {
            selectEnum("Name Order", NAME_ORDER, NameOrder.entries, true) { o ->
                label = o.name
                value = o.toString()
                selected = namingConvention.nameOrder == o
            }
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
    selectEnum("Lookup Distance", LOOKUP_DISTANCE, GenonymicLookupDistance.entries) { distance ->
        label = distance.name
        value = distance.toString()
        selected = lookupDistance == distance
    }
    selectEnum("Genonymic Style", GENONYMIC_STYLE, GenonymicStyleType.entries, true) { type ->
        label = type.name
        value = type.toString()
        selected = when (type) {
            GenonymicStyleType.NamesOnly -> style is NamesOnlyStyle
            GenonymicStyleType.Prefix -> style is PrefixStyle
            GenonymicStyleType.Suffix -> style is SuffixStyle
            GenonymicStyleType.ChildOf -> style is ChildOfStyle
        }
    }
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
    selectRarityMap("Beard Styles", BEARD_STYLE, culture.appearanceStyle.beardStyles)
    selectRarityMap("Goatee Styles", GOATEE_STYLE, culture.appearanceStyle.goateeStyles)
    selectRarityMap("Moustache Styles", MOUSTACHE_STYLE, culture.appearanceStyle.moustacheStyles)
    selectRarityMap("Hair Styles", HAIR_STYLE, culture.appearanceStyle.hairStyles)
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

