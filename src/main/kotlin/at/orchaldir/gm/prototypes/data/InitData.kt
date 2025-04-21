package at.orchaldir.gm.prototypes.data

import at.orchaldir.gm.core.model.ELEMENTS
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.createStorage
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    logger.info { "Command line args: $args" }
    val path = args[0]
    logger.info { "Path: $path" }

    val state = State(
        ELEMENTS.associateWith { createStorage(it) },
        path,
    )

    state.save()
}
