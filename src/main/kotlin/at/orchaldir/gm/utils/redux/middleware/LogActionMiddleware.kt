package at.orchaldir.gm.utils.redux.middleware

import at.orchaldir.gm.utils.redux.Dispatcher
import at.orchaldir.gm.utils.redux.Middleware
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class LogAction<Action, State> : Middleware<Action, State> {
    override fun invoke(
        dispatcher: Dispatcher<Action>,
        supplier: () -> State,
    ): Dispatcher<Action> {
        return { action ->
            logger.info("Dispatch $action")
            try {
                dispatcher(action)
            } catch (exception: Exception) {
                logger.warn { "Dispatch failed with: ${exception.message}" }
                throw exception
            }
        }
    }
}