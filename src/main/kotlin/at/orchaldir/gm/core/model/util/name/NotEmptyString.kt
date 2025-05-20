package at.orchaldir.gm.core.model.util.name

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class NotEmptyString private constructor(val text: String) {

    companion object {
        fun init(name: String) = NotEmptyString(name.trim())
    }

    init {
        require(text.isNotEmpty()) { "Text is empty!" }
    }

}
