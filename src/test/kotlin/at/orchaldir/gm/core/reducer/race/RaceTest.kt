package at.orchaldir.gm.core.reducer.race

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteRace
import at.orchaldir.gm.core.action.UpdateRace
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.aging.LifeStage
import at.orchaldir.gm.core.model.race.aging.LifeStages
import at.orchaldir.gm.core.model.race.aging.SimpleAging
import at.orchaldir.gm.core.model.race.appearance.RaceAppearance
import at.orchaldir.gm.core.model.realm.District
import at.orchaldir.gm.core.model.realm.Realm
import at.orchaldir.gm.core.model.realm.Town
import at.orchaldir.gm.core.model.util.CharacterReference
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.origin.CreatedElement
import at.orchaldir.gm.core.model.util.population.PopulationPerRace
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.HALF
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


class RaceTest {

    private val state = State(
        listOf(
            Storage(CALENDAR0),
            Storage(listOf(Race(RACE_ID_0), Race(RACE_ID_1))),
            Storage(RaceAppearance(RACE_APPEARANCE_ID_0)),
        )
    )

    @Nested
    inner class DeleteTest {
        val action = DeleteRace(RACE_ID_0)

        @Test
        fun `Can delete an existing race`() {
            assertEquals(1, REDUCER.invoke(state, action).first.getRaceStorage().getSize())
        }

        @Test
        fun `Cannot delete the last race`() {
            val state = State(Storage(Race(RACE_ID_0)))

            assertIllegalArgument("Cannot delete the last race") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteRace(UNKNOWN_RACE_ID)

            assertIllegalArgument("Requires unknown Race 99!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot delete a race used by a character`() {
            val character = Character(CharacterId(0), race = RACE_ID_0)
            val newState = state.updateStorage(Storage(character))

            assertIllegalArgument("Cannot delete Race 0, because it is used by a character!") {
                REDUCER.invoke(
                    newState,
                    action
                )
            }
        }

        @Nested
        inner class PopulationTest {
            val population = PopulationPerRace(100, mapOf(RACE_ID_0 to HALF))

            @Test
            fun `Cannot delete a race used by the population of a district`() {
                asserPopulation(District(DISTRICT_ID_0, population = population))
            }

            @Test
            fun `Cannot delete a race used by the population of a realm`() {
                asserPopulation(Realm(REALM_ID_0, population = population))
            }

            @Test
            fun `Cannot delete a race used by the population of a town`() {
                asserPopulation(Town(TOWN_ID_0, population = population))
            }

            private fun <ID : Id<ID>, ELEMENT : Element<ID>> asserPopulation(element: ELEMENT) {
                val newState = state.updateStorage(Storage(element))

                assertIllegalArgument("Cannot delete Race 0, because it is used by a population!") {
                    REDUCER.invoke(
                        newState,
                        action
                    )
                }
            }
        }
    }

    @Nested
    inner class UpdateTest {
        val action = UpdateRace(Race(RACE_ID_0))

        @Test
        fun `Cannot update unknown id`() {

            assertIllegalArgument("Requires unknown Race 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Race appearance must exist`() {
            val newState = state.removeStorage(RACE_APPEARANCE_ID_0)

            assertIllegalArgument("Requires unknown Race Appearance 0!") { REDUCER.invoke(newState, action) }
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
            val origin = CreatedElement(CharacterReference(CHARACTER_ID_0))
            val action = UpdateRace(Race(RACE_ID_0, date = DAY0, origin = origin))

            assertIllegalArgument("Requires unknown Creator (Character 0)!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Date is in the future`() {
            val origin = CreatedElement(CharacterReference(CHARACTER_ID_0))
            val action = UpdateRace(Race(RACE_ID_0, date = FUTURE_DAY_0, origin = origin))
            val newState = state.updateStorage(Storage(Character(CHARACTER_ID_0)))

            assertIllegalArgument("Date (Race) is in the future!") { REDUCER.invoke(newState, action) }
        }

        private fun createSimpleLifeStage(name: String, maxAge: Int) = LifeStage(Name.init(name), maxAge)
        private fun createSimpleAging(stages: List<LifeStage>) = SimpleAging(lifeStages = stages)

        private fun <T> testIsValid(createStage: (String, Int) -> T, createAging: (List<T>) -> LifeStages) {
            val race = Race(
                RACE_ID_0, NAME, lifeStages = createAging(
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