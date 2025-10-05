package at.orchaldir.gm.core.model.race

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.race.aging.ImmutableLifeStage
import at.orchaldir.gm.core.model.race.aging.LifeStages
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.Creation
import at.orchaldir.gm.core.model.util.HasOrigin
import at.orchaldir.gm.core.model.util.HasStartDate
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.origin.Origin
import at.orchaldir.gm.core.model.util.origin.OriginType
import at.orchaldir.gm.core.model.util.origin.UndefinedOrigin
import at.orchaldir.gm.core.model.util.origin.validateOriginType
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.util.source.HasDataSources
import at.orchaldir.gm.core.reducer.race.validateHeight
import at.orchaldir.gm.core.reducer.race.validateLifeStages
import at.orchaldir.gm.core.reducer.util.validateDate
import at.orchaldir.gm.core.reducer.util.validateOrigin
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Distribution
import at.orchaldir.gm.utils.math.unit.Weight
import kotlinx.serialization.Serializable
import kotlin.math.pow

const val RACE_TYPE = "Race"
val DEFAULT_GENDERS = Gender.entries - Gender.Genderless
val MIN_RACE_HEIGHT = Distance.fromCentimeters(10)
val MAX_RACE_HEIGHT = Distance.fromCentimeters(500)
val ALLOWED_RACE_ORIGINS = listOf(
    OriginType.Combined,
    OriginType.Created,
    OriginType.Evolved,
    OriginType.Modified,
    OriginType.Original,
    OriginType.Planar,
    OriginType.Undefined,
)

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
    val name: Name = Name.init(id),
    val genders: OneOf<Gender> = OneOf(DEFAULT_GENDERS),
    val height: Distribution<Distance> = Distribution.fromMeters(1.8f),
    val weight: Weight = Weight.fromKilograms(75.0f),
    val lifeStages: LifeStages = ImmutableLifeStage(),
    val date: Date? = null,
    val origin: Origin = UndefinedOrigin,
    val sources: Set<DataSourceId> = emptySet(),
) : ElementWithSimpleName<RaceId>, HasDataSources, HasOrigin, HasStartDate {

    init {
        validateOriginType(origin, ALLOWED_RACE_ORIGINS)
    }

    override fun id() = id
    override fun name() = name.text
    override fun creator() = origin.creator()
    override fun origin() = origin
    override fun sources() = sources
    override fun startDate() = date

    fun calculateBodyMassIndex() = weight.toKilograms() / height.center.toMeters().pow(2)

    override fun clone(cloneId: RaceId) = copy(id = cloneId, name = Name.init("Clone ${cloneId.value}"))

    override fun validate(state: State) {
        validateDate(state, date, "Race")
        validateHeight(this)
        validateLifeStages(state, lifeStages)
        validateOrigin(state, id, origin, date, ::RaceId)
        state.getDataSourceStorage().require(sources)
    }

}