package at.orchaldir.gm.app.html.model.culture

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.selectRarityMap
import at.orchaldir.gm.app.html.showRarityMap
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parseOneOf
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
    showRarityMap("Bun Styles", style.bunStyles)
    showRarityMap("Long Hair Styles", style.longHairStyles)
    showRarityMap("Ponytail Styles", style.ponytailStyles)
    showRarityMap("Ponytail Positions", style.ponytailPositions)
    showRarityMap("Short Hair Styles", style.shortHairStyles)
    showRarityMap("Hair Lengths", style.hairLengths)
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
    selectRarityMap("Bun Styles", combine(BUN, STYLE), style.bunStyles)
    selectRarityMap("Long Hair Styles", combine(LONG, HAIR, STYLE), style.longHairStyles)
    selectRarityMap("Ponytail Styles", combine(PONYTAIL, STYLE), style.ponytailStyles)
    selectRarityMap("Ponytail Positions", combine(PONYTAIL, POSITION), style.ponytailPositions)
    selectRarityMap("Short Hair Styles", combine(SHORT, HAIR, STYLE), style.shortHairStyles)
    selectRarityMap("Hair Lengths", combine(HAIR, LENGTH), style.hairLengths)
}


// parse

fun parseAppearanceStyle(parameters: Parameters) = AppearanceStyle(
    parseOneOf(parameters, combine(BEARD, STYLE), BeardStyleType::valueOf),
    parseOneOf(parameters, GOATEE_STYLE, GoateeStyle::valueOf),
    parseOneOf(parameters, MOUSTACHE_STYLE, MoustacheStyle::valueOf),
    parseOneOf(parameters, combine(HAIR, STYLE), HairStyle::valueOf),
    parseOneOf(parameters, combine(BUN, STYLE), BunStyle::valueOf),
    parseOneOf(parameters, combine(LONG, HAIR, STYLE), LongHairStyle::valueOf),
    parseOneOf(parameters, combine(PONYTAIL, STYLE), PonytailStyle::valueOf),
    parseOneOf(parameters, combine(PONYTAIL, POSITION), PonytailPosition::valueOf),
    parseOneOf(parameters, combine(SHORT, HAIR, STYLE), ShortHairStyle::valueOf),
    parseOneOf(parameters, combine(HAIR, LENGTH), HairLength::valueOf),
    parseOneOf(parameters, LIP_COLORS, Color::valueOf),
)