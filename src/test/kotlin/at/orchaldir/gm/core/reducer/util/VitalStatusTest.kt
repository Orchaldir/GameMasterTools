package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateAction
import at.orchaldir.gm.core.model.Config
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.AgeViaBirthdate
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.health.Disease
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.realm.Battle
import at.orchaldir.gm.core.model.realm.Catastrophe
import at.orchaldir.gm.core.model.realm.War
import at.orchaldir.gm.core.model.time.Time
import at.orchaldir.gm.core.model.time.date.Day
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class VitalStatusTest {

    private val age = AgeViaBirthdate(DAY0)
    private val state = State(
        listOf(
            Storage(Battle(BATTLE_ID_0)),
            Storage(Catastrophe(CATASTROPHE_ID_0)),
            Storage(CALENDAR0),
            Storage(
                listOf(
                    Character(CHARACTER_ID_0),
                    Character(CHARACTER_ID_1),
                )
            ),
            Storage(Culture(CULTURE_ID_0)),
            Storage(Disease(DISEASE_ID_0)),
            Storage(Race(RACE_ID_0)),
            Storage(War(WAR_ID_0)),
        ),
        config = Config(time = Time(currentDate = Day(10))),
    )

    @Nested
    inner class UpdateTest {

        @Nested
        inner class DateTest {
            @Test
            fun `Cannot die in the future`() {
                testFailToDie(Day(11), Accident)
            }

            @Test
            fun `Cannot die before being born`() {
                testFailToDie(Day(-1), Accident)
            }
        }

        @Test
        fun `Died from accident`() {
            testDie(Day(5), Accident)
        }

        @Nested
        inner class KilledByTest {
            @Test
            fun `Died from murder`() {
                testDie(Day(5), KilledBy(CharacterReference(CHARACTER_ID_1)))
            }

            @Test
            fun `Killer doesn't exist`() {
                testFailToDie(Day(5), KilledBy(GodReference(UNKNOWN_GOD_ID)))
            }

            @Test
            fun `Murderer cannot be the same character`() {
                testFailToDie(Day(5), KilledBy(CharacterReference(CHARACTER_ID_0)))
            }
        }

        @Nested
        inner class BattleTest {

            @Test
            fun `Died in battle`() {
                testDie(Day(5), DeathInBattle(BATTLE_ID_0))
            }

            @Test
            fun `Battle doesn't exist`() {
                testFailToDie(Day(5), DeathInBattle(UNKNOWN_BATTLE_ID))
            }
        }

        @Nested
        inner class CatastropheTest {

            @Test
            fun `Died from catastrophe`() {
                testDie(Day(5), DeathByCatastrophe(CATASTROPHE_ID_0))
            }

            @Test
            fun `Catastrophe doesn't exist`() {
                testFailToDie(Day(5), DeathByCatastrophe(UNKNOWN_CATASTROPHE_ID))
            }
        }

        @Nested
        inner class DiseaseTest {

            @Test
            fun `Killed in war`() {
                testDie(Day(5), DeathByDisease(DISEASE_ID_0))
            }

            @Test
            fun `War doesn't exist`() {
                testFailToDie(Day(5), DeathByDisease(UNKNOWN_DISEASE_ID))
            }
        }

        @Nested
        inner class WarTest {

            @Test
            fun `Killed in war`() {
                testDie(Day(5), DeathInWar(WAR_ID_0))
            }

            @Test
            fun `War doesn't exist`() {
                testFailToDie(Day(5), DeathInWar(UNKNOWN_WAR_ID))
            }
        }

        @Test
        fun `Died from old age`() {
            testDie(Day(5), OldAge)
        }

        private fun testDie(deathDate: Day, causeOfDeath: CauseOfDeath) {
            val character = Character(CHARACTER_ID_0, status = Dead(deathDate, causeOfDeath))
            val action = UpdateAction(character)

            val result = REDUCER.invoke(state, action).first

            assertEquals(
                character,
                result.getCharacterStorage().getOrThrow(CHARACTER_ID_0)
            )
        }

        private fun testFailToDie(deathDate: Day, causeOfDeath: CauseOfDeath) {
            val action = UpdateAction(
                Character(
                    CHARACTER_ID_0,
                    age = AgeViaBirthdate(Day(0)),
                    status = Dead(deathDate, causeOfDeath)
                )
            )

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }
    }
}

fun <ID : Id<ID>, ELEMENT : Element<ID>> testAllowedVitalStatusTypes(
    state: State,
    isValidMap: Map<VitalStatusType, Boolean>,
    create: (VitalStatus) -> ELEMENT,
) {
    VitalStatusType.entries.forEach { type ->
        require(isValidMap.containsKey(type)) { "Input doesn't contain type $type!" }

        val status = when (type) {
            VitalStatusType.Abandoned -> Abandoned(DAY2)
            VitalStatusType.Alive -> Alive
            VitalStatusType.Closed -> Closed(DAY2)
            VitalStatusType.Dead -> Dead(DAY2)
            VitalStatusType.Destroyed -> Destroyed(DAY2)
            VitalStatusType.Vanished -> Vanished(DAY2)
        }
        val element = create(status)
        val action = UpdateAction(element)

        if (isValidMap.getValue(type)) {
            REDUCER.invoke(state, action)
        } else {
            assertIllegalArgument("Invalid vital status ${type}!") { REDUCER.invoke(state, action) }
        }
    }
}