package at.orchaldir.gm.core.model.name

import at.orchaldir.gm.utils.titlecaseFirstChar
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class Text private constructor(val text: String) {

    companion object {
        fun init(name: String) = Text(name.trim())
    }

    init {
        require(text.isNotEmpty()) { "Text is empty!" }
    }

}
