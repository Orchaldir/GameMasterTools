package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.character.appearance.UndefinedAppearance
import at.orchaldir.gm.core.model.character.title.AbstractTitle
import at.orchaldir.gm.core.model.character.title.NoTitle
import at.orchaldir.gm.core.model.character.title.TitleId
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.culture.language.ComprehensionLevel
import at.orchaldir.gm.core.model.culture.language.LanguageId
import at.orchaldir.gm.core.model.culture.name.getDefaultFamilyName
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.race.aging.LifeStageId
import at.orchaldir.gm.core.model.rpg.statblock.StatblockLookup
import at.orchaldir.gm.core.model.rpg.statblock.UndefinedStatblockLookup
import at.orchaldir.gm.core.model.time.Duration
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.util.origin.Origin
import at.orchaldir.gm.core.model.util.origin.OriginType
import at.orchaldir.gm.core.model.util.origin.UndefinedOrigin
import at.orchaldir.gm.core.model.util.origin.validateOriginType
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.util.source.HasDataSources
import at.orchaldir.gm.core.reducer.character.validateCharacterAppearance
import at.orchaldir.gm.core.reducer.character.validateCharacterData
import at.orchaldir.gm.core.reducer.character.validateEquipped
import at.orchaldir.gm.core.selector.time.date.getStartDay
import at.orchaldir.gm.core.selector.time.getCurrentDate
import at.orchaldir.gm.core.selector.time.getDefaultCalendar
import at.orchaldir.gm.core.selector.util.getGenonymName
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val CHARACTER_TYPE = "Character"
val ALLOWED_CHARACTER_AUTHENTICITY = listOf(
    AuthenticityType.Undefined,
    AuthenticityType.Authentic,
    AuthenticityType.SecretIdentity,
)
val ALLOWED_CHARACTER_ORIGINS = listOf(
    OriginType.Born,
    OriginType.Created,
    OriginType.Undefined,
)
val ALLOWED_HOUSING_TYPES = listOf(
    PositionType.Undefined,
    PositionType.Apartment,
    PositionType.District,
    PositionType.Home,
    PositionType.Homeless,
    PositionType.LongTermCare,
    PositionType.Moon,
    PositionType.Plane,
    PositionType.Realm,
    PositionType.Region,
    PositionType.Town,
    PositionType.World,
)
val ALLOWED_VITAL_STATUS_FOR_CHARACTER = setOf(
    VitalStatusType.Alive,
    VitalStatusType.Dead,
    VitalStatusType.Vanished,
)
val ALLOWED_CAUSES_OF_DEATH_FOR_CHARACTER = CauseOfDeathType.entries

@JvmInline
@Serializable
value class CharacterId(val value: Int) : Id<CharacterId> {

    override fun next() = CharacterId(value + 1)
    override fun type() = CHARACTER_TYPE
    override fun value() = value

}

@Serializable
data class Character(
    val id: CharacterId,
    val name: CharacterName = Mononym.init("Character ${id.value}"),
    val race: RaceId = RaceId(0),
    val gender: Gender = Gender.Male,
    val sexuality: SexualOrientation = SexualOrientation.Heterosexual,
    val origin: Origin = UndefinedOrigin,
    val age: CharacterAge = AgeViaDefaultLifeStage,
    val status: VitalStatus = Alive,
    val culture: CultureId? = null,
    val relationships: Map<CharacterId, Set<InterpersonalRelationship>> = mapOf(),
    val languages: Map<LanguageId, ComprehensionLevel> = emptyMap(),
    val appearance: Appearance = UndefinedAppearance,
    val equipped: Equipped = UndefinedEquipped,
    val housingStatus: History<Position> = History(UndefinedPosition),
    val employmentStatus: History<EmploymentStatus> = History(UndefinedEmploymentStatus),
    val beliefStatus: History<BeliefStatus> = History(UndefinedBeliefStatus),
    val title: TitleId? = null,
    val authenticity: Authenticity = Authentic,
    val statblock: StatblockLookup = UndefinedStatblockLookup,
    val sources: Set<DataSourceId> = emptySet(),
) : Element<CharacterId>, HasBelief, HasDataSources, HasVitalStatus {

    init {
        validateOriginType(origin, ALLOWED_CHARACTER_ORIGINS)
    }

    override fun id() = id

    override fun name(state: State): String {
        val title: AbstractTitle = state.getTitleStorage().getOptional(title) ?: NoTitle

        return when (name) {
            is FamilyName -> {
                state.getCultureStorage().getOptional(culture)?.namingConvention
                    ?.getFamilyName(name, gender, title)
                    ?: getDefaultFamilyName(name, gender, title)
            }

            is Genonym -> title.resolveFullName(state.getGenonymName(this, name), gender)
            is Mononym -> title.resolveFullName(name.name.text, gender)
        }
    }

    override fun toSortString(state: State) = name.toSortString()

    fun nameForSorting(state: State): String {
        val title: AbstractTitle = state.getTitleStorage().getOptional(title) ?: NoTitle

        return when (name) {
            is FamilyName -> title.resolveFamilyName(
                name.family.text,
                gender
            ) + ", " + name.given.text + if (name.middle != null) {
                " " + name.middle.text
            } else {
                ""
            }

            is Genonym -> title.resolveFullName(state.getGenonymName(this, name), gender)
            is Mononym -> title.resolveFullName(name.name.text, gender)
        }
    }

    override fun belief() = beliefStatus
    override fun sources() = sources
    override fun startDate() = age.date()
    override fun vitalStatus() = status

    fun getAge(state: State): Duration? = when (age) {
        is AgeViaBirthdate -> calculateAgeWithBirthdate(state, age.date)

        AgeViaDefaultLifeStage -> {
            val race = state.getRaceStorage().getOrThrow(race)

            race.lifeStages.getDefaultLifeStageId()?.let {
                approximateAgeViaLifeStage(state, race, it)
            }
        }

        is AgeViaLifeStage -> {
            val race = state.getRaceStorage().getOrThrow(race)

            approximateAgeViaLifeStage(state, race, age.lifeStage)
        }
    }

    private fun calculateAgeWithBirthdate(
        state: State,
        birthDate: Date,
    ): Duration {
        val currentDay = state.getCurrentDate()
        val defaultCalendar = state.getDefaultCalendar()
        val birthDay = defaultCalendar.getStartDay(birthDate)

        if (birthDay >= currentDay) {
            return Duration(0)
        }

        if (status is Dead) {
            val deathDate = defaultCalendar.getStartDay(status.date)

            if (deathDate < currentDay) {
                return deathDate.getDurationBetween(birthDay)
            }
        }

        return currentDay.getDurationBetween(birthDay)
    }

    private fun approximateAgeViaLifeStage(
        state: State,
        race: Race,
        lifeStageId: LifeStageId,
    ): Duration {
        val defaultCalendar = state.getDefaultCalendar()
        val lifeStage = race.lifeStages.getLifeStage(lifeStageId)

        return Duration(lifeStage.maxAge * defaultCalendar.getDaysPerYear())
    }

    fun isAlive(calendar: Calendar, date: Date): Boolean {
        if (calendar.isAfterOrEqual(date, this.date)) {
            if (status is Dead) {
                return calendar.isAfterOrEqual(status.date, date)
            }

            return true
        }

        return false
    }

    // employment

    fun checkEmploymentStatus(check: (EmploymentStatus) -> Boolean) = if (status is Alive) {
        check(employmentStatus.current)
    } else {
        false
    }

    fun checkPreviousEmploymentStatus(check: (EmploymentStatus) -> Boolean) =
        (status is Dead && check(employmentStatus.current)) ||
                employmentStatus.previousEntries.any { check(it.entry) }

    fun checkCurrentOrPreviousEmploymentStatus(check: (EmploymentStatus) -> Boolean) =
        check(employmentStatus.current) || employmentStatus.previousEntries.any { check(it.entry) }

    fun getBusiness() = if (status is Alive) {
        employmentStatus.current.getBusiness()
    } else {
        null
    }

    override fun validate(state: State) {
        validateCharacterData(state, this)
        validateCharacterAppearance(state, appearance, race)
        validateEquipped(state, equipped, statblock)
        state.getDataSourceStorage().require(sources)
    }
}