package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteSpell
import at.orchaldir.gm.core.action.UpdateIllness
import at.orchaldir.gm.core.model.Data
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.illness.Illness
import at.orchaldir.gm.core.model.IllnessId
import at.orchaldir.gm.core.model.magic.Spell
import at.orchaldir.gm.core.model.SpellId
import at.orchaldir.gm.core.model.time.Time
import at.orchaldir.gm.core.model.time.date.Day
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class OriginTest {

    private val origin = NaturalOrigin<IllnessId>(DAY1)
    private val state = State(
        listOf(
            Storage(CALENDAR0),
            Storage(Character(CHARACTER_ID_0)),
            Storage(listOf(Illness(ILLNESS_ID_0), Illness(ILLNESS_ID_1, origin = origin), Illness(ILLNESS_ID_2))),
        ),
        data = Data(time = Time(currentDate = Day(10))),
    )
    private val creator = CreatedByCharacter(CHARACTER_ID_0)
    private val unknownCreator = CreatedByCharacter(UNKNOWN_CHARACTER_ID)

    @Nested
    inner class DeleteTest {
        val action = DeleteSpell(SPELL_ID_0)

        @Test
        fun `Used as parent in a combined origin`() {
            test(CombinedOrigin(setOf(SPELL_ID_0, SPELL_ID_2)))
        }

        @Test
        fun `Used as parent in a evolved origin`() {
            test(EvolvedOrigin(SPELL_ID_0))
        }

        @Test
        fun `Used as parent in a modified origin`() {
            test(ModifiedOrigin(SPELL_ID_0, UndefinedCreator))
        }

        @Test
        fun `Used as parent in a translated origin`() {
            test(TranslatedOrigin(SPELL_ID_0, UndefinedCreator))
        }

        private fun test(origin: Origin<SpellId>) {
            val spell1 = Spell(SPELL_ID_1, origin = origin)
            val state = state.updateStorage(Storage(listOf(Spell(SPELL_ID_0), spell1)))

            assertIllegalArgument("Cannot delete Spell 0, because it is used!") {
                REDUCER.invoke(state, action)
            }
        }

    }

    @Nested
    inner class UpdateTest {

        @Nested
        inner class DateTest {

            @Test
            fun `Date before parent's date`() {
                val origin = EvolvedOrigin(ILLNESS_ID_1, DAY0)
                failOrigin(origin, "The Illness 1 doesn't exist at the required date!")
            }

            @Test
            fun `Same date`() {
                val origin = EvolvedOrigin(ILLNESS_ID_1, DAY1)
                testOrigin(origin)
            }

            @Test
            fun `Later date`() {
                val origin = EvolvedOrigin(ILLNESS_ID_1, DAY2)
                testOrigin(origin)
            }
        }

        @Nested
        inner class CombinedOriginTest {

            @Test
            fun `Fails without parents`() {
                assertIllegalArgument("The combined origin needs at least 2 parents!") {
                    CombinedOrigin<IllnessId>(emptySet())
                }
            }

            @Test
            fun `Fails with 1 parent`() {
                assertIllegalArgument("The combined origin needs at least 2 parents!") {
                    CombinedOrigin(setOf(ILLNESS_ID_0))
                }
            }

            @Test
            fun `Fails with unknown parent`() {
                val origin = CombinedOrigin(setOf(UNKNOWN_ILLNESS_ID, ILLNESS_ID_1))
                failOrigin(origin, "Requires unknown Illness 99!")
            }

            @Test
            fun `Fails with reusing id as parent`() {
                val origin = CombinedOrigin(setOf(ILLNESS_ID_0, ILLNESS_ID_1))
                failOrigin(origin, "An element cannot be its own parent!")
            }

            @Test
            fun `Valid origin`() {
                testOrigin(CombinedOrigin(setOf(ILLNESS_ID_1, ILLNESS_ID_2)))
            }
        }

        @Nested
        inner class CreatedOriginTest {

            @Test
            fun `Unknown creator`() {
                failOrigin(CreatedOrigin(unknownCreator), "Cannot use an unknown Character 99 as Creator!")
            }

            @Test
            fun `Valid origin`() {
                testOrigin(CreatedOrigin(creator))
            }

        }

        @Nested
        inner class EvolvedOriginTest {

            @Test
            fun `Fails with unknown parent`() {
                val origin = EvolvedOrigin(UNKNOWN_ILLNESS_ID)
                failOrigin(origin, "Requires unknown Illness 99!")
            }

            @Test
            fun `Fails with reusing id as parent`() {
                val origin = EvolvedOrigin(ILLNESS_ID_0)
                failOrigin(origin, "An element cannot be its own parent!")
            }

            @Test
            fun `Valid origin`() {
                testOrigin(EvolvedOrigin(ILLNESS_ID_1))
            }
        }

        @Nested
        inner class ModifiedOriginTest {

            @Test
            fun `Fails with unknown parent`() {
                val origin = ModifiedOrigin(UNKNOWN_ILLNESS_ID, creator)
                failOrigin(origin, "Requires unknown Illness 99!")
            }

            @Test
            fun `Fails with reusing id as parent`() {
                val origin = ModifiedOrigin(ILLNESS_ID_0, creator)
                failOrigin(origin, "An element cannot be its own parent!")
            }

            @Test
            fun `Unknown creator`() {
                val origin = ModifiedOrigin(ILLNESS_ID_1, unknownCreator)
                failOrigin(origin, "Cannot use an unknown Character 99 as Modifier!")
            }

            @Test
            fun `Valid origin`() {
                testOrigin(ModifiedOrigin(ILLNESS_ID_1, creator))
            }
        }

        @Nested
        inner class TranslatedOriginTest {

            @Test
            fun `Fails with unknown parent`() {
                val origin = TranslatedOrigin(UNKNOWN_ILLNESS_ID, creator)
                failOrigin(origin, "Requires unknown Illness 99!")
            }

            @Test
            fun `Fails with reusing id as parent`() {
                val origin = TranslatedOrigin(ILLNESS_ID_0, creator)
                failOrigin(origin, "An element cannot be its own parent!")
            }

            @Test
            fun `Unknown creator`() {
                val origin = TranslatedOrigin(ILLNESS_ID_1, unknownCreator)
                failOrigin(origin, "Cannot use an unknown Character 99 as Translator!")
            }

            @Test
            fun `Valid origin`() {
                testOrigin(TranslatedOrigin(ILLNESS_ID_1, creator))
            }
        }

        private fun testOrigin(origin: Origin<IllnessId>) {
            val illness = Illness(ILLNESS_ID_0, origin = origin)
            val action = UpdateIllness(illness)

            val result = REDUCER.invoke(state, action).first

            assertEquals(
                illness,
                result.getIllnessStorage().getOrThrow(ILLNESS_ID_0)
            )
        }

        private fun failOrigin(origin: Origin<IllnessId>, message: String) {
            val illness = Illness(ILLNESS_ID_0, origin = origin)
            val action = UpdateIllness(illness)

            assertIllegalArgument(message) {
                REDUCER.invoke(state, action)
            }
        }
    }
}