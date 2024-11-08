package at.orchaldir.gm.app.parse

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.parse.world.parseTownId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.util.*
import io.ktor.http.*

fun parseOwnership(parameters: Parameters, state: State, startDate: Date) = History(
    parseOwner(parameters, OWNER),
    parsePreviousOwners(parameters, state, startDate),
)

private fun parsePreviousOwners(parameters: Parameters, state: State, startDate: Date): List<HistoryEntry> {
    val param = combine(OWNER, HISTORY)
    val count = parseInt(parameters, param, 0)
    var minDate = startDate.next()

    return (0..<count)
        .map {
            val previousOwner = parsePreviousOwner(parameters, state, combine(param, it), minDate)
            minDate = previousOwner.until.next()

            previousOwner
        }
}

private fun parsePreviousOwner(parameters: Parameters, state: State, param: String, minDate: Date) = HistoryEntry(
    parseOwner(parameters, param),
    parseDate(parameters, state, combine(param, DATE), minDate),
)

private fun parseOwner(parameters: Parameters, param: String): Owner = when (parameters[param]) {
    OwnerType.None.toString() -> NoOwner
    OwnerType.Character.toString() -> OwnedByCharacter(parseCharacterId(parameters, combine(param, CHARACTER)))
    OwnerType.Town.toString() -> OwnedByTown(parseTownId(parameters, combine(param, TOWN)))
    else -> UnknownOwner
}