package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.HELMET
import at.orchaldir.gm.app.SHAPE
import at.orchaldir.gm.app.STYLE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.optionalField
import at.orchaldir.gm.app.html.selectOptionalValue
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.util.part.editColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.parseColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.showColorSchemeItemPart
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Helmet
import at.orchaldir.gm.core.model.item.equipment.style.*
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showHelmet(
    call: ApplicationCall,
    state: State,
    helmet: Helmet,
) {
    field("Style", helmet.style.getType())

    when (val style = helmet.style) {
        is ChainmailHood -> {
            optionalField("Body Shape", style.shape)
            showColorSchemeItemPart(call, state, style.part, "Chainmail")
        }

        is SkullCap -> {
            field("Helmet Shape", style.shape)
            showColorSchemeItemPart(call, state, style.part, "Helmet")
        }
    }
}

// edit

fun FORM.editHelmet(
    state: State,
    helmet: Helmet,
) {
    selectValue("Style", STYLE, HelmetStyleType.entries, helmet.style.getType())

    when (val style = helmet.style) {
        is ChainmailHood -> {
            selectOptionalValue("Body Shape", SHAPE, style.shape, HoodBodyShape.entries)
            editColorSchemeItemPart(state, style.part, HELMET, "Chainmail")
        }

        is SkullCap -> {
            selectValue("Helmet Shape", SHAPE, HelmetShape.entries, style.shape)
            editColorSchemeItemPart(state, style.part, HELMET, "Helmet")
        }
    }
}

// parse

fun parseHelmet(parameters: Parameters) = Helmet(
    parseHelmetStyle(parameters),
)

fun parseHelmetStyle(
    parameters: Parameters,
) = when (parse(parameters, STYLE, HelmetStyleType.SkullCap)) {
    HelmetStyleType.ChainmailHood -> ChainmailHood(
        parse<HoodBodyShape>(parameters, SHAPE),
        parseColorSchemeItemPart(parameters, HELMET),
    )

    HelmetStyleType.SkullCap -> SkullCap(
        parse(parameters, SHAPE, HelmetShape.Round),
        NoHelmetFront,
        parseColorSchemeItemPart(parameters, HELMET),
    )
}