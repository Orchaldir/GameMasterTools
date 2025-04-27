package at.orchaldir.gm.core.model.economy.money

import at.orchaldir.gm.core.model.name.Text
import kotlinx.serialization.Serializable

@Serializable
data class Denomination(
    val text: Text = Text.init("gp"),
    val isPrefix: Boolean = false,
) {
    companion object {
        fun init(text: String, isPrefix: Boolean = false) = Denomination(Text.init(text), isPrefix)
    }

    fun display(value: Int) = if (isPrefix) {
        "${text.text} $value"
    } else {
        "$value ${text.text}"
    }

}