package at.orchaldir.gm.app.html.culture

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.parseOneOf
import at.orchaldir.gm.app.html.parseOneOrNone
import at.orchaldir.gm.app.html.selectRarityMap
import at.orchaldir.gm.app.html.showRarityMap
import at.orchaldir.gm.app.html.combine
import at.orchaldir.gm.core.model.character.appearance.beard.BeardStyleType
import at.orchaldir.gm.core.model.character.appearance.beard.FullBeardStyle
import at.orchaldir.gm.core.model.character.appearance.beard.GoateeStyle
import at.orchaldir.gm.core.model.character.appearance.beard.MoustacheStyle
import at.orchaldir.gm.core.model.character.appearance.hair.*
import at.orchaldir.gm.core.model.culture.fashion.AppearanceFashion
import at.orchaldir.gm.core.model.culture.fashion.BeardFashion
import at.orchaldir.gm.core.model.culture.fashion.HairFashion
import at.orchaldir.gm.core.model.util.render.Color
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2
import kotlinx.html.h3

// show

fun HtmlBlockTag.showAppearanceFashion(style: AppearanceFashion) {
    h2 { +"Appearance" }

    showRarityMap("Lip Colors", style.lipColors)

    showBeard(style.beard)
    showHair(style.hair)
}

private fun HtmlBlockTag.showBeard(fashion: BeardFashion) {
    h3 { +"Beard" }

    showRarityMap("Beard Styles", fashion.beardStyles)

    if (fashion.beardStyles.contains(BeardStyleType.Full)) {
        showRarityMap("Beard Length", fashion.beardLength)
        showRarityMap("Full Beard Styles", fashion.fullBeardStyles)
    }

    if (fashion.hasGoatee()) {
        showRarityMap("Goatee Styles", fashion.goateeStyles)
    }

    if (fashion.hasMoustache()) {
        showRarityMap("Moustache Styles", fashion.moustacheStyles)
    }
}

private fun HtmlBlockTag.showHair(fashion: HairFashion) {
    h3 { +"Hair" }

    showRarityMap("Hair Styles", fashion.hairStyles)

    if (fashion.hairStyles.contains(HairStyle.Bun)) {
        showRarityMap("Bun Styles", fashion.bunStyles)
    }

    if (fashion.hairStyles.contains(HairStyle.Long)) {
        showRarityMap("Long Hair Styles", fashion.longHairStyles)
    }

    if (fashion.hairStyles.contains(HairStyle.Ponytail)) {
        showRarityMap("Ponytail Styles", fashion.ponytailStyles)
        showRarityMap("Ponytail Positions", fashion.ponytailPositions)
    }

    if (fashion.hairStyles.contains(HairStyle.Short)) {
        showRarityMap("Short Hair Styles", fashion.shortHairStyles)
    }

    if (fashion.hasLongHair()) {
        showRarityMap("Hair Lengths", fashion.hairLengths)
    }
}

// edit

fun HtmlBlockTag.editAppearanceFashion(style: AppearanceFashion) {
    h2 { +"Appearance" }

    selectRarityMap("Lip Colors", LIP_COLORS, style.lipColors)

    editBeard(style.beard)
    editHair(style.hair)
}

private fun HtmlBlockTag.editBeard(fashion: BeardFashion) {
    h3 { +"Beard" }

    selectRarityMap("Beard Styles", combine(BEARD, STYLE), fashion.beardStyles)

    if (fashion.beardStyles.contains(BeardStyleType.Full)) {
        selectRarityMap("Beard Length", combine(BEARD, LENGTH), fashion.beardLength)
        selectRarityMap("Full Beard Styles", combine(FULL, STYLE), fashion.fullBeardStyles)
    }

    if (fashion.hasGoatee()) {
        selectRarityMap("Goatee Styles", combine(GOATEE, STYLE), fashion.goateeStyles)
    }

    if (fashion.hasMoustache()) {
        selectRarityMap("Moustache Styles", combine(MOUSTACHE, STYLE), fashion.moustacheStyles)
    }
}

private fun HtmlBlockTag.editHair(fashion: HairFashion) {
    h3 { +"Hair" }

    selectRarityMap("Hair Styles", combine(HAIR, STYLE), fashion.hairStyles)

    if (fashion.hairStyles.contains(HairStyle.Bun)) {
        selectRarityMap("Bun Styles", combine(BUN, STYLE), fashion.bunStyles)
    }

    if (fashion.hairStyles.contains(HairStyle.Long)) {
        selectRarityMap("Long Hair Styles", combine(LONG, HAIR, STYLE), fashion.longHairStyles)
    }

    if (fashion.hairStyles.contains(HairStyle.Ponytail)) {
        selectRarityMap("Ponytail Styles", combine(PONYTAIL, STYLE), fashion.ponytailStyles)
        selectRarityMap("Ponytail Positions", combine(PONYTAIL, POSITION), fashion.ponytailPositions)
    }

    if (fashion.hairStyles.contains(HairStyle.Short)) {
        selectRarityMap("Short Hair Styles", combine(SHORT, HAIR, STYLE), fashion.shortHairStyles)
    }

    if (fashion.hasLongHair()) {
        selectRarityMap("Hair Lengths", combine(HAIR, LENGTH), fashion.hairLengths)
    }
}


// parse

fun parseAppearanceFashion(parameters: Parameters) = AppearanceFashion(
    parseBeardFashion(parameters),
    parseHairFashion(parameters),
    parseOneOf(parameters, LIP_COLORS, Color::valueOf),
)

fun parseBeardFashion(parameters: Parameters) = BeardFashion(
    parseOneOf(parameters, combine(BEARD, STYLE), BeardStyleType::valueOf),
    parseOneOrNone(parameters, combine(BEARD, LENGTH), HairLength::valueOf),
    parseOneOrNone(parameters, combine(FULL, STYLE), FullBeardStyle::valueOf),
    parseOneOrNone(parameters, combine(GOATEE, STYLE), GoateeStyle::valueOf),
    parseOneOrNone(parameters, combine(MOUSTACHE, STYLE), MoustacheStyle::valueOf),
)

fun parseHairFashion(parameters: Parameters) = HairFashion(
    parseOneOf(parameters, combine(HAIR, STYLE), HairStyle::valueOf),
    parseOneOrNone(parameters, combine(BUN, STYLE), BunStyle::valueOf),
    parseOneOrNone(parameters, combine(LONG, HAIR, STYLE), LongHairStyle::valueOf),
    parseOneOrNone(parameters, combine(PONYTAIL, STYLE), PonytailStyle::valueOf),
    parseOneOrNone(parameters, combine(PONYTAIL, POSITION), PonytailPosition::valueOf),
    parseOneOrNone(parameters, combine(SHORT, HAIR, STYLE), ShortHairStyle::valueOf),
    parseOneOrNone(parameters, combine(HAIR, LENGTH), HairLength::valueOf),
)