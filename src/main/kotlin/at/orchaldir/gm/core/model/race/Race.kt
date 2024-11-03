package at.orchaldir.gm.core.model.race

import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.race.aging.ImmutableLifeStage
import at.orchaldir.gm.core.model.race.aging.LifeStages
import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.math.Distribution
import kotlinx.serialization.Serializable

const val RACE = "Race"

@JvmInline
@Serializable
value class RaceId(val value: Int) : Id<RaceId> {

    override fun next() = RaceId(value + 1)
    override fun type() = RACE
    override fun value() = value

}

@Serializable
data class Race(
    val id: RaceId,
    val name: String = "Race ${id.value}",
    val genders: OneOf<Gender> = OneOf(Gender.entries),
    val height: Distribution = Distribution.fromMeters(1.8f, 0.2f),
    val lifeStages: LifeStages = ImmutableLifeStage(),
) : ElementWithSimpleName<RaceId> {

    override fun id() = id
    override fun name() = name

}