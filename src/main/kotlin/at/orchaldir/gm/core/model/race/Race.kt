package at.orchaldir.gm.core.model.race

import at.orchaldir.gm.core.model.appearance.OneOf
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.race.appearance.AppearanceOptions
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
    val genders: OneOf<Gender> = OneOf(Gender.entries),
    val appearance: AppearanceOptions = AppearanceOptions(),
) : Element<RaceId> {

    override fun id() = id

}