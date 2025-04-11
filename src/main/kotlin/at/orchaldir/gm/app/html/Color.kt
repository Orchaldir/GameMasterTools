package at.orchaldir.gm.app.html

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.model.fieldFactor
import at.orchaldir.gm.app.html.model.parseFactor
import at.orchaldir.gm.app.html.model.selectPercentage
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseFloat
import at.orchaldir.gm.app.parse.parseUByte
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.style

// show

fun HtmlBlockTag.showColor(color: Color) {
    field("Solid Fill") {
        +color.name
    }
}

// edit

fun HtmlBlockTag.selectColor(
    color: Color,
    selectId: String,
    label: String = "Color",
    colors: Collection<Color> = Color.entries,
) {
    selectColor(label, selectId, OneOf(colors), color)
}

fun HtmlBlockTag.selectColor(
    labelText: String,
    selectId: String,
    rarityMap: OneOf<Color>,
    current: Color,
) {
    selectOneOf(labelText, selectId, rarityMap, current, true) { c ->
        label = c.name
        value = c.toString()
        style = "background-color:$c"
    }
}

fun HtmlBlockTag.selectColor(
    labelText: String,
    selectId: String,
    values: Collection<Color>,
    current: Color,
) {
    selectValue(labelText, selectId, values, true) { c ->
        label = c.name
        value = c.toString()
        selected = current == c
        style = "background-color:$c"
    }
}

fun HtmlBlockTag.selectOptionalColor(
    fieldLabel: String,
    selectId: String,
    selectedValue: Color?,
    values: Collection<Color>,
    update: Boolean = false,
) {
    selectOptionalValue(
        fieldLabel,
        selectId,
        selectedValue,
        values,
        update,
    ) { color ->
        label = color.name
        value = color.name
        style = "background-color:$color"
    }
}

// parse

