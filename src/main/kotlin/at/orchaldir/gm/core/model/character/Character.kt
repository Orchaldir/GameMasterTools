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
import at.orchaldir.gm.core.model.item.equipment.EquipmentIdMap
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.time.Duration
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.time.date.Day
import at.orchaldir.gm.core.model.time.date.Year
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.util.origin.Origin
import at.orchaldir.gm.core.model.util.origin.OriginType
import at.orchaldir.gm.core.model.util.origin.UndefinedOrigin
import at.orchaldir.gm.core.model.util.origin.validateOriginType
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.util.source.HasDataSources
import at.orchaldir.gm.core.selector.time.calendar.getDefaultCalendar
import at.orchaldir.gm.core.selector.time.date.getStartDay
import at.orchaldir.gm.core.selector.util.getGenonymName
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val CHARACTER_TYPE = "Character"
val ALLOWED_CHARACTER_ORIGINS = listOf(
    OriginType.Born,
    OriginType.Created,
    OriginType.Undefined,
)

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
    val birthDate: Date = Year(0),
    val vitalStatus: VitalStatus = Alive,
    val culture: CultureId = CultureId(0),
    val personality: Set<PersonalityTraitId> = emptySet(),
    val relationships: Map<CharacterId, Set<InterpersonalRelationship>> = mapOf(),
    val languages: Map<LanguageId, ComprehensionLevel> = emptyMap(),
    val appearance: Appearance = UndefinedAppearance,
    val equipmentMap: EquipmentIdMap = EquipmentMap(),
    val housingStatus: History<HousingStatus> = History(UndefinedHousingStatus),
    val employmentStatus: History<EmploymentStatus> = History(UndefinedEmploymentStatus),
    val beliefStatus: History<BeliefStatus> = History(UndefinedBeliefStatus),
    val title: TitleId? = null,
    val sources: Set<DataSourceId> = emptySet(),
) : Element<CharacterId>, HasDataSources, HasVitalStatus {

    init {
        validateOriginType(origin, ALLOWED_CHARACTER_ORIGINS)
    }

    override fun id() = id

    override fun name(state: State): String {
        val title: AbstractTitle = state.getTitleStorage().getOptional(title) ?: NoTitle

        return when (name) {
            is FamilyName -> {
                val culture = state.getCultureStorage().getOrThrow(culture)

                culture.namingConvention.getFamilyName(name, gender, title)
            }

            is Genonym -> title.resolveFullName(state.getGenonymName(this, name), gender)
            is Mononym -> title.resolveFullName(name.name.text, gender)
        }
    }

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

    override fun sources() = sources
    override fun startDate() = birthDate
    override fun vitalStatus() = vitalStatus

    fun getAge(state: State, currentDay: Day): Duration {
        val defaultCalendar = state.getDefaultCalendar()
        val birthDate = defaultCalendar.getStartDay(birthDate)

        if (birthDate >= currentDay) {
            return Duration(0)
        }

        if (vitalStatus is Dead) {
            val deathDate = defaultCalendar.getStartDay(vitalStatus.deathDay)

            if (deathDate < currentDay) {
                return deathDate.getDurationBetween(birthDate)
            }
        }

        return currentDay.getDurationBetween(birthDate)
    }

    fun isAlive(calendar: Calendar, date: Date): Boolean {
        if (calendar.isAfterOrEqual(date, birthDate)) {
            if (vitalStatus is Dead) {
                return calendar.isAfterOrEqual(vitalStatus.deathDay, date)
            }

            return true
        }

        return false
    }

    // employment

    fun checkEmploymentStatus(check: (EmploymentStatus) -> Boolean) = if (vitalStatus is Alive) {
        check(employmentStatus.current)
    } else {
        false
    }

    fun checkPreviousEmploymentStatus(check: (EmploymentStatus) -> Boolean) =
        (vitalStatus is Dead && check(employmentStatus.current)) ||
                employmentStatus.previousEntries.any { check(it.entry) }

    fun checkCurrentOrPreviousEmploymentStatus(check: (EmploymentStatus) -> Boolean) =
        check(employmentStatus.current) || employmentStatus.previousEntries.any { check(it.entry) }

    fun getBusiness() = if (vitalStatus is Alive) {
        employmentStatus.current.getBusiness()
    } else {
        null
    }
}