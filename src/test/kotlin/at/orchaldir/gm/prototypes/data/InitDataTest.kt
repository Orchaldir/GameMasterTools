package at.orchaldir.gm.prototypes.data

import org.junit.jupiter.api.Test

class InitDataTest {

    @Test
    fun `Default state is valid`() {
        val state = createDefaultState("")

        state.validate()
    }

}