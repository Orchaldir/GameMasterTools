package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.origin.BornElement
import at.orchaldir.gm.core.model.util.origin.CombinedElement
import at.orchaldir.gm.core.model.util.origin.EvolvedElement
import at.orchaldir.gm.core.model.util.origin.ModifiedElement
import at.orchaldir.gm.core.model.util.origin.Origin
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test


class OriginTest {
    private val state = State(
        listOf(
            Storage(CALENDAR0),
            Storage(
                listOf(
                    Character(CHARACTER_ID_0),
                    Character(CHARACTER_ID_1, gender = Gender.Male, birthDate = DAY1),
                    Character(CHARACTER_ID_2, gender = Gender.Female, birthDate = DAY1),
                )
            ),
        )
    )

    @Nested
    inner class BornElementTest {

        @Test
        fun `Valid parents`() {
            test(BornElement(CHARACTER_ID_2, CHARACTER_ID_1))
        }

        @Test
        fun `No Parents`() {
            test(BornElement())
        }

        @Test
        fun `Mother is not female`() {
            val origin = BornElement(CHARACTER_ID_1, null)

            assertIllegalArgument("Mother 1 is not Female!") {
                test(origin)
            }
        }

        @Test
        fun `Father is not male`() {
            val origin = BornElement(null, CHARACTER_ID_2)

            assertIllegalArgument("Father 2 is not Male!") {
                test(origin)
            }
        }
    }

    @Nested
    inner class CombinedElementTest {

        @Test
        fun `Valid parents`() {
            test(CombinedElement.init(setOf(CHARACTER_ID_2, CHARACTER_ID_1)))
        }

        @Test
        fun `No Parents`() {
            test(CombinedElement())
        }
    }

    @Nested
    inner class ParentMustExistTest {

        @Test
        fun `Unknown father`() {
            test(BornElement(null, UNKNOWN_CHARACTER_ID))
        }

        @Test
        fun `Unknown mother`() {
            test(BornElement(UNKNOWN_CHARACTER_ID, null))
        }

        @Test
        fun `Combined element`() {
            test(CombinedElement.init(setOf(UNKNOWN_CHARACTER_ID)))
        }

        @Test
        fun `Evolved element`() {
            test(EvolvedElement(UNKNOWN_CHARACTER_ID))
        }

        @Test
        fun `Modified element`() {
            test(ModifiedElement(UNKNOWN_CHARACTER_ID))
        }

        private fun test(origin: Origin) {
            assertIllegalArgument("Requires unknown parent Character 99!") {
                checkOrigin(state, CHARACTER_ID_0, origin, null, ::CharacterId)
            }
        }
    }

    @Nested
    inner class ParentMustExistAtThatDateTest {

        @Test
        fun `Test father`() {
            test(BornElement(null, CHARACTER_ID_1))
        }

        @Test
        fun `Test mother`() {
            test(BornElement(CHARACTER_ID_2, null), "Character 2")
        }

        @Test
        fun `Combined element`() {
            test(CombinedElement.init(setOf(CHARACTER_ID_1)))
        }

        @Test
        fun `Evolved element`() {
            test(EvolvedElement(CHARACTER_ID_1))
        }

        @Test
        fun `Modified element`() {
            test(ModifiedElement(CHARACTER_ID_1))
        }

        private fun test(origin: Origin, noun: String = "Character 1") {
            assertIllegalArgument("The parent $noun doesn't exist at the required date!") {
                checkOrigin(state, CHARACTER_ID_0, origin, DAY0, ::CharacterId)
            }
        }
    }

    @Nested
    inner class CannotBeYourOwnParentTest {

        @Test
        fun `A born element cannot be their own mother`() {
            test(BornElement(CHARACTER_ID_0, null))
        }

        @Test
        fun `A born element cannot be their own father`() {
            test(BornElement(null, CHARACTER_ID_0))
        }

        @Test
        fun `Combined element`() {
            test(CombinedElement.init(setOf(CHARACTER_ID_0)))
        }

        @Test
        fun `Evolved element`() {
            test(EvolvedElement(CHARACTER_ID_0))
        }

        @Test
        fun `Modified element`() {
            test(ModifiedElement(CHARACTER_ID_0))
        }

        private fun test(origin: Origin) {
            assertIllegalArgument("Character 0 cannot be its own parent!") {
                checkOrigin(state, CHARACTER_ID_0, origin, null, ::CharacterId)
            }
        }

    }

    private fun test(origin: Origin) {
        checkOrigin(state, CHARACTER_ID_0, origin, null, ::CharacterId)
    }

}