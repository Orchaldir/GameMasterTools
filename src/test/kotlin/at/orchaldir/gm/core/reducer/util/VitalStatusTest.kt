package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateCharacter
import at.orchaldir.gm.core.model.Data
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.race.Race
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
                Storage(CALENDAR0),
                Storage(
                    listOf(
                        Character(CHARACTER_ID_0),
                        Character(CHARACTER_ID_1),
                    )
                ),
                Storage(listOf(Culture(CULTURE_ID_0))),
                Storage(listOf(Race(RACE_ID_0))),
            ),
            data = Data(time = Time(currentDate = Day(10))),
        )

        @Test
        fun `Died from accident`() {
            testDie(Day(5), Accident)
        }

        @Test
        fun `Cannot die from accident in the future`() {
            testFailToDie(Day(11), Accident)
        }

        @Test
        fun `Cannot die from accident before its origin`() {
            testFailToDie(Day(-1), Accident)
        }

        @Test
        fun `Died from murder`() {
            testDie(Day(5), Murder(CHARACTER_ID_1))
        }

        @Test
        fun `Cannot die from murder in the future`() {
            testFailToDie(Day(11), Murder(CHARACTER_ID_1))
        }

        @Test
        fun `Cannot die from murder before its origin`() {
            testFailToDie(Day(-1), Murder(CHARACTER_ID_1))
        }

        @Test
        fun `Catastrophe doesn't exist`() {
            testFailToDie(Day(5), DeathByCatastrophe(UNKNOWN_CATASTROPHE_ID))
        }

        @Test
        fun `Killer doesn't exist`() {
            testFailToDie(Day(5), Murder(CHARACTER_ID_2))
        }

        @Test
        fun `War doesn't exist`() {
            testFailToDie(Day(5), DeathByWar(UNKNOWN_WAR_ID))
        }

        @Test
        fun `Battle doesn't exist`() {
            testFailToDie(Day(5), DeathInBattle(UNKNOWN_BATTLE_ID))
        }

        @Test
        fun `Died from old age`() {
            testDie(Day(5), OldAge)
        }

        @Test
        fun `Cannot die from old age in the future`() {
            testFailToDie(Day(11), OldAge)
        }

        @Test
        fun `Cannot die from old age before its origin`() {
            testFailToDie(Day(-1), OldAge)
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