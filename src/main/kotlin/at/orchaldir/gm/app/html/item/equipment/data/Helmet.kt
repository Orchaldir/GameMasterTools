package at.orchaldir.gm.app.html.item.equipment.data

import at.orchaldir.gm.app.BODY_SHAPE
import at.orchaldir.gm.app.HELMET
import at.orchaldir.gm.app.SHAPE
import at.orchaldir.gm.app.STYLE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.item.equipment.style.*
import at.orchaldir.gm.app.html.rpg.combat.parseArmorStats
import at.orchaldir.gm.app.html.util.part.editItemPart
import at.orchaldir.gm.app.html.util.part.parseItemPart
import at.orchaldir.gm.app.html.util.part.parseMadeFromMetal
import at.orchaldir.gm.app.html.util.part.showItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.CHAIN_MAIL_MATERIALS
import at.orchaldir.gm.core.model.item.equipment.HELMET_MATERIALS
import at.orchaldir.gm.core.model.item.equipment.Helmet
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.part.ItemPartType
import at.orchaldir.gm.core.model.util.part.SOLID_MATERIALS
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
            showItemPart(call, state, style.main)
        }

        is GreatHelm -> {
            field("Helmet Shape", style.shape)
            field("Eye Holes", style.eyeHole)
            showItemPart(call, state, style.main)
        }

        is SkullCap -> {
            field("Helmet Shape", style.shape)
            showItemPart(call, state, style.main)
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
            editItemPart(state, style.main, HELMET, allowedTypes = CHAIN_MAIL_MATERIALS)
        }

        is GreatHelm -> {
            selectValue("Helmet Shape", SHAPE, HelmetShape.entries, style.shape)
            editItemPart(state, style.main, HELMET, allowedTypes = HELMET_MATERIALS)
            selectEyeHoles(style.eyeHole, HELMET)
        }

        is SkullCap -> {
            selectValue("Helmet Shape", SHAPE, HelmetShape.entries, style.shape)
            editItemPart(state, style.main, HELMET, allowedTypes = HELMET_MATERIALS)
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
        parseItemPart(parameters, HELMET, CHAIN_MAIL_MATERIALS),
    )

    HelmetStyleType.GreatHelm -> GreatHelm(
        parse(parameters, SHAPE, HelmetShape.Round),
        parseEyeHoles(parameters, HELMET),
        parseItemPart(parameters, HELMET, HELMET_MATERIALS),
    )

    HelmetStyleType.SkullCap -> SkullCap(
        parse(parameters, SHAPE, HelmetShape.Round),
        parseHelmetFront(parameters),
        parseItemPart(parameters, HELMET, HELMET_MATERIALS),
    )
}