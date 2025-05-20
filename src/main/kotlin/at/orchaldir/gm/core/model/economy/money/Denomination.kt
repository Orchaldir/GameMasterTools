package at.orchaldir.gm.core.model.economy.money

import at.orchaldir.gm.core.model.util.name.NotEmptyString
import kotlinx.serialization.Serializable

@Serializable
data class Denomination(
    val text: NotEmptyString = NotEmptyString.init("gp"),
    val isPrefix: Boolean = false,
    val hasSpace: Boolean = false,
) {
    companion object {
        fun init(text: String, isPrefix: Boolean = false, hasSpace: Boolean = false) =
            Denomination(NotEmptyString.init(text), isPrefix, hasSpace)
    }

    fun display(value: Int): String {
        val center = if (hasSpace) {
            " "
        } else {
            ""
        }

        return if (isPrefix) {
            "${text.text}$center$value"
        } else {
            "$value$center${text.text}"
        }
    }

}