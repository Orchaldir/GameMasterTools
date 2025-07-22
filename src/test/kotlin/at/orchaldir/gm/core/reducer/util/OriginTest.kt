package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.origin.BornElement
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test


class OriginTest {

    @Nested
    inner class BornElementTest {
        private val state = State(
            listOf(
                Storage(CALENDAR0),
                Storage(
                    listOf(
                        Character(CHARACTER_ID_0),
                        Character(CHARACTER_ID_1, gender = Gender.Male),
                        Character(CHARACTER_ID_2, gender = Gender.Female),
                    )
                ),
            )
        )

        @Test
        fun `Valid parents`() {
            val origin = BornElement(CHARACTER_ID_2, CHARACTER_ID_1)

            test(origin, null)
        }

        @Test
        fun `Parents are undefined`() {
            val origin = BornElement()

            test(origin, null)
        }

        @Test
        fun `Unknown mother`() {
            val origin = BornElement(UNKNOWN_CHARACTER_ID, null)

            assertIllegalArgument("Requires unknown parent Character 99!") {
                test(origin, null)
            }
        }

        @Test
        fun `Mother is not female`() {
            val origin = BornElement(CHARACTER_ID_1, null)

            assertIllegalArgument("Mother 1 is not Female!") {
                test(origin, null)
            }
        }

        @Test
        fun `Unknown father`() {
            val origin = BornElement(null, UNKNOWN_CHARACTER_ID)

            assertIllegalArgument("Requires unknown parent Character 99!") {
                test(origin, null)
            }
        }

        @Test
        fun `Father is not male`() {
            val origin = BornElement(null, CHARACTER_ID_2)

            assertIllegalArgument("Father 2 is not Male!") {
                test(origin, null)
            }
        }

        private fun test(origin: BornElement, date: Date?) {
            checkOrigin(state, CHARACTER_ID_0, origin, date, ::CharacterId)
        }
    }

}