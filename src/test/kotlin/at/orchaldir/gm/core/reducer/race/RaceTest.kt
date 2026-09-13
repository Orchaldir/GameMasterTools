package at.orchaldir.gm.core.reducer.race

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateAction
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.aging.CustomAging
import at.orchaldir.gm.core.model.race.aging.LifeStage
import at.orchaldir.gm.core.model.race.aging.LifeStages
import at.orchaldir.gm.core.model.race.appearance.RaceAppearance
import at.orchaldir.gm.core.model.rpg.equipment.EquipmentModifier
import at.orchaldir.gm.core.model.util.CharacterReference
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.origin.CreatedElement
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


class RaceTest {

    private val race0 = Race(RACE_ID_0)
    private val race1 = Race(RACE_ID_1)
    private val state = State(
        listOf(
            Storage(CALENDAR0),
            Storage(race0),
            Storage(RaceAppearance(RACE_APPEARANCE_ID_0)),
        )
    )

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            assertInvalid(race1, "Requires unknown Race 1!")
        }

        @Test
        fun `Race appearance must exist`() {
            val newState = state.removeStorage(RACE_APPEARANCE_ID_0)

            assertInvalid(race0, "Requires unknown Race Appearance 0!", newState)
        }

        @Test
        fun `Max age must be higher than the previous simple life stage`() {
            testMinAgeTooLow(::createSimpleLifeStage, ::createSimpleAging)
        }

        private fun <T> testMinAgeTooLow(createStage: (String, Int) -> T, createAging: (List<T>) -> LifeStages) {
            (0..5).forEach { maxAge ->
                val state = State(Storage(Race(RACE_ID_0)))
                val race = Race(
                    RACE_ID_0, NAME, lifeStages = createAging(
                        listOf(
                            createStage("A", 5),
                            createStage("B", maxAge),
                        )
                    )
                )
                val action = UpdateAction(race)

                assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
            }
        }

        @Test
        fun `Simple aging is valid`() {
            testIsValid(::createSimpleLifeStage, ::createSimpleAging)
        }

        @Test
        fun `Creator must exist`() {
            val origin = CreatedElement(CharacterReference(CHARACTER_ID_0))
            val race = Race(RACE_ID_0, date = DAY0, origin = origin)

            assertInvalid(race, "Requires unknown Creator (Character 0)!")
        }

        @Test
        fun `Date is in the future`() {
            val origin = CreatedElement(CharacterReference(CHARACTER_ID_0))
            val newState = state.updateStorage(Character(CHARACTER_ID_0))
            val race = Race(RACE_ID_0, date = FUTURE_DAY_0, origin = origin)

            assertInvalid(race, "Date (Race) is in the future!", newState)
        }

        private fun createSimpleLifeStage(name: String, maxAge: Int) = LifeStage(Name.init(name), maxAge)
        private fun createSimpleAging(stages: List<LifeStage>) = CustomAging(lifeStages = stages)

        private fun <T> testIsValid(createStage: (String, Int) -> T, createAging: (List<T>) -> LifeStages) {
            val race = Race(
                RACE_ID_0, NAME, lifeStages = createAging(
                    listOf(
                        createStage("A", 6),
                        createStage("B", 7),
                    )
                )
            )
            val action = UpdateAction(race)

            assertEquals(race, REDUCER.invoke(state, action).first.getRaceStorage().get(RACE_ID_0))
        }

        private fun assertInvalid(race: Race, message: String, s: State= state) {
            val action = UpdateAction(race)

            assertIllegalArgument(message) { REDUCER.invoke(s, action) }
        }
    }

}