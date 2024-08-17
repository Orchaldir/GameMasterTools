package at.orchaldir.gm.core.model.material

import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val MOON = "Moon"

@JvmInline
@Serializable
value class MoonId(val value: Int) : Id<MoonId> {

    override fun next() = MoonId(value + 1)
    override fun type() = MOON
    override fun value() = value

}

@Serializable
data class Moon(
    val id: MoonId,
    val name: String = "Moon ${id.value}",
) : Element<MoonId> {

    override fun id() = id
    override fun name() = name

}