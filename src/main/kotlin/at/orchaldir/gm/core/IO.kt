package at.orchaldir.gm.core

import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mu.KotlinLogging

val logger = KotlinLogging.logger {}

inline fun <reified ID : Id<ID>, reified ELEMENT : Element<ID>> save(storage: Storage<ID, ELEMENT>) {
    logger.info { "save(): " }
    logger.info { Json.encodeToString(storage) }
}
