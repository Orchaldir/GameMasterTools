package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.character.Gender.*
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.culture.name.*
import at.orchaldir.gm.core.model.culture.name.GenonymicLookupDistance.OneGeneration
import at.orchaldir.gm.core.model.culture.name.GenonymicLookupDistance.TwoGenerations
import at.orchaldir.gm.core.model.culture.name.NameOrder.FamilyNameFirst
import at.orchaldir.gm.core.model.culture.name.NameOrder.GivenNameFirst
import at.orchaldir.gm.core.model.util.GenderMap
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val ID0 = CharacterId(0)
private val ID1 = CharacterId(1)
private val ID2 = CharacterId(2)
private val OTHER = CharacterId(10)
private val CULTURE0 = CultureId(0)
private val GENDER_MAP = GenderMap("f", "g", "m")

class NameTest {

    @Test
    fun `Get Mononym independent of culture`() {
        val state = State(Storage(Character(ID0, Mononym("Test"))))

        assertEquals("Test", state.getElementName(ID0))
    }

    @Nested
    inner class FamilyNameTest {

        @Test
        fun `Given name first`() {
            val state = init(GivenNameFirst, null)

            assertEquals("Given Family", state.getElementName(ID0))
        }

        @Test
        fun `Given name first with middle name`() {
            val state = init(GivenNameFirst, "Middle")

            assertEquals("Given Middle Family", state.getElementName(ID0))
        }

        @Test
        fun `Family name first`() {
            val state = init(FamilyNameFirst, null)

            assertEquals("Family Given", state.getElementName(ID0))
        }

        @Test
        fun `Family name first with middle name`() {
            val state = init(FamilyNameFirst, "Middle")

            assertEquals("Family Middle Given", state.getElementName(ID0))
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

                assertFailsWith<IllegalStateException> { state.getElementName(ID0) }
            }
        }

        private fun init(nameOrder: NameOrder, middle: String?) = init(FamilyConvention(nameOrder), middle)

        private fun init(convention: NamingConvention, middle: String?) = State(
            listOf(
                Storage(Character(ID0, FamilyName("Given", middle, "Family"))),
                Storage(Culture(CULTURE0, namingConvention = convention)),
            )
        )

    }

    @Nested
    inner class PatronymTest {

        @Test
        fun `Without a father`() {
            val state = State(
                listOf(
                    Storage(Character(ID0, Genonym("A"))),
                    Storage(Culture(CULTURE0, namingConvention = PatronymConvention())),
                )
            )

            assertEquals("A", state.getElementName(ID0))
        }

        @Nested
        inner class OneGenerationTest {
            @Test
            fun `Names Only style`() {
                val state = State(
                    listOf(
                        Storage(
                            listOf(
                                Character(ID0, Genonym("Child"), origin = Born(OTHER, ID1)),
                                Character(ID1, Genonym("Father"))
                            )
                        ),
                        Storage(Culture(CULTURE0, namingConvention = PatronymConvention())),
                    )
                )

                assertEquals("Child Father", state.getElementName(ID0))
            }

            @Test
            fun `Prefix style for son`() {
                val state = init(Male, PrefixStyle(GENDER_MAP))

                assertEquals("Child mFather", state.getElementName(ID0))
            }

            @Test
            fun `Prefix style for daughter`() {
                val state = init(Female, PrefixStyle(GENDER_MAP))

                assertEquals("Child fFather", state.getElementName(ID0))
            }

            @Test
            fun `Prefix style for child`() {
                val state = init(Genderless, PrefixStyle(GENDER_MAP))

                assertEquals("Child gFather", state.getElementName(ID0))
            }

            @Test
            fun `Suffix style for son`() {
                val state = init(Male, SuffixStyle(GENDER_MAP))

                assertEquals("Child Fatherm", state.getElementName(ID0))
            }

            @Test
            fun `Suffix style for daughter`() {
                val state = init(Female, SuffixStyle(GENDER_MAP))

                assertEquals("Child Fatherf", state.getElementName(ID0))
            }

            @Test
            fun `Suffix style for child`() {
                val state = init(Genderless, SuffixStyle(GENDER_MAP))

                assertEquals("Child Fatherg", state.getElementName(ID0))
            }

            private fun init(gender: Gender, style: GenonymicStyle) = State(
                listOf(
                    Storage(
                        listOf(
                            Character(ID0, Genonym("Child"), gender = gender, origin = Born(OTHER, ID1)),
                            Character(ID1, Genonym("Father"))
                        )
                    ),
                    Storage(
                        Culture(
                            CULTURE0, namingConvention = PatronymConvention(
                                style = style
                            )
                        )
                    )
                )
            )
        }

        @Test
        fun `Two generations`() {
            val state = State(
                listOf(
                    Storage(
                        listOf(
                            Character(ID0, Genonym("A"), gender = Female, origin = Born(OTHER, ID1)),
                            Character(ID1, Genonym("B"), gender = Male, origin = Born(OTHER, ID2)),
                            Character(ID2, Genonym("C"))
                        )
                    ),
                    Storage(
                        listOf(
                            Culture(
                                CULTURE0, namingConvention = PatronymConvention(
                                    TwoGenerations, ChildOfStyle(
                                        GENDER_MAP
                                    )
                                )
                            )
                        )
                    )
                )
            )

            assertEquals("A f B m C", state.getElementName(ID0))
        }
    }

    @Test
    fun `Test Matronym`() {
        val state = State(
            listOf(
                Storage(
                    listOf(
                        Character(ID0, Genonym("A"), gender = Male, origin = Born(ID1, OTHER)),
                        Character(ID1, Genonym("B"), gender = Female, origin = Born(ID2, OTHER)),
                        Character(ID2, Genonym("C"))
                    )
                ),
                Storage(
                    listOf(
                        Culture(
                            CULTURE0, namingConvention = MatronymConvention(
                                TwoGenerations, ChildOfStyle(
                                    GENDER_MAP
                                )
                            )
                        )
                    )
                )
            )
        )

        assertEquals("A m B f C", state.getElementName(ID0))
    }

    @Nested
    inner class GenonymTest {

        @Test
        fun `Use father with male`() {
            val state = init(Male)

            assertEquals("A m C", state.getElementName(ID0))
        }

        @Test
        fun `Use mother with female`() {
            val state = init(Female)

            assertEquals("A f B", state.getElementName(ID0))
        }

        private fun init(gender: Gender) = State(
            listOf(
                Storage(
                    listOf(
                        Character(ID0, Genonym("A"), gender = gender, origin = Born(ID1, ID2)),
                        Character(ID1, Genonym("B"), gender = Female),
                        Character(ID2, Genonym("C"), gender = gender)
                    )
                ),
                Storage(
                    listOf(
                        Culture(
                            CULTURE0, namingConvention = GenonymConvention(
                                OneGeneration, ChildOfStyle(
                                    GENDER_MAP
                                )
                            )
                        )
                    )
                )
            )
        )
    }

}