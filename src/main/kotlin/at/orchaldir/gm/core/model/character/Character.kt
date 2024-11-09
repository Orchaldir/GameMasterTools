package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.Calendar
import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.character.appearance.UndefinedAppearance
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.language.ComprehensionLevel
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.time.Day
import at.orchaldir.gm.core.model.time.Duration
import at.orchaldir.gm.core.model.util.ElementWithComplexName
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.selector.getGenonymName
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val CHARACTER = "Character"

@JvmInline
@Serializable
value class CharacterId(val value: Int) : Id<CharacterId> {

    override fun next() = CharacterId(value + 1)
    override fun type() = CHARACTER
    override fun value() = value

}

@Serializable
data class Character(
    val id: CharacterId,
    val name: CharacterName = Mononym("Character ${id.value}"),
    val race: RaceId = RaceId(0),
    val gender: Gender = Gender.Genderless,
    val origin: CharacterOrigin = UndefinedCharacterOrigin,
    val birthDate: Day = Day(0),
    val vitalStatus: VitalStatus = Alive,
    val culture: CultureId = CultureId(0),
    val personality: Set<PersonalityTraitId> = emptySet(),
    val relationships: Map<CharacterId, Set<InterpersonalRelationship>> = mapOf(),
    val languages: Map<LanguageId, ComprehensionLevel> = emptyMap(),
    val appearance: Appearance = UndefinedAppearance,
    val equipmentMap: EquipmentMap = EquipmentMap(emptyMap()),
    val livingStatus: History<LivingStatus> = History(Homeless),
    val employmentStatus: History<EmploymentStatus> = History(Unemployed),
) : ElementWithComplexName<CharacterId> {

    override fun id() = id

    override fun name(state: State): String {
        return when (val name = name) {
            is FamilyName -> {
                val culture = state.getCultureStorage().getOrThrow(culture)

                culture.namingConvention.getFamilyName(name)
            }

            is Genonym -> state.getGenonymName(this, name)
            is Mononym -> name.name
        }
    }

    fun getAge(currentDay: Day): Duration {
        if (birthDate >= currentDay) {
            return Duration(0)
        }

        if (vitalStatus is Dead) {
            val deathDate = vitalStatus.deathDay

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