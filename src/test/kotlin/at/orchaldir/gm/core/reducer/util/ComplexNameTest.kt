package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.CHARACTER_ID_0
import at.orchaldir.gm.MOON_ID_0
import at.orchaldir.gm.MOUNTAIN_ID_0
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.name.*
import at.orchaldir.gm.core.model.world.moon.Moon
import at.orchaldir.gm.core.model.world.terrain.Mountain
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private val STATE = State(
    listOf(
        Storage(Character(CHARACTER_ID_0)),
        Storage(Moon(MOON_ID_0)),
        Storage(Mountain(MOUNTAIN_ID_0)),
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
            val state = STATE.removeStorage(CHARACTER_ID_0)

            assertIllegalArgument("Reference for complex name is unknown!") {
                checkComplexName(state, NameWithReference(ReferencedFamilyName(CHARACTER_ID_0), "test", null))
            }
        }

        @Test
        fun `A valid family name`() {
            checkComplexName(STATE, NameWithReference(ReferencedFamilyName(CHARACTER_ID_0), "test", null))
        }

        @Test
        fun `The character referenced for its full name doesn't exist`() {
            val state = STATE.removeStorage(CHARACTER_ID_0)

            assertIllegalArgument("Reference for complex name is unknown!") {
                checkComplexName(state, NameWithReference(ReferencedFullName(CHARACTER_ID_0), null, "test"))
            }
        }

        @Test
        fun `A valid full name`() {
            checkComplexName(STATE, NameWithReference(ReferencedFullName(CHARACTER_ID_0), null, "test"))
        }

        @Test
        fun `The referenced moon doesn't exist`() {
            val state = STATE.removeStorage(MOON_ID_0)

            assertIllegalArgument("Reference for complex name is unknown!") {
                checkComplexName(state, NameWithReference(ReferencedMoon(MOON_ID_0), "a", "b"))
            }
        }

        @Test
        fun `A valid moon`() {
            checkComplexName(STATE, NameWithReference(ReferencedMoon(MOON_ID_0), "a", "b"))
        }

        @Test
        fun `The referenced mountain doesn't exist`() {
            val state = STATE.removeStorage(MOUNTAIN_ID_0)

            assertIllegalArgument("Reference for complex name is unknown!") {
                checkComplexName(state, NameWithReference(ReferencedMountain(MOUNTAIN_ID_0), "a", "b"))
            }
        }

        @Test
        fun `A valid mountain`() {
            checkComplexName(STATE, NameWithReference(ReferencedMountain(MOUNTAIN_ID_0), "a", "b"))
        }
    }


}