package at.orchaldir.gm.core.model.race

import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.race.aging.ImmutableLifeStage
import at.orchaldir.gm.core.model.race.aging.LifeStages
import at.orchaldir.gm.core.model.util.Creation
import at.orchaldir.gm.core.model.util.HasStartDate
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.util.source.HasDataSources
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Distribution
import at.orchaldir.gm.utils.math.unit.Weight
import kotlinx.serialization.Serializable
import kotlin.math.pow

const val RACE_TYPE = "Race"
val MIN_RACE_HEIGHT = Distance.fromCentimeters(10)
val MAX_RACE_HEIGHT = Distance.fromCentimeters(500)

@JvmInline
@Serializable
value class RaceId(val value: Int) : Id<RaceId> {

    override fun next() = RaceId(value + 1)
    override fun type() = RACE_TYPE
    override fun value() = value

}

@Serializable
data class Race(
    val id: RaceId,
    val name: Name = Name.init("Race ${id.value}"),
    val genders: OneOf<Gender> = OneOf(Gender.entries),
    val height: Distribution<Distance> = Distribution.fromMeters(1.8f),
    val weight: Weight = Weight.fromKilograms(75.0f),
    val lifeStages: LifeStages = ImmutableLifeStage(),
    val origin: RaceOrigin = OriginalRace,
    val sources: Set<DataSourceId> = emptySet(),
) : ElementWithSimpleName<RaceId>, Creation, HasDataSources, HasStartDate {

    override fun id() = id
    override fun name() = name.text
    override fun creator() = origin.creator()
    override fun sources() = sources
    override fun startDate() = origin.startDate()

    fun calculateBodyMassIndex() = weight.toKilograms() / height.center.toMeters().pow(2)

}