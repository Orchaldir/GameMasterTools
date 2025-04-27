package at.orchaldir.gm.core.model.economy.money

import at.orchaldir.gm.core.model.name.NotEmptyString
import kotlinx.serialization.Serializable

@Serializable
data class Denomination(
    val text: NotEmptyString = NotEmptyString.init("gp"),
    val isPrefix: Boolean = false,
) {
    companion object {
        fun init(text: String, isPrefix: Boolean = false) = Denomination(NotEmptyString.init(text), isPrefix)
    }

    fun display(value: Int) = if (isPrefix) {
        "${text.text} $value"
    } else {
        "$value ${text.text}"
    }

}