package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.NAME
import at.orchaldir.gm.RACE_APPEARANCE_ID_0
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.action.DeleteRaceAppearance
import at.orchaldir.gm.core.action.UpdateRaceAppearance
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.FeatureColorType.Hair
import at.orchaldir.gm.core.model.character.appearance.hair.HairType
import at.orchaldir.gm.core.model.character.appearance.tail.SimpleTailShape.Cat
import at.orchaldir.gm.core.model.character.appearance.tail.SimpleTailShape.Rat
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.race.aging.ImmutableLifeStage
import at.orchaldir.gm.core.model.race.appearance.FeatureColorOptions
import at.orchaldir.gm.core.model.race.appearance.HairOptions
import at.orchaldir.gm.core.model.race.appearance.RaceAppearance
import at.orchaldir.gm.core.model.race.appearance.TailOptions
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RaceAppearanceTest {

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an existing appearance`() {
            val state = State(Storage(RaceAppearance(RACE_APPEARANCE_ID_0)))
            val action = DeleteRaceAppearance(RACE_APPEARANCE_ID_0)

            assertEquals(0, REDUCER.invoke(state, action).first.getRaceAppearanceStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteRaceAppearance(RACE_APPEARANCE_ID_0)

            assertIllegalArgument("Requires unknown Race Appearance 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete an appearance used by a race`() {
            val race = Race(RaceId(0), lifeStages = ImmutableLifeStage(RACE_APPEARANCE_ID_0))
            val state = State(
                listOf(
                    Storage(race),
                    Storage(RaceAppearance(RACE_APPEARANCE_ID_0)),
                )
            )
            val action = DeleteRaceAppearance(RACE_APPEARANCE_ID_0)

            assertIllegalArgument("Race Appearance 0 cannot be deleted") { REDUCER.invoke(state, action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateRaceAppearance(RaceAppearance(RACE_APPEARANCE_ID_0))

            assertIllegalArgument("Requires unknown Race Appearance 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `No tail options for for a simple tail shape`() {
            val state = State(Storage(RaceAppearance(RACE_APPEARANCE_ID_0)))
            val tailOptions = TailOptions(simpleShapes = OneOf(Rat))
            val action = UpdateRaceAppearance(RaceAppearance(RACE_APPEARANCE_ID_0, tail = tailOptions))

            assertIllegalArgument("No options for Rat tail!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Reuse hair color option requires hair`() {
            val state = State(Storage(RaceAppearance(RACE_APPEARANCE_ID_0)))
            val tailOptions = TailOptions(simpleOptions = mapOf(Cat to FeatureColorOptions(Hair)))
            val action = UpdateRaceAppearance(
                RaceAppearance(
                    RACE_APPEARANCE_ID_0,
                    hair = HairOptions(hairTypes = OneOf(HairType.None)),
                    tail = tailOptions,
                )
            )

            assertIllegalArgument("Tail options for Cat require hair!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Update is valid`() {
            val state = State(Storage(RaceAppearance(RACE_APPEARANCE_ID_0)))
            val appearance = RaceAppearance(RACE_APPEARANCE_ID_0, NAME)
            val action = UpdateRaceAppearance(appearance)

            assertEquals(
                appearance,
                REDUCER.invoke(state, action).first.getRaceAppearanceStorage().get(RACE_APPEARANCE_ID_0)
            )
        }
    }

}