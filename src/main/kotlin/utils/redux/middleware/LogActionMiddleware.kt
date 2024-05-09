package at.orchaldir.gm.utils.redux.middleware

import mu.KotlinLogging
import at.orchaldir.gm.utils.redux.Dispatcher
import at.orchaldir.gm.utils.redux.Middleware

private val logger = KotlinLogging.logger {}

class LogAction<Action, State> : Middleware<Action, State> {
    override fun invoke(
        dispatcher: Dispatcher<Action>,
        supplier: () -> State,
    ): Dispatcher<Action> {
        return { action ->
            logger.info("Dispatch $action")
            dispatcher(action)
        }
    }
}