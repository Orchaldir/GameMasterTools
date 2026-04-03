package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.CALENDAR_ID_0
import at.orchaldir.gm.CHARACTER_ID_0
import at.orchaldir.gm.JOB_ID_0
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.job.Job
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.date.Day
import at.orchaldir.gm.core.model.util.Accident
import at.orchaldir.gm.core.model.util.Dead
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
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

    @Nested
    inner class NameTest {
        private val given = Name.init("John")
        private val jobName = Name.init("Smith")
        private val state = State(Storage(Job(JOB_ID_0, jobName)))

        @Test
        fun `An occupational with a job`() {
            val character = Character(CHARACTER_ID_0, name = OccupationalName(given))

            assertEquals("John", character.name(state))
        }

        @Test
        fun `An occupational name without a job`() {
            val character = Character(
                CHARACTER_ID_0,
                name = OccupationalName(given),
                employmentStatus = History(Employed(JOB_ID_0)),
            )
            assertEquals("John the Smith", character.name(state))
        }
    }

}