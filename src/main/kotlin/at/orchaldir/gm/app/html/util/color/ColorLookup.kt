package at.orchaldir.gm.app.html.util.color

import at.orchaldir.gm.app.COLOR
import at.orchaldir.gm.app.MAP
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.render.*
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.fieldColorLookup(
    label: String,
    lookup: ColorLookup,
) {
    field(label) {
        showColorLookup(lookup)
    }
}

fun HtmlBlockTag.showColorLookup(
    lookup: ColorLookup,
) = when (lookup) {
    is FixedColor -> showOptionalColor(lookup.color)
    is RandomColor -> showInlineList(lookup.colors.getValidValues(), 3, ::showColor)
    LookupMaterial -> +"Color of Material"
    LookupSchema0 -> +"1.Color of Schema"
    LookupSchema1 -> +"2.Color of Schema"
}

// edit


fun HtmlBlockTag.editColorLookup(
    state: State,
    label: String,
    lookup: ColorLookup,
    param: String,
    colors: Collection<Color> = Color.entries,
) {
    showDetails(label, true) {
        selectValue(
            "Type",
            combine(param, TYPE),
            ColorLookupType.entries,
            lookup.type(),
        ) { type ->
            when (type) {
                ColorLookupType.Fixed -> false
                ColorLookupType.Random -> false
                ColorLookupType.Material -> false
                ColorLookupType.Schema0 -> state.getColorSchemeStorage().isEmpty()
                ColorLookupType.Schema1 -> state.getColorSchemeStorage().isEmpty()
            }
        }

        when (lookup) {
            is FixedColor -> selectColor(
                lookup.color,
                combine(param, COLOR),
                "Color",
                colors,
            )

            is RandomColor -> selectColorRarityMap(
                "Colors",
                combine(param, COLOR, MAP),
                lookup.colors,
                colors,
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
    default: Color = Color.Pink,
) = when (parse(parameters, combine(param, TYPE), ColorLookupType.Material)) {
    ColorLookupType.Fixed -> FixedColor(
        parse(parameters, combine(param, COLOR), default),
    )

    ColorLookupType.Random -> RandomColor(
        parseColorOneOf(
            parameters,
            combine(param, COLOR, MAP),
            setOf(default),
        )
    )

    ColorLookupType.Material -> LookupMaterial
    ColorLookupType.Schema0 -> LookupSchema0
    ColorLookupType.Schema1 -> LookupSchema1
}
