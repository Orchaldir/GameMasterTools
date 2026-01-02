package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.CALENDAR_ID_0
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.date.Day
import at.orchaldir.gm.core.model.util.Accident
import at.orchaldir.gm.core.model.util.Dead
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse

class CharacterTest {

    @Nested
    inner class AgeTest {

        private val birthDate = Day(10)
        private val deathDay = Day(25)
        private val age = AgeViaBirthdate(birthDate)
        private val character = Character(CharacterId(0), age = age, status = Dead(deathDay, Accident))
        private val calendar = Calendar(CALENDAR_ID_0)
        private val state = State(Storage(calendar))


        @Test
        fun `Not alive before the day of birth`() {
            assertFalse(character.isAlive(state, Day(2)))
        }

        @Test
        fun `Alive on the day of birth`() {
            assertTrue(character.isAlive(state, birthDate))
        }

        @Test
        fun `Alive between birth & death`() {
            assertTrue(character.isAlive(state, Day(15)))
        }

        @Test
        fun `Alive on the day of death`() {
            assertTrue(character.isAlive(state, deathDay))
        }

        @Test
        fun `Dead after dying`() {
            assertFalse(character.isAlive(state, Day(30)))
        }
    }

}