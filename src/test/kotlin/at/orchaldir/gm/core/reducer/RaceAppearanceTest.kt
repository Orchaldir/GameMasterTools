package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.DeleteRaceAppearance
import at.orchaldir.gm.core.action.UpdateRaceAppearance
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.ItemTemplate
import at.orchaldir.gm.core.model.item.Shirt
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.race.aging.ImmutableLifeStage
import at.orchaldir.gm.core.model.race.appearance.RaceAppearance
import at.orchaldir.gm.core.model.race.appearance.RaceAppearanceId
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val ID0 = RaceAppearanceId(0)

class RaceAppearanceTest {

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an existing appearance`() {
            val state = State(Storage(RaceAppearance(ID0)))
            val action = DeleteRaceAppearance(ID0)

            assertEquals(0, REDUCER.invoke(state, action).first.getRaceAppearanceStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteRaceAppearance(ID0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete an appearance used by a race`() {
            val race = Race(RaceId(0), lifeStages = ImmutableLifeStage(ID0))
            val state = State(
                listOf(
                    Storage(race),
                    Storage(RaceAppearance(ID0)),
                )
            )
            val action = DeleteRaceAppearance(ID0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateRaceAppearance(RaceAppearance(ID0))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Update is valid`() {
            val state = State(Storage(RaceAppearance(ID0)))
            val appearance = RaceAppearance(ID0, "Test")
            val action = UpdateRaceAppearance(appearance)

            assertEquals(appearance, REDUCER.invoke(state, action).first.getRaceAppearanceStorage().get(ID0))
        }
    }

}