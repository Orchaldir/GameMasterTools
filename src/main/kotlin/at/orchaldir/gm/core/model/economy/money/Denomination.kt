package at.orchaldir.gm.core.model.economy.money

import at.orchaldir.gm.core.model.name.Name
import at.orchaldir.gm.core.model.name.Text
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.HasStartDate
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

@Serializable
data class Denomination(
    val text: Text = Text.init("gp"),
    val isPrefix: Boolean = false,
) {
    constructor(text: String, isPrefix: Boolean = false) : this(Text.init(text), isPrefix)

    fun display(value: Int) = if (isPrefix) {
        "${text.text} $value"
    } else {
        "$value ${text.text}"
    }

}