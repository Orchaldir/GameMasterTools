package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.FamilyName
import at.orchaldir.gm.core.model.character.Mononym
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
private val CULTURE0 = CultureId(0)

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
                val state = State(
                    characters = Storage(listOf(Character(ID0, FamilyName("Given", null, "Family")))),
                    cultures = Storage(listOf(Culture(CULTURE0, namingConvention = it)))
                )

                assertFailsWith<IllegalStateException> { state.getName(ID0) }
            }
        }

        private fun init(nameOrder: NameOrder, middle: String?) = State(
            characters = Storage(listOf(Character(ID0, FamilyName("Given", middle, "Family")))),
            cultures = Storage(listOf(Culture(CULTURE0, namingConvention = FamilyConvention(nameOrder))))
        )

    }


}