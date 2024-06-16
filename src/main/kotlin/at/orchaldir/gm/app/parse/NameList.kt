package at.orchaldir.gm.app.parse

import at.orchaldir.gm.core.model.NameList
import at.orchaldir.gm.core.model.NameListId
import io.ktor.http.*
import io.ktor.server.util.*


fun parseNameList(id: NameListId, parameters: Parameters): NameList {
    val name = parameters.getOrFail("name")
    val names = parameters.getOrFail("names")
        .split('\n')
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .sorted()

    return NameList(id, name, names)
}
