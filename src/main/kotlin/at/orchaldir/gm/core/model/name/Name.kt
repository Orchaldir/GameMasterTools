package at.orchaldir.gm.core.model.name

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class Name private constructor(val text: String) {

    companion object {
        fun init(name: String) = Name(name.trim())
    }

    init {
        require(text.isNotEmpty()) { "Name is empty!" }
    }

}
