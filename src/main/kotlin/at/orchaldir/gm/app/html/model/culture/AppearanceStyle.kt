package at.orchaldir.gm.app.html.model.culture

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.selectRarityMap
import at.orchaldir.gm.app.html.showRarityMap
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parseOneOf
import at.orchaldir.gm.app.parse.parseOneOrNone
import at.orchaldir.gm.core.model.character.appearance.beard.BeardStyleType
import at.orchaldir.gm.core.model.character.appearance.beard.GoateeStyle
import at.orchaldir.gm.core.model.character.appearance.beard.MoustacheStyle
import at.orchaldir.gm.core.model.character.appearance.hair.*
import at.orchaldir.gm.core.model.culture.fashion.AppearanceStyle
import at.orchaldir.gm.core.model.util.Color
import io.ktor.http.*
import kotlinx.html.*

// show

fun HtmlBlockTag.showAppearanceStyle(style: AppearanceStyle) {
    h2 { +"Appearance" }

    showRarityMap("Lip Colors", style.lipColors)

    h3 { +"Beard" }

    showRarityMap("Beard Styles", style.beardStyles)

    if (style.hasGoatee()) {
        showRarityMap("Goatee Styles", style.goateeStyles)
    }

    if (style.hasMoustache()) {
        showRarityMap("Moustache Styles", style.moustacheStyles)
    }

    h3 { +"Hair" }

    showRarityMap("Hair Styles", style.hairStyles)

    if (style.hairStyles.contains(HairStyle.Bun)) {
        showRarityMap("Bun Styles", style.bunStyles)
    }

    if (style.hairStyles.contains(HairStyle.Long)) {
        showRarityMap("Long Hair Styles", style.longHairStyles)
    }

    if (style.hairStyles.contains(HairStyle.Ponytail)) {
        showRarityMap("Ponytail Styles", style.ponytailStyles)
        showRarityMap("Ponytail Positions", style.ponytailPositions)
    }

    if (style.hairStyles.contains(HairStyle.Short)) {
        showRarityMap("Short Hair Styles", style.shortHairStyles)
    }

    if (style.hasLongHair()) {
        showRarityMap("Hair Lengths", style.hairLengths)
    }
}

// edit

fun HtmlBlockTag.editAppearanceOptions(style: AppearanceStyle) {
    h2 { +"Appearance" }

    selectRarityMap("Lip Colors", LIP_COLORS, style.lipColors)

    h3 { +"Beard" }

    selectRarityMap("Beard Styles", combine(BEARD, STYLE), style.beardStyles)

    if (style.hasGoatee()) {
        selectRarityMap("Goatee Styles", GOATEE_STYLE, style.goateeStyles)
    }

    if (style.hasMoustache()) {
        selectRarityMap("Moustache Styles", MOUSTACHE_STYLE, style.moustacheStyles)
    }

    h3 { +"Hair" }

    selectRarityMap("Hair Styles", combine(HAIR, STYLE), style.hairStyles)

    if (style.hairStyles.contains(HairStyle.Bun)) {
        selectRarityMap("Bun Styles", combine(BUN, STYLE), style.bunStyles)
    }

    if (style.hairStyles.contains(HairStyle.Long)) {
        selectRarityMap("Long Hair Styles", combine(LONG, HAIR, STYLE), style.longHairStyles)
    }

    if (style.hairStyles.contains(HairStyle.Ponytail)) {
        selectRarityMap("Ponytail Styles", combine(PONYTAIL, STYLE), style.ponytailStyles)
        selectRarityMap("Ponytail Positions", combine(PONYTAIL, POSITION), style.ponytailPositions)
    }

    if (style.hairStyles.contains(HairStyle.Short)) {
        selectRarityMap("Short Hair Styles", combine(SHORT, HAIR, STYLE), style.shortHairStyles)
    }

    if (style.hasLongHair()) {
        selectRarityMap("Hair Lengths", combine(HAIR, LENGTH), style.hairLengths)
    }
}


// parse

fun parseAppearanceStyle(parameters: Parameters) = AppearanceStyle(
    parseOneOf(parameters, combine(BEARD, STYLE), BeardStyleType::valueOf),
    parseOneOrNone(parameters, GOATEE_STYLE, GoateeStyle::valueOf),
    parseOneOrNone(parameters, MOUSTACHE_STYLE, MoustacheStyle::valueOf),
    parseOneOf(parameters, combine(HAIR, STYLE), HairStyle::valueOf),
    parseOneOrNone(parameters, combine(BUN, STYLE), BunStyle::valueOf),
    parseOneOrNone(parameters, combine(LONG, HAIR, STYLE), LongHairStyle::valueOf),
    parseOneOrNone(parameters, combine(PONYTAIL, STYLE), PonytailStyle::valueOf),
    parseOneOrNone(parameters, combine(PONYTAIL, POSITION), PonytailPosition::valueOf),
    parseOneOrNone(parameters, combine(SHORT, HAIR, STYLE), ShortHairStyle::valueOf),
    parseOneOrNone(parameters, combine(HAIR, LENGTH), HairLength::valueOf),
    parseOneOf(parameters, LIP_COLORS, Color::valueOf),
)