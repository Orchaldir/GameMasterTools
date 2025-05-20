package at.orchaldir.gm.app.html.model.economy.money

import at.orchaldir.gm.app.FONT
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.util.font.parseOptionalFontId
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.*
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showCoinSide(
    call: ApplicationCall,
    state: State,
    side: CoinSide,
    label: String,
) {
    showDetails(label, true) {
        showCoinSide(call, state, side)
    }
}

fun HtmlBlockTag.showCoinSide(
    call: ApplicationCall,
    state: State,
    side: CoinSide,
) {
    field("Type", side.getType())

    val font = when (side) {
        BlankCoinSide -> return
        is ShowDenomination -> side.font
        is ShowName -> side.font
        is ShowNumber -> side.font
        is ShowValue -> side.font
    }

    optionalFieldLink("Font", call, state, font)
}

// edit

fun HtmlBlockTag.editCoinSide(
    state: State,
    side: CoinSide,
    label: String,
    param: String,
) {
    showDetails(label, true) {
        editCoinSide(state, side, param)
    }
}

fun HtmlBlockTag.editCoinSide(
    state: State,
    side: CoinSide,
    param: String,
) {
    selectValue(
        "Type",
        combine(param, TYPE),
        CoinSideType.entries,
        side.getType(),
    )

    val font = when (side) {
        BlankCoinSide -> return
        is ShowDenomination -> side.font
        is ShowName -> side.font
        is ShowNumber -> side.font
        is ShowValue -> side.font
    }

    selectOptionalElement(
        state,
        "Font",
        combine(param, FONT),
        state.getFontStorage().getAll(),
        font,
    )
}

// parse

fun parseCoinSide(parameters: Parameters, param: String): CoinSide {
    val font = parseOptionalFontId(parameters, combine(param, FONT))

    return when (parse(parameters, combine(param, TYPE), CoinSideType.Blank)) {
        CoinSideType.Blank -> BlankCoinSide
        CoinSideType.Denomination -> ShowDenomination(font)
        CoinSideType.Name -> ShowName(font)
        CoinSideType.Number -> ShowNumber(font)
        CoinSideType.Value -> ShowValue(font)
    }
}
