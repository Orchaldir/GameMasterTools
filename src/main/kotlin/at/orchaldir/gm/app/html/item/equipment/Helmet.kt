package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.BODY_SHAPE
import at.orchaldir.gm.app.HELMET
import at.orchaldir.gm.app.SHAPE
import at.orchaldir.gm.app.STYLE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.item.equipment.style.*
import at.orchaldir.gm.app.html.optionalField
import at.orchaldir.gm.app.html.rpg.combat.parseArmorStats
import at.orchaldir.gm.app.html.selectOptionalValue
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.util.part.editColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.parseColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.showColorSchemeItemPart
import at.orchaldir.gm.app.html.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Helmet
import at.orchaldir.gm.core.model.item.equipment.style.*
import io.ktor.http.*
import io.ktor.server.application.*
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

        is GreatHelm -> {
            field("Helmet Shape", style.shape)
            field("Eye Holes", style.eyeHole)
            showColorSchemeItemPart(call, state, style.part, "Helmet")
        }

        is SkullCap -> {
            field("Helmet Shape", style.shape)
            showColorSchemeItemPart(call, state, style.part, "Helmet")
            showHelmetFront(call, state, style.front)
        }
    }
}

// edit

fun HtmlBlockTag.editHelmet(
    state: State,
    helmet: Helmet,
) {
    selectValue("Style", STYLE, HelmetStyleType.entries, helmet.style.getType())

    when (val style = helmet.style) {
        is ChainmailHood -> {
            selectOptionalValue("Body Shape", BODY_SHAPE, style.shape, HoodBodyShape.entries)
            editColorSchemeItemPart(state, style.part, HELMET, "Chainmail")
        }

        is GreatHelm -> {
            selectValue("Helmet Shape", SHAPE, HelmetShape.entries, style.shape)
            editColorSchemeItemPart(state, style.part, HELMET, "Helmet")
            selectEyeHoles(style.eyeHole, HELMET)
        }

        is SkullCap -> {
            selectValue("Helmet Shape", SHAPE, HelmetShape.entries, style.shape)
            editColorSchemeItemPart(state, style.part, HELMET, "Helmet")
            editHelmetFront(state, style.front)
        }
    }
}

// parse

fun parseHelmet(parameters: Parameters) = Helmet(
    parseHelmetStyle(parameters),
    parseArmorStats(parameters),
)

fun parseHelmetStyle(
    parameters: Parameters,
) = when (parse(parameters, STYLE, HelmetStyleType.SkullCap)) {
    HelmetStyleType.ChainmailHood -> ChainmailHood(
        parse<HoodBodyShape>(parameters, BODY_SHAPE),
        parseColorSchemeItemPart(parameters, HELMET),
    )

    HelmetStyleType.GreatHelm -> GreatHelm(
        parse(parameters, SHAPE, HelmetShape.Round),
        parseEyeHoles(parameters, HELMET),
        parseColorSchemeItemPart(parameters, HELMET),
    )

    HelmetStyleType.SkullCap -> SkullCap(
        parse(parameters, SHAPE, HelmetShape.Round),
        parseHelmetFront(parameters),
        parseColorSchemeItemPart(parameters, HELMET),
    )
}