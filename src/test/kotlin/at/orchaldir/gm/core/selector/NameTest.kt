package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.FamilyName
import at.orchaldir.gm.core.model.character.Mononym
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.culture.name.FamilyConvention
import at.orchaldir.gm.core.model.culture.name.NameOrder
import at.orchaldir.gm.core.model.culture.name.NameOrder.GivenNameFirst
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

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
            val state = State(
                characters = Storage(listOf(Character(ID0, FamilyName("A", null, "B")))),
                cultures = Storage(listOf(Culture(CULTURE0, namingConvention = FamilyConvention(GivenNameFirst))))
            )

            assertEquals("A B", state.getName(ID0))
        }

    }


}