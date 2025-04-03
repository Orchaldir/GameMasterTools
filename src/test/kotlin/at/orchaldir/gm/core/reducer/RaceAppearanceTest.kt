package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.action.DeleteRaceAppearance
import at.orchaldir.gm.core.action.UpdateRaceAppearance
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.hair.HairType
import at.orchaldir.gm.core.model.character.appearance.tail.SimpleTailShape
import at.orchaldir.gm.core.model.character.appearance.tail.SimpleTailShape.Cat
import at.orchaldir.gm.core.model.character.appearance.tail.SimpleTailShape.Rat
import at.orchaldir.gm.core.model.character.appearance.tail.TailColorType
import at.orchaldir.gm.core.model.character.appearance.tail.TailColorType.Hair
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.race.aging.ImmutableLifeStage
import at.orchaldir.gm.core.model.race.appearance.*
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

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

            assertIllegalArgument("Requires unknown Race Appearance 0!") { REDUCER.invoke(State(), action) }
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

            assertIllegalArgument("Race Appearance 0 cannot be deleted") { REDUCER.invoke(state, action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateRaceAppearance(RaceAppearance(ID0))

            assertIllegalArgument("Requires unknown Race Appearance 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `No tail options for for a simple tail shape`() {
            val state = State(Storage(RaceAppearance(ID0)))
            val tailOptions = TailOptions(simpleShapes = OneOf(Rat))
            val action = UpdateRaceAppearance(RaceAppearance(ID0, tailOptions = tailOptions))

            assertIllegalArgument("No options for Rat tail!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Reuse hair color option requires hair`() {
            val state = State(Storage(RaceAppearance(ID0)))
            val tailOptions = TailOptions(simpleOptions = mapOf(Cat to SimpleTailOptions(Hair)))
            val action = UpdateRaceAppearance(
                RaceAppearance(
                    ID0,
                    hairOptions = HairOptions(hairTypes = OneOf(HairType.None)),
                    tailOptions = tailOptions,
                )
            )

            assertIllegalArgument("Tail options for Cat require hair!") { REDUCER.invoke(state, action) }
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