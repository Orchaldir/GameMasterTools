package utils.redux.middleware

import at.orchaldir.gm.utils.redux.DefaultStore
import at.orchaldir.gm.utils.redux.middleware.LogAction
import at.orchaldir.gm.utils.redux.noFollowUps
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LogActionMiddlewareTest {

    @Test
    fun `Test logging of actions`() {
        val store = DefaultStore<Int, Int>(
            10, { state,
                  action ->
                noFollowUps(state + action)
            },
            listOf(LogAction())
        )

        store.dispatch(3)

        assertEquals(13, store.getState())
    }

}