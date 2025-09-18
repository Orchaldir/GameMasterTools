package at.orchaldir.gm.app.html.util.color

import at.orchaldir.gm.app.COLOR
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.render.*
import at.orchaldir.gm.core.selector.item.getEquipment
import at.orchaldir.gm.core.selector.util.sortEquipmentList
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showColorScheme(
    call: ApplicationCall,
    state: State,
    scheme: ColorScheme,
) {
    field("Name", scheme.data.name())

    when (scheme.data) {
        is OneColor -> fieldColor(scheme.data.color)
        is TwoColors -> {
            fieldColor(scheme.data.color0, "1.Color")
            fieldColor(scheme.data.color1, "2.Color")
        }

        UndefinedColors -> doNothing()
    }

    fieldElements(call, state, state.sortEquipmentList(state.getEquipment(scheme.id)))
}

// edit

fun FORM.editColorScheme(
    state: State,
    scheme: ColorScheme,
) {
    field("Colors", scheme.data.name())

    editColors(scheme.data)
}

private fun FORM.editColors(colors: Colors) {
    selectValue(
        "Type",
        TYPE,
        ColorsType.entries,
        colors.type(),
    )

    when (colors) {
        is OneColor -> selectColor("Color", 0, Color.entries, colors.color)
        is TwoColors -> {
            selectColor("1.Color", 0, Color.entries - colors.color1, colors.color0)
            selectColor("2.Color", 1, Color.entries - colors.color0, colors.color1)
        }

        UndefinedColors -> doNothing()
    }
}

private fun FORM.selectColor(
    label: String,
    index: Int,
    colors: Collection<Color>,
    color: Color,
) {
    selectColor(color, combine(COLOR, index), label, colors)
}

// parse

fun parseColorSchemeId(value: String) = ColorSchemeId(value.toInt())
fun parseColorSchemeId(parameters: Parameters, param: String) = ColorSchemeId(parseInt(parameters, param))
fun parseOptionalColorSchemeId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { ColorSchemeId(it) }

fun parseColorScheme(parameters: Parameters, state: State, id: ColorSchemeId) = ColorScheme(
    id,
    parseColors(parameters),
)

fun parseColors(parameters: Parameters) = when (parse(parameters, TYPE, ColorsType.Undefined)) {
    ColorsType.One -> OneColor(
        parseColors(parameters, 0, Color.Pink),
    )

    ColorsType.Two -> TwoColors.init(
        parseColors(parameters, 0, Color.Black),
        parseColors(parameters, 1, Color.White),
    )

    ColorsType.Undefined -> UndefinedColors
}

fun parseColors(parameters: Parameters, index: Int, default: Color) = parse(parameters, combine(COLOR, index), default)
