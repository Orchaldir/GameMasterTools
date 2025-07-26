package at.orchaldir.gm.app.html

import at.orchaldir.gm.app.COLOR
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.render.Color
import kotlinx.html.HtmlBlockTag
import kotlinx.html.span
import kotlinx.html.style

// show

fun HtmlBlockTag.fieldOptionalColor(color: Color?, label: String = "Color") {
    if (color != null) {
        fieldColor(color, label)
    }
}

fun HtmlBlockTag.fieldColor(color: Color, label: String = "Color") {
    field(label) {
        showOptionalColor(color)
    }
}

fun HtmlBlockTag.showOptionalColor(color: Color?) {
    if (color != null) {
        showColor(color)
    }
}

fun HtmlBlockTag.showColor(color: Color) {
    showColorBlock(color)
    +" $color"
}

fun HtmlBlockTag.showColorBlock(color: Color) {
    span {
        style = "color:$color"
        +"█"
    }
}

// edit

fun HtmlBlockTag.selectColor(
    labelText: String,
    selectId: String,
    rarityMap: OneOf<Color>,
    current: Color,
) {
    selectFromOneOf(labelText, selectId, rarityMap, current) { c ->
        label = c.name
        value = c.toString()
        style = "background-color:$c"
    }
}

fun HtmlBlockTag.selectColor(
    current: Color,
    selectId: String = COLOR,
    labelText: String = "Color",
    values: Collection<Color> = Color.entries,
) {
    selectValue(labelText, selectId, values) { c ->
        label = c.name
        value = c.toString()
        selected = current == c
        style = "background-color:$c"
    }
}

fun HtmlBlockTag.selectOptionalColor(
    selectedValue: Color?,
    selectId: String = COLOR,
    fieldLabel: String = "Color",
    values: Collection<Color> = Color.entries,
) {
    selectOptionalValue(
        fieldLabel,
        selectId,
        selectedValue,
        values,
    ) { color ->
        label = color.name
        value = color.name
        style = "background-color:$color"
    }
}

// parse

