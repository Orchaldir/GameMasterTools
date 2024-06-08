package at.orchaldir.gm.core

import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import java.io.File

val logger = KotlinLogging.logger {}
@OptIn(ExperimentalSerializationApi::class)
val prettyJson = Json {
    prettyPrint = true
    prettyPrintIndent = " "
}

inline fun <reified ID : Id<ID>, reified ELEMENT : Element<ID>> save(storage: Storage<ID, ELEMENT>) {
    logger.info { "save(): ${storage.name}s" }

    File("data/${storage.name}s.json").writeText(prettyJson.encodeToString(storage))
}
