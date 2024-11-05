package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.core.model.calendar.Calendar
import at.orchaldir.gm.core.model.calendar.CalendarId
import at.orchaldir.gm.core.model.time.Day
import at.orchaldir.gm.core.model.time.Duration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse

class CharacterTest {

    @Nested
    inner class AgeTest {

        private val birthDate = Day(10)
        private val deathDay = Day(25)
        private val character = Character(CharacterId(0), birthDate = birthDate, vitalStatus = Dead(deathDay, Accident))
        private val calendar = Calendar(CalendarId(0))

        @Test
        fun `Age is 0 before the day of birth`() {
            assertGetAge(Day(2), 0)
        }

        @Test
        fun `Not alive before the day of birth`() {
            assertFalse(character.isAlive(calendar, Day(2)))
        }

        @Test
        fun `Age is 0 on the day of birth`() {
            assertGetAge(birthDate, 0)
        }

        @Test
        fun `Alive on the day of birth`() {
            assertTrue(character.isAlive(calendar, birthDate))
        }

        @Test
        fun `Correct age while while alive`() {
            assertGetAge(Day(15), 5)
        }

        @Test
        fun `Alive between birth & death`() {
            assertTrue(character.isAlive(calendar, Day(15)))
        }

        @Test
        fun `Correct age on the day of death`() {
            assertGetAge(deathDay, 15)
        }

        @Test
        fun `Alive on the day of death`() {
            assertTrue(character.isAlive(calendar, deathDay))
        }

        @Test
        fun `Age doesn't increase after dying`() {
            assertGetAge(Day(30), 15)
        }

        @Test
        fun `Dead after dying`() {
            assertFalse(character.isAlive(calendar, Day(30)))
        }

        private fun assertGetAge(day: Day, duration: Int) {
            assertEquals(Duration(duration), character.getAge(day))
            // assertEquals(alive, character.isAlive(calendar, day))
        }
    }

}