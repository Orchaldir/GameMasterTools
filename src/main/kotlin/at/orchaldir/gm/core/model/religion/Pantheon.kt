package at.orchaldir.gm.core.model.religion

import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val PANTHEON_TYPE = "Pantheon"

@JvmInline
@Serializable
value class PantheonId(val value: Int) : Id<PantheonId> {

    override fun next() = PantheonId(value + 1)
    override fun type() = PANTHEON_TYPE
    override fun value() = value

}

@Serializable
data class Pantheon(
    val id: PantheonId,
    val name: String = "Pantheon ${id.value}",
    val title: String? = null,
    val gods: Set<GodId> = emptySet(),
) : ElementWithSimpleName<PantheonId> {

    override fun id() = id
    override fun name() = name

}