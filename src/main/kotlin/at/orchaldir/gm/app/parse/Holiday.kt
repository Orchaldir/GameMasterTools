package at.orchaldir.gm.app.parse

import at.orchaldir.gm.core.model.holiday.Holiday
import at.orchaldir.gm.core.model.holiday.HolidayId
import io.ktor.http.*
import io.ktor.server.util.*

fun parseHolidayId(
    parameters: Parameters,
    param: String,
) = HolidayId(parseInt(parameters, param))

fun parseHoliday(id: HolidayId, parameters: Parameters): Holiday {
    val name = parameters.getOrFail(NAME)

    return Holiday(
        id,
        name,
    )
}