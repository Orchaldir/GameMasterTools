package at.orchaldir.gm.app.parse

import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.NAMES
import at.orchaldir.gm.core.model.name.NameList
import at.orchaldir.gm.core.model.name.NameListId
import io.ktor.http.*
import io.ktor.server.util.*

fun parseNameListId(
    parameters: Parameters,
    param: String,
) = NameListId(parseInt(parameters, param))

fun parseNameList(id: NameListId, parameters: Parameters): NameList {
    val name = parameters.getOrFail(NAME)
    val names = parameters.getOrFail(NAMES)
        .split('\n')
        .toList()

    return NameList(id, name, names)
}
