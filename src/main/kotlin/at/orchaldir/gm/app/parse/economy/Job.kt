package at.orchaldir.gm.app.parse.economy

import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.SPELLS
import at.orchaldir.gm.app.html.model.magic.parseSpellId
import at.orchaldir.gm.app.html.model.parseName
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.app.parse.parseSomeOf
import at.orchaldir.gm.core.model.economy.job.Job
import at.orchaldir.gm.core.model.economy.job.JobId
import io.ktor.http.*
import io.ktor.server.util.*

fun parseJobId(parameters: Parameters, param: String) = JobId(parseInt(parameters, param))

fun parseJobId(value: String) = JobId(value.toInt())

fun parseJob(id: JobId, parameters: Parameters) = Job(
    id,
    parseName(parameters),
    parseSomeOf(parameters, SPELLS, ::parseSpellId),
)
