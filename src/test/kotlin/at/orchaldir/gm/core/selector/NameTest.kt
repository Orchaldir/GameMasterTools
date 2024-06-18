package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.appearance.GenderMap
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.character.Gender.*
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.culture.name.*
import at.orchaldir.gm.core.model.culture.name.NameOrder.FamilyNameFirst
import at.orchaldir.gm.core.model.culture.name.NameOrder.GivenNameFirst
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val ID0 = CharacterId(0)
private val ID1 = CharacterId(1)
private val OTHER = CharacterId(2)
private val CULTURE0 = CultureId(0)
private val GENDER_MAP = GenderMap("f", "g", "m")

class NameTest {

    @Test
    fun `Get Mononym independent of culture`() {
        val state = State(characters = Storage(listOf(Character(ID0, Mononym("Test")))))

        assertEquals("Test", state.getName(ID0))
    }

    @Nested
    inner class FamilyNameTest {

        @Test
        fun `Given name first`() {
            val state = init(GivenNameFirst, null)

            assertEquals("Given Family", state.getName(ID0))
        }

        @Test
        fun `Given name first with middle name`() {
            val state = init(GivenNameFirst, "Middle")

            assertEquals("Given Middle Family", state.getName(ID0))
        }

        @Test
        fun `Family name first`() {
            val state = init(FamilyNameFirst, null)

            assertEquals("Family Given", state.getName(ID0))
        }

        @Test
        fun `Family name first with middle name`() {
            val state = init(FamilyNameFirst, "Middle")

            assertEquals("Family Middle Given", state.getName(ID0))
        }

        @Test
        fun `Family name is incompatible with other conventions`() {
            listOf(
                NoNamingConvention,
                MononymConvention(),
                PatronymConvention(),
                MatronymConvention(),
                GenonymConvention()
            ).forEach {
                val state = init(it, null)

                assertFailsWith<IllegalStateException> { state.getName(ID0) }
            }
        }

        private fun init(nameOrder: NameOrder, middle: String?) = init(FamilyConvention(nameOrder), middle)

        private fun init(convention: NamingConvention, middle: String?) = State(
            characters = Storage(listOf(Character(ID0, FamilyName("Given", middle, "Family")))),
            cultures = Storage(listOf(Culture(CULTURE0, namingConvention = convention)))
        )

    }

    @Nested
    inner class PatronymTest {

        @Test
        fun `Without a father`() {
            val state = State(
                characters = Storage(listOf(Character(ID0, Genonym("A")))),
                cultures = Storage(listOf(Culture(CULTURE0, namingConvention = PatronymConvention())))
            )

            assertEquals("A", state.getName(ID0))
        }

        @Nested
        inner class OneGenerationTest {
            @Test
            fun `Names Only style`() {
                val state = State(
                    characters = Storage(
                        listOf(
                            Character(ID0, Genonym("Child"), origin = Born(OTHER, ID1)),
                            Character(ID1, Genonym("Father"))
                        )
                    ),
                    cultures = Storage(listOf(Culture(CULTURE0, namingConvention = PatronymConvention())))
                )

                assertEquals("Child Father", state.getName(ID0))
            }

            @Test
            fun `Prefix style for son`() {
                val state = init(Male, PrefixStyle(GENDER_MAP))

                assertEquals("Child mFather", state.getName(ID0))
            }

            @Test
            fun `Prefix style for daughter`() {
                val state = init(Female, PrefixStyle(GENDER_MAP))

                assertEquals("Child fFather", state.getName(ID0))
            }

            @Test
            fun `Prefix style for child`() {
                val state = init(Genderless, PrefixStyle(GENDER_MAP))

                assertEquals("Child gFather", state.getName(ID0))
            }

            @Test
            fun `Suffix style for son`() {
                val state = init(Male, SuffixStyle(GENDER_MAP))

                assertEquals("Child Fatherm", state.getName(ID0))
            }

            @Test
            fun `Suffix style for daughter`() {
                val state = init(Female, SuffixStyle(GENDER_MAP))

                assertEquals("Child Fatherf", state.getName(ID0))
            }

            @Test
            fun `Suffix style for child`() {
                val state = init(Genderless, SuffixStyle(GENDER_MAP))

                assertEquals("Child Fatherg", state.getName(ID0))
            }

            private fun init(gender: Gender, style: GenonymicStyle) = State(
                characters = Storage(
                    listOf(
                        Character(ID0, Genonym("Child"), gender = gender, origin = Born(OTHER, ID1)),
                        Character(ID1, Genonym("Father"))
                    )
                ),
                cultures = Storage(
                    listOf(
                        Culture(
                            CULTURE0, namingConvention = PatronymConvention(
                                style = style
                            )
                        )
                    )
                )
            )
        }
    }


}