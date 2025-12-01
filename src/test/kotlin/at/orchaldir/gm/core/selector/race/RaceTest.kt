package at.orchaldir.gm.core.selector.race

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterTemplate
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceGroup
import at.orchaldir.gm.core.model.realm.District
import at.orchaldir.gm.core.model.realm.Realm
import at.orchaldir.gm.core.model.realm.Town
import at.orchaldir.gm.core.model.util.population.PopulationPerRace
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.HALF
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RaceTest {

    @Nested
    inner class CanDeleteTest {
        private val race = Race(RACE_ID_0)
        private val state = State(
            listOf(
                Storage(race),
            )
        )
        val population = PopulationPerRace(100, mapOf(RACE_ID_0 to HALF))

        @Test
        fun `Cannot delete a race part of a group`() {
            val group = RaceGroup(RACE_GROUP_ID_0, races = setOf(RACE_ID_0))
            val newState = state.updateStorage(Storage(group))

            failCanDelete(newState, RACE_GROUP_ID_0)
        }

        @Test
        fun `Cannot delete a race used by a character`() {
            val character = Character(CHARACTER_ID_0, race = RACE_ID_0)
            val newState = state.updateStorage(Storage(character))

            failCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete a race used by a character template`() {
            val template = CharacterTemplate(CHARACTER_TEMPLATE_ID_0, race = RACE_ID_0)
            val newState = state.updateStorage(Storage(template))

            failCanDelete(newState, CHARACTER_TEMPLATE_ID_0)
        }

        @Test
        fun `Cannot delete a race used by the population of a district`() {
            val district = District(DISTRICT_ID_0, population = population)
            val newState = state.updateStorage(Storage(district))

            failCanDelete(newState, DISTRICT_ID_0)
        }

        @Test
        fun `Cannot delete a race used by the population of a realm`() {
            val realm = Realm(REALM_ID_0, population = population)
            val newState = state.updateStorage(Storage(realm))

            failCanDelete(newState, REALM_ID_0)
        }

        @Test
        fun `Cannot delete a race used by the population of a town`() {
            val town = Town(TOWN_ID_0, population = population)
            val newState = state.updateStorage(Storage(town))

            failCanDelete(newState, TOWN_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(DeleteResult(RACE_ID_0).addId(blockingId), state.canDeleteRace(RACE_ID_0))
        }
    }

}