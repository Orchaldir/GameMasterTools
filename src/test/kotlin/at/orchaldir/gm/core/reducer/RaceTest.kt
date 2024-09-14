package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.DeleteRace
import at.orchaldir.gm.core.action.UpdateRace
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.race.aging.*
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val ID0 = RaceId(0)
private val ID1 = RaceId(1)

class RaceTest {

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an existing race`() {
            val state = State(Storage(listOf(Race(ID0), Race(ID1))))
            val action = DeleteRace(ID0)

            assertEquals(1, REDUCER.invoke(state, action).first.getRaceStorage().getSize())
        }

        @Test
        fun `Cannot delete the last race`() {
            val state = State(Storage(Race(ID0)))
            val action = DeleteRace(ID0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteRace(ID0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete a race used by a character`() {
            val character = Character(CharacterId(0), race = ID0)
            val state = State(
                listOf(
                    Storage(character),
                    Storage(Race(ID0)),
                )
            )
            val action = DeleteRace(ID0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateRace(Race(ID0))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Max age must be higher than the previous simple life stage`() {
            testMinAgeTooLow(::createSimpleLifeStage, ::createSimpleAging)
        }

        private fun <T> testMinAgeTooLow(createStage: (String, Int) -> T, createAging: (List<T>) -> LifeStages) {
            (0..5).forEach { maxAge ->
                val state = State(Storage(Race(ID0)))
                val race = Race(
                    ID0, "Test", lifeStages = createAging(
                        listOf(
                            createStage("A", 5),
                            createStage("B", maxAge),
                        )
                    )
                )
                val action = UpdateRace(race)

                assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
            }
        }

        @Test
        fun `Simple aging is valid`() {
            testIsValid(::createSimpleLifeStage, ::createSimpleAging)
        }

        private fun createSimpleLifeStage(name: String, maxAge: Int) = LifeStage(name, maxAge)
        private fun createSimpleAging(stages: List<LifeStage>) = SimpleAging(lifeStages = stages)

        private fun <T> testIsValid(createStage: (String, Int) -> T, createAging: (List<T>) -> LifeStages) {
            val state = State(Storage(Race(ID0)))
            val race = Race(
                ID0, "Test", lifeStages = createAging(
                    listOf(
                        createStage("A", 6),
                        createStage("B", 7),
                    )
                )
            )
            val action = UpdateRace(race)

            assertEquals(race, REDUCER.invoke(state, action).first.getRaceStorage().get(ID0))
        }
    }

}