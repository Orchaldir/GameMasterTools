package at.orchaldir.gm.app.html.model.item.text

import at.orchaldir.gm.app.ALIGNMENT
import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.PAGE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.model.font.editFontOption
import at.orchaldir.gm.app.html.model.font.parseFontOption
import at.orchaldir.gm.app.html.model.font.showFontOption
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.content.*
import at.orchaldir.gm.core.model.util.HorizontalAlignment
import at.orchaldir.gm.core.model.util.HorizontalAlignment.Center
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag


// show

fun HtmlBlockTag.showPageNumbering(
    call: ApplicationCall,
    state: State,
    data: PageNumbering,
) {
    showDetails("Page Numbering") {
        field("Type", data.getType())

        when (data) {
            NoPageNumbering -> doNothing()
            is PageNumberingReusingFont -> field("Horizontal Alignment", data.horizontalAlignment)
            is SimplePageNumbering -> {
                showFontOption(call, state, "Font", data.fontOption)
                field("Horizontal Alignment", data.horizontalAlignment)
            }
        }
    }
}

// edit

fun HtmlBlockTag.editPageNumbering(
    state: State,
    data: PageNumbering,
) {
    val param = combine(PAGE, NUMBER)

    showDetails("Page Numbering", true) {
        selectValue("Type", param, PageNumberingType.entries, data.getType(), true)

        when (data) {
            NoPageNumbering -> doNothing()
            is PageNumberingReusingFont -> selectHorizontalAlignment(param, data.horizontalAlignment)
            is SimplePageNumbering -> {
                editFontOption(state, "Font", data.fontOption, param)
                selectHorizontalAlignment(param, data.horizontalAlignment)
            }
        }
    }
}

private fun DETAILS.selectHorizontalAlignment(
    param: String,
    alignment: HorizontalAlignment,
) {
    selectValue(
        "Horizontal Alignment",
        combine(param, ALIGNMENT),
        HorizontalAlignment.entries,
        alignment,
        true,
    )
}

// parse

fun parsePageNumbering(parameters: Parameters): PageNumbering {
    val param = combine(PAGE, NUMBER)

    return when (parse(parameters, param, PageNumberingType.None)) {
        PageNumberingType.None -> NoPageNumbering
        PageNumberingType.ReusingFont -> PageNumberingReusingFont(
            parse(parameters, combine(param, ALIGNMENT), Center),
        )

        PageNumberingType.Simple -> SimplePageNumbering(
            parseFontOption(parameters, param),
            parse(parameters, combine(param, ALIGNMENT), Center),
        )
    }
}
