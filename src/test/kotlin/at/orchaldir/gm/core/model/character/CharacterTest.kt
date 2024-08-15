package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.core.model.time.Day
import at.orchaldir.gm.core.model.time.Duration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CharacterTest {

    @Nested
    inner class GetAgeTest {

        private val birthDate = Day(10)
        private val deathDay = Day(25)
        private val character = Character(CharacterId(0), birthDate = birthDate, causeOfDeath = Accident(deathDay))

        @Test
        fun `Age is 0 before the day of birth`() {
            assertGetAge(Day(2), 0)
        }

        @Test
        fun `Age is 0 on the day of birth`() {
            assertGetAge(birthDate, 0)
        }

        @Test
        fun `Correct age while while alive`() {
            val character = Character(CharacterId(0), birthDate = birthDate)
            assertEquals(Duration(5), character.getAge(Day(15)))
        }

        @Test
        fun `Correct age before death date`() {
            assertGetAge(Day(15), 5)
        }

        @Test
        fun `Correct age on the day of death`() {
            assertGetAge(deathDay, 15)
        }

        @Test
        fun `Age doesn't increase after dying`() {
            assertGetAge(Day(30), 15)
        }

        private fun assertGetAge(day: Day, duration: Int) {
            assertEquals(Duration(duration), character.getAge(day))
        }
    }

}