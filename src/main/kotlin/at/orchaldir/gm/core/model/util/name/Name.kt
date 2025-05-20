package at.orchaldir.gm.core.model.util.name

import at.orchaldir.gm.utils.titlecaseFirstChar
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class Name private constructor(val text: String) {

    companion object {
        fun init(name: String) = Name(name.trim().titlecaseFirstChar())
    }

    init {
        require(text.isNotEmpty()) { "Name is empty!" }
    }

}
