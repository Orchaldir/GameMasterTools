package at.orchaldir.gm.app.html.util.color

import at.orchaldir.gm.app.COLOR
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.util.render.*
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showColorLookup(
    label: String,
    lookup: ColorLookup,
) {
    field(label) {
        when (lookup) {
            is FixedColor -> showColor(lookup.color)
            LookupMaterial -> +"Color of Material"
            LookupSchema0 -> +"1.Color of Schema"
            LookupSchema1 -> +"2.Color of Schema"
        }
    }
}

// edit


fun HtmlBlockTag.editColorLookup(
    label: String,
    lookup: ColorLookup,
    param: String,
) {
    showDetails(label, true) {
        selectValue(
            "Type",
            combine(param, TYPE),
            ColorLookupType.entries,
            lookup.type(),
        )

        when (lookup) {
            is FixedColor -> selectColor(
                lookup.color,
                combine(param, COLOR),
                "Color",
                Color.entries,
            )

            LookupMaterial -> doNothing()
            LookupSchema0 -> doNothing()
            LookupSchema1 -> doNothing()
        }
    }
}

// parse

fun parseColorLookup(
    parameters: Parameters,
    param: String,
) = when (parse(parameters, combine(param, TYPE), ColorLookupType.Schema0)) {
    ColorLookupType.Fixed -> FixedColor(
        parse(parameters, combine(param, COLOR), Color.Pink),
    )

    ColorLookupType.Material -> LookupMaterial
    ColorLookupType.Schema0 -> LookupSchema0
    ColorLookupType.Schema1 -> LookupSchema1
}
