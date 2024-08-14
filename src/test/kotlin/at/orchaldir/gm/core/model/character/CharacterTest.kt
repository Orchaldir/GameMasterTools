package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.core.model.time.Day
import at.orchaldir.gm.core.model.time.Duration
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CharacterTest {

    @Nested
    inner class GetAgeTest {

        private val birthDate = Day(10)
        private val deathDay = Day(25)
        private val character = Character(CharacterId(0), birthDate = birthDate, causeOfDeath = Accident(deathDay))

        @Test
        fun `Age is zero before birthdate`() {
            assertEquals(Duration(0), character.getAge(Day(2)))
        }

        @Test
        fun `Age is zero on the birthdate`() {
            assertEquals(Duration(0), character.getAge(birthDate))
        }

        @Test
        fun `Correct age whole while alive`() {
            assertEquals(Duration(5), character.getAge(Day(15)))
        }

        @Test
        fun `Correct age on death date`() {
            assertEquals(Duration(15), character.getAge(deathDay))
        }

        @Test
        fun `Age doesn't increase after dying`() {
            assertEquals(Duration(15), character.getAge(Day(30)))
        }
    }

}