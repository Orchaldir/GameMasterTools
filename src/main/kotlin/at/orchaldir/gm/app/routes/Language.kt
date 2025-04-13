package at.orchaldir.gm.app.routes

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.selectCreator
import at.orchaldir.gm.app.html.model.selectDate
import at.orchaldir.gm.app.html.model.showCreator
import at.orchaldir.gm.app.parse.parseLanguage
import at.orchaldir.gm.core.action.CreateLanguage
import at.orchaldir.gm.core.action.DeleteLanguage
import at.orchaldir.gm.core.action.UpdateLanguage
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.language.*
import at.orchaldir.gm.core.selector.*
import at.orchaldir.gm.core.selector.culture.countCultures
import at.orchaldir.gm.core.selector.culture.getCultures
import at.orchaldir.gm.core.selector.item.countTexts
import at.orchaldir.gm.core.selector.item.getTexts
import at.orchaldir.gm.core.selector.magic.countSpells
import at.orchaldir.gm.core.selector.magic.getSpells
import at.orchaldir.gm.core.selector.util.sortPlanes
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

@Resource("/$LANGUAGE_TYPE")
class LanguageRoutes {
    @Resource("details")
    class Details(val id: LanguageId, val parent: LanguageRoutes = LanguageRoutes())

    @Resource("new")
    class New(val parent: LanguageRoutes = LanguageRoutes())

    @Resource("delete")
    class Delete(val id: LanguageId, val parent: LanguageRoutes = LanguageRoutes())

    @Resource("edit")
    class Edit(val id: LanguageId, val parent: LanguageRoutes = LanguageRoutes())

    @Resource("preview")
    class Preview(val id: LanguageId, val parent: LanguageRoutes = LanguageRoutes())

    @Resource("update")
    class Update(val id: LanguageId, val parent: LanguageRoutes = LanguageRoutes())
}

fun Application.configureLanguageRouting() {
    routing {
        get<LanguageRoutes> {
            logger.info { "Get all languages" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllLanguages(call, STORE.getState())
            }
        }
        get<LanguageRoutes.Details> { details ->
            logger.info { "Get details of language ${details.id.value}" }

            val state = STORE.getState()
            val language = state.getLanguageStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showLanguageDetails(call, state, language)
            }
        }
        get<LanguageRoutes.New> {
            logger.info { "Add new language" }

            STORE.dispatch(CreateLanguage)

            call.respondRedirect(
                call.application.href(
                    LanguageRoutes.Edit(
                        STORE.getState().getLanguageStorage().lastId
                    )
                )
            )

            STORE.getState().save()
        }
        get<LanguageRoutes.Delete> { delete ->
            logger.info { "Delete language ${delete.id.value}" }

            STORE.dispatch(DeleteLanguage(delete.id))

            call.respondRedirect(call.application.href(LanguageRoutes()))

            STORE.getState().save()
        }
        get<LanguageRoutes.Edit> { edit ->
            logger.info { "Get editor for language ${edit.id.value}" }

            val state = STORE.getState()
            val language = state.getLanguageStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showLanguageEditor(call, state, language)
            }
        }
        post<LanguageRoutes.Preview> { preview ->
            logger.info { "Preview changes to language ${preview.id.value}" }

            val state = STORE.getState()
            val language = parseLanguage(call.receiveParameters(), state, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showLanguageEditor(call, state, language)
            }
        }
        post<LanguageRoutes.Update> { update ->
            logger.info { "Update language ${update.id.value}" }

            val language = parseLanguage(call.receiveParameters(), STORE.getState(), update.id)

            STORE.dispatch(UpdateLanguage(language))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllLanguages(
    call: ApplicationCall,
    state: State,
) {
    val languages = STORE.getState().getLanguageStorage().getAll().sortedBy { it.name }
    val createLink = call.application.href(LanguageRoutes.New())

    simpleHtml("Languages") {
        field("Count", languages.size)

        table {
            tr {
                th { +"Name" }
                th { +"Origin" }
                th { +"Characters" }
                th { +"Cultures" }
                th { +"Languages" }
                th { +"Spells" }
                th { +"Texts" }
            }
            languages.forEach { language ->
                tr {
                    td { link(call, state, language) }
                    td { displayOrigin(call, state, language) }
                    tdSkipZero(state.countCharacters(language.id))
                    tdSkipZero(state.countCultures(language.id))
                    tdSkipZero(state.countChildren(language.id))
                    tdSkipZero(state.countSpells(language.id))
                    tdSkipZero(state.countTexts(language.id))
                }
            }
        }

        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showLanguageDetails(
    call: ApplicationCall,
    state: State,
    language: Language,
) {
    val backLink = call.application.href(LanguageRoutes())
    val deleteLink = call.application.href(LanguageRoutes.Delete(language.id))
    val editLink = call.application.href(LanguageRoutes.Edit(language.id))
    val children = state.getChildren(language.id)
    val texts = state.getTexts(language.id)
    val characters = state.getCharacters(language.id)
    val cultures = state.getCultures(language.id)
    val spells = state.getSpells(language.id)

    simpleHtml("Language: ${language.name}") {
        field("Name", language.name)
        showOrigin(call, state, language)
        showList("Child Languages", children) { language ->
            link(call, language)
        }
        h2 { +"Usage" }
        showList("Characters", characters) { character ->
            link(call, state, character)
        }
        showList("Cultures", cultures) { culture ->
            link(call, culture)
        }
        showList("Spells", spells) { spell ->
            link(call, state, spell)
        }
        showList("Texts", texts) { texts ->
            link(call, state, texts)
        }
        action(editLink, "Edit")
        if (state.canDelete(language.id)) {
            action(deleteLink, "Delete")
        }
        back(backLink)
    }
}

private fun HtmlBlockTag.showOrigin(
    call: ApplicationCall,
    state: State,
    language: Language,
) {
    field("Origin") {
        displayOrigin(call, state, language)
    }
}

private fun HtmlBlockTag.displayOrigin(
    call: ApplicationCall,
    state: State,
    language: Language,
) {
    when (val origin = language.origin) {
        is CombinedLanguage -> {
            +"Combines "

            showInlineList(origin.parents) { parent ->
                link(call, state, parent)
            }
        }

        is EvolvedLanguage -> {
            +"Evolved from "
            link(call, state, origin.parent)
        }

        is InventedLanguage -> {
            +"Invented by "
            showCreator(call, state, origin.inventor)
        }

        OriginalLanguage -> +"Original"
        is PlanarLanguage -> {
            +"Part of "
            link(call, state, origin.plane)
        }
    }
}

private fun HTML.showLanguageEditor(
    call: ApplicationCall,
    state: State,
    language: Language,
) {
    val backLink = href(call, language.id)
    val previewLink = call.application.href(LanguageRoutes.Preview(language.id))
    val updateLink = call.application.href(LanguageRoutes.Update(language.id))

    simpleHtml("Edit Language: ${language.name}") {
        formWithPreview(previewLink, updateLink, backLink) {
            editLanguage(state, language)
        }
    }
}

private fun FORM.editLanguage(
    state: State,
    language: Language,
) {
    val possibleInventors = state.getCharacterStorage().getAll()
    val possibleParents = state.getPossibleParents(language.id)
        .sortedBy { it.name }
    val planes = state.sortPlanes()
    selectName(language.name)
    selectValue("Origin", ORIGIN, LanguageOriginType.entries, language.origin.getType(), true) {
        when (it) {
            LanguageOriginType.Combined -> possibleParents.size < 2
            LanguageOriginType.Evolved -> possibleParents.isEmpty()
            LanguageOriginType.Invented -> possibleInventors.isEmpty()
            else -> false
        }
    }
    when (val origin = language.origin) {
        is CombinedLanguage -> {
            selectElements(state, LANGUAGES, possibleParents, origin.parents)
        }

        is EvolvedLanguage -> selectElement(state, "Parent", LANGUAGES, possibleParents, origin.parent)

        is InventedLanguage -> {
            selectCreator(state, origin.inventor, language.id, origin.date, "Inventor")
            selectDate(state, "Date", origin.date, DATE)
        }

        is PlanarLanguage -> selectElement(state, "Plane", PLANE, planes, origin.plane)

        else -> doNothing()
    }
}