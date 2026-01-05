package at.orchaldir.gm.core.selector.race

import at.orchaldir.gm.RACE_APPEARANCE_ID_0
import at.orchaldir.gm.RACE_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.race.aging.ImmutableLifeStage
import at.orchaldir.gm.core.model.race.appearance.RaceAppearance
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RaceAppearanceTest {

    @Nested
    inner class CanDeleteTest {
        private val appearance = RaceAppearance(RACE_APPEARANCE_ID_0)
        private val state = State(
            listOf(
                Storage(appearance),
            )
        )

        @Test
        fun `Cannot delete an appearance used by a race`() {
            val race = Race(RaceId(0), lifeStages = ImmutableLifeStage(RACE_APPEARANCE_ID_0))
            val newState = state.updateStorage(race)

            failCanDelete(newState, RACE_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(
                DeleteResult(RACE_APPEARANCE_ID_0).addId(blockingId),
                state.canDeleteRaceAppearance(RACE_APPEARANCE_ID_0)
            )
        }
    }

}