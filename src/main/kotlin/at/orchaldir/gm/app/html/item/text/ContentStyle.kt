package at.orchaldir.gm.app.html.item.text

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.font.editFontOption
import at.orchaldir.gm.app.html.util.font.parseFontOption
import at.orchaldir.gm.app.html.util.font.showFontOption
import at.orchaldir.gm.app.html.util.math.fieldFactor
import at.orchaldir.gm.app.html.util.math.parseFactor
import at.orchaldir.gm.app.html.util.math.selectFactor
import at.orchaldir.gm.app.html.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.content.*
import at.orchaldir.gm.utils.math.Factor.Companion.fromPermille
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showContentStyle(
    call: ApplicationCall,
    state: State,
    style: ContentStyle,
) {
    showDetails("Style") {
        showFontOption(call, state, "Main Font", style.main)
        showFontOption(call, state, "Quote Font", style.quote)
        showFontOption(call, state, "Title Font", style.title)
        field("Is Justified?", style.isJustified)
        fieldFactor("Margin", style.margin)
        showInitials(call, state, style.initials)
        showContentGeneration(style.generation)
    }
}

fun HtmlBlockTag.showContentGeneration(
    generation: ContentGeneration,
) {
    showDetails("Generation") {
        showParagraphGeneration(generation.main, "Main")
        showParagraphGeneration(generation.quote, "Quote")
        showRarityMap("Rarity", generation.rarity)
    }
}

fun HtmlBlockTag.showParagraphGeneration(
    generation: ParagraphGeneration,
    noun: String,
) {
    showDetails(noun) {
        field("Min Length", generation.minLength)
        field("Max Length", generation.maxLength)
    }
}


// edit

fun HtmlBlockTag.editContentStyle(
    state: State,
    style: ContentStyle,
    param: String,
) {
    showDetails("Style", true) {
        editFontOption(state, "Main Font", style.main, combine(param, MAIN))
        editFontOption(state, "Quote Font", style.quote, combine(param, QUOTE))
        editFontOption(state, "Title Font", style.title, combine(param, TITLE))
        selectBool(
            "Is Justified?",
            style.isJustified,
            combine(param, ALIGNMENT),
        )
        selectFactor(
            "Margin",
            combine(param, SIDE),
            style.margin,
            MIN_MARGIN,
            MAX_MARGIN,
            fromPermille(1),
        )
        editInitials(state, style.initials, param)
        editContentGeneration(style.generation, param)
    }
}

fun HtmlBlockTag.editContentGeneration(
    generation: ContentGeneration,
    param: String,
) {
    showDetails("Generation", true) {
        editParagraphGeneration(generation.main, "Main", combine(param, MAIN))
        editParagraphGeneration(generation.quote, "Quote", combine(param, QUOTE))
        selectRarityMap("Rarity", combine(param, TYPE), generation.rarity)
    }
}

fun HtmlBlockTag.editParagraphGeneration(
    generation: ParagraphGeneration,
    noun: String,
    param: String,
) {
    showDetails(noun, true) {
        selectInt(
            "Min Length",
            generation.minLength,
            1,
            1000,
            1,
            combine(param, MIN),
        )
        selectInt(
            "Max Length",
            generation.maxLength,
            generation.minLength,
            1000,
            1,
            combine(param, MAX),
        )
    }
}

// parse

fun parseContentStyle(parameters: Parameters, param: String) = ContentStyle(
    parseFontOption(parameters, combine(param, MAIN), DEFAULT_MAIN_SIZE),
    parseFontOption(parameters, combine(param, QUOTE), DEFAULT_MAIN_SIZE),
    parseFontOption(parameters, combine(param, TITLE), DEFAULT_TITLE_SIZE),
    parseBool(parameters, combine(param, ALIGNMENT)),
    parseFactor(parameters, combine(param, SIDE), DEFAULT_MARGIN),
    parseInitials(parameters, param),
    parseContentGeneration(parameters, param),
)

fun parseContentGeneration(parameters: Parameters, param: String) = ContentGeneration(
    parseParagraphGeneration(
        parameters,
        combine(param, MAIN),
        MIN_PARAGRAPH_LENGTH,
        MAX_PARAGRAPH_LENGTH,
    ),
    parseParagraphGeneration(
        parameters,
        combine(param, QUOTE),
        MIN_QUOTE_LENGTH,
        MAX_QUOTE_LENGTH,
    ),
    parseOneOf(
        parameters,
        combine(param, TYPE),
        ContentEntryType::valueOf,
        listOf(ContentEntryType.Paragraph),
    ),
)

fun parseParagraphGeneration(parameters: Parameters, param: String, min: Int, max: Int) = ParagraphGeneration(
    parseInt(parameters, combine(param, MIN), min),
    parseInt(parameters, combine(param, MAX), max),
)
