package at.orchaldir.gm.core.model.util.name

import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.titlecaseFirstChar
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class Name private constructor(val text: String) {

    companion object {
        fun init(name: String) = Name(name.trim().titlecaseFirstChar())
        fun <ID : Id<ID>> init(id: ID) = Name(id.print())
    }

    init {
        require(text.isNotEmpty()) { "Name is empty!" }
    }

}
