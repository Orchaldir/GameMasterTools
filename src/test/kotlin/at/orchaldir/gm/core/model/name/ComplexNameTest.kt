package at.orchaldir.gm.core.model.name

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.FamilyName
import at.orchaldir.gm.core.model.character.Mononym
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.name.FamilyConvention
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import kotlin.test.Test

private val STATE = State(
    listOf(
        Storage(
            listOf(
                Character(CHARACTER_ID_0, FamilyName("Given0", "Middle0", "Family0")),
                Character(CHARACTER_ID_1, Mononym("Mononym0")),
            )
        ),
        Storage(Culture(CULTURE_ID_0, namingConvention = FamilyConvention())),
    )
)

class ComplexNameTest {

    @Test
    fun `Test simple name`() {
        assertEquals("Simple0", SimpleName("Simple0").resolve(STATE))
    }

    @Nested
    inner class NameWithReferenceTest {
        @Test
        fun `Reference full name with family name`() {
            val name = NameWithReference(ReferencedFullName(CHARACTER_ID_0), "Pre0", "Post0")

            assertEquals("Pre0 Given0 Middle0 Family0 Post0", name.resolve(STATE))
        }
    }

}