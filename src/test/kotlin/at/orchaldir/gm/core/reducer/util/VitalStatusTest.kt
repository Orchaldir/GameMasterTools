package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateCharacter
import at.orchaldir.gm.core.model.Data
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.realm.Battle
import at.orchaldir.gm.core.model.realm.Catastrophe
import at.orchaldir.gm.core.model.realm.War
import at.orchaldir.gm.core.model.time.Time
import at.orchaldir.gm.core.model.time.date.Day
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class VitalStatusTest {

    @Nested
    inner class UpdateTest {

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
                Storage(Race(RACE_ID_0)),
                Storage(War(WAR_ID_0)),
            ),
            data = Data(time = Time(currentDate = Day(10))),
        )

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
        inner class MurdererTest {

            @Test
            fun `Died from murder`() {
                testDie(Day(5), Murder(CHARACTER_ID_1))
            }

            @Test
            fun `Murderer doesn't exist`() {
                testFailToDie(Day(5), Murder(CHARACTER_ID_2))
            }

            @Test
            fun `Murderer cannot be the same character`() {
                testFailToDie(Day(5), Murder(CHARACTER_ID_0))
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
            val character = Character(CHARACTER_ID_0, vitalStatus = Dead(deathDate, causeOfDeath))
            val action = UpdateCharacter(character)

            val result = REDUCER.invoke(state, action).first

            assertEquals(
                character,
                result.getCharacterStorage().getOrThrow(CHARACTER_ID_0)
            )
        }

        private fun testFailToDie(deathDate: Day, causeOfDeath: CauseOfDeath) {
            val action = UpdateCharacter(Character(CHARACTER_ID_0, vitalStatus = Dead(deathDate, causeOfDeath)))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }
    }
}