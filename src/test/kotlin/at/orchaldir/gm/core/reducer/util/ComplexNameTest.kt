package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.CHARACTER_ID_0
import at.orchaldir.gm.CHARACTER_ID_1
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.name.NameWithReference
import at.orchaldir.gm.core.model.name.ReferencedFamilyName
import at.orchaldir.gm.core.model.name.ReferencedFullName
import at.orchaldir.gm.core.model.name.SimpleName
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private val STATE = State(
    listOf(
        Storage(Character(CHARACTER_ID_0)),
    )
)

class ComplexNameTest {

    @Test
    fun `A valid simple`() {
        checkComplexName(STATE, SimpleName("Test"))
    }

    @Test
    fun `A simple name must not be empty`() {
        assertIllegalArgument("A simple name must not be empty!") {
            checkComplexName(STATE, SimpleName(""))
        }
    }

    @Nested
    inner class NameWithReferenceTest {

        @Test
        fun `The prefix & the postfix must not be empty at the same time`() {
            assertIllegalArgument("The prefix & the postfix of the name with reference must not be empty at the same time!") {
                checkComplexName(STATE, NameWithReference(ReferencedFullName(CHARACTER_ID_0), null, null))
            }
        }

        @Test
        fun `The character referenced for its family name doesn't exist`() {
            assertIllegalArgument("Reference for complex name is unknown!") {
                checkComplexName(STATE, NameWithReference(ReferencedFamilyName(CHARACTER_ID_1), "test", null))
            }
        }

        @Test
        fun `A valid family name`() {
            checkComplexName(STATE, NameWithReference(ReferencedFamilyName(CHARACTER_ID_0), "test", null))
        }

        @Test
        fun `The character referenced for its full name doesn't exist`() {
            assertIllegalArgument("Reference for complex name is unknown!") {
                checkComplexName(STATE, NameWithReference(ReferencedFullName(CHARACTER_ID_1), null, "test"))
            }
        }

        @Test
        fun `A valid full name`() {
            checkComplexName(STATE, NameWithReference(ReferencedFullName(CHARACTER_ID_0), null, "test"))
        }
    }


}