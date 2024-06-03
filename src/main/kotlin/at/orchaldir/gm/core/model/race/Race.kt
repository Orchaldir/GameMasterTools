package at.orchaldir.gm.core.model.race

import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class RaceId(val value: Int) : Id<RaceId> {

    override fun next() = RaceId(value + 1)
    override fun value() = value

}

@Serializable
data class Race(
    val id: RaceId,
    val name: String = "Race ${id.value}",
    val appearance: AppearanceOptions = AppearanceOptions(),
) : Element<RaceId> {

    override fun id() = id
    override fun name() = name

}