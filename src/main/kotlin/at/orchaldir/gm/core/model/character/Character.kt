package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.character.appearance.UndefinedAppearance
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.language.ComprehensionLevel
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.time.Duration
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.time.date.Day
import at.orchaldir.gm.core.model.time.date.Year
import at.orchaldir.gm.core.model.util.HasStartDate
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.selector.getGenonymName
import at.orchaldir.gm.core.selector.time.calendar.getDefaultCalendar
import at.orchaldir.gm.core.selector.time.date.getStartDay
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val CHARACTER_TYPE = "Character"

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
    val name: CharacterName = Mononym("Character ${id.value}"),
    val race: RaceId = RaceId(0),
    val gender: Gender = Gender.Genderless,
    val origin: CharacterOrigin = UndefinedCharacterOrigin,
    val birthDate: Date = Year(0),
    val vitalStatus: VitalStatus = Alive,
    val culture: CultureId = CultureId(0),
    val personality: Set<PersonalityTraitId> = emptySet(),
    val relationships: Map<CharacterId, Set<InterpersonalRelationship>> = mapOf(),
    val languages: Map<LanguageId, ComprehensionLevel> = emptyMap(),
    val appearance: Appearance = UndefinedAppearance,
    val equipmentMap: EquipmentMap = EquipmentMap(emptyMap()),
    val housingStatus: History<HousingStatus> = History(UndefinedHousingStatus),
    val employmentStatus: History<EmploymentStatus> = History(UndefinedEmploymentStatus),
    val beliefStatus: History<BeliefStatus> = History(UndefinedBeliefStatus),
) : Element<CharacterId>, HasStartDate {

    override fun id() = id

    override fun name(state: State): String {
        return when (name) {
            is FamilyName -> {
                val culture = state.getCultureStorage().getOrThrow(culture)

                culture.namingConvention.getFamilyName(name)
            }

            is Genonym -> state.getGenonymName(this, name)
            is Mononym -> name.name
        }
    }

    override fun startDate() = birthDate

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

}