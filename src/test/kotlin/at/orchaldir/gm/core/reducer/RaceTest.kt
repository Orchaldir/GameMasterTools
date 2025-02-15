package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteRace
import at.orchaldir.gm.core.action.UpdateBusiness
import at.orchaldir.gm.core.action.UpdateRace
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.race.CreatedRace
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.aging.LifeStage
import at.orchaldir.gm.core.model.race.aging.LifeStages
import at.orchaldir.gm.core.model.race.aging.SimpleAging
import at.orchaldir.gm.core.model.util.CreatedByCharacter
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


class RaceTest {

    private val state = State(
        listOf(
            Storage(CALENDAR0),
            Storage(listOf(Race(RACE_ID_0), Race(RACE_ID_1))),
        )
    )

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an existing race`() {
            val action = DeleteRace(RACE_ID_0)

            assertEquals(1, REDUCER.invoke(state, action).first.getRaceStorage().getSize())
        }

        @Test
        fun `Cannot delete the last race`() {
            val state = State(Storage(Race(RACE_ID_0)))
            val action = DeleteRace(RACE_ID_0)

            assertIllegalArgument("Cannot delete the last race") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteRace(RACE_ID_2)

            assertIllegalArgument("Requires unknown Race 2!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot delete a race used by a character`() {
            val character = Character(CharacterId(0), race = RACE_ID_0)
            val newState = state.updateStorage(Storage(character))
            val action = DeleteRace(RACE_ID_0)

            assertIllegalArgument("Race 0 is used by characters") { REDUCER.invoke(newState, action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateRace(Race(RACE_ID_0))

            assertIllegalArgument("Requires unknown Race 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Max age must be higher than the previous simple life stage`() {
            testMinAgeTooLow(::createSimpleLifeStage, ::createSimpleAging)
        }

        private fun <T> testMinAgeTooLow(createStage: (String, Int) -> T, createAging: (List<T>) -> LifeStages) {
            (0..5).forEach { maxAge ->
                val state = State(Storage(Race(RACE_ID_0)))
                val race = Race(
                    RACE_ID_0, "Test", lifeStages = createAging(
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

        @Test
        fun `Creator must exist`() {
            val origin = CreatedRace(CreatedByCharacter(CHARACTER_ID_0), DAY0)
            val action = UpdateRace(Race(RACE_ID_0, origin = origin))

            assertIllegalArgument("Cannot use an unknown character 0 as Creator!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Date is in the future`() {
            val origin = CreatedRace(CreatedByCharacter(CHARACTER_ID_0), FUTURE_DAY_0)
            val action = UpdateRace(Race(RACE_ID_0, origin = origin))
            val newState = state.updateStorage(Storage(Character(CHARACTER_ID_0)))

            assertIllegalArgument("Date (Race) is in the future!") { REDUCER.invoke(newState, action) }
        }

        private fun createSimpleLifeStage(name: String, maxAge: Int) = LifeStage(name, maxAge)
        private fun createSimpleAging(stages: List<LifeStage>) = SimpleAging(lifeStages = stages)

        private fun <T> testIsValid(createStage: (String, Int) -> T, createAging: (List<T>) -> LifeStages) {
            val state = State(Storage(Race(RACE_ID_0)))
            val race = Race(
                RACE_ID_0, "Test", lifeStages = createAging(
                    listOf(
                        createStage("A", 6),
                        createStage("B", 7),
                    )
                )
            )
            val action = UpdateRace(race)

            assertEquals(race, REDUCER.invoke(state, action).first.getRaceStorage().get(RACE_ID_0))
        }
    }

}