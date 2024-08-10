package at.orchaldir.gm.core

import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
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
const val VERSION = 1

@Serializable
data class Data<I, E>(val version: Int, val elements: Map<I, E>)

inline fun <reified ID : Id<ID>, reified ELEMENT : Element<ID>> saveStorage(
    path: String,
    storage: Storage<ID, ELEMENT>,
) {
    val data = Data(VERSION, storage.getAll().associateBy { it.id() })

    saveData(path, storage.name + "s", data)
}

inline fun <reified T> saveData(
    path: String,
    name: String,
    data: T,
) {
    logger.info { "save(): $name" }

    File("$path/$name.json").writeText(prettyJson.encodeToString(data))
}

inline fun <reified ID : Id<ID>, reified ELEMENT : Element<ID>> loadStorage(
    path: String,
    type: String,
    zero: ID,
): Storage<ID, ELEMENT> {
    logger.info { "load(): ${type}s" }

    val string = File("$path/${type}s.json").readText()
    val data = prettyJson.decodeFromString<Data<ID, ELEMENT>>(string)

    if (data.elements.isEmpty()) {
        return Storage(zero, type)
    }

    return Storage(data.elements.values.toList(), type)
}
