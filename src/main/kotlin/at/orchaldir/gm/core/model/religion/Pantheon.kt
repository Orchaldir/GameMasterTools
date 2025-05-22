package at.orchaldir.gm.core.model.religion

import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.name.NotEmptyString
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
    val name: Name = Name.init("Pantheon ${id.value}"),
    val title: NotEmptyString? = null,
    val gods: Set<GodId> = emptySet(),
) : ElementWithSimpleName<PantheonId> {

    override fun id() = id
    override fun name() = name.text

}