package at.orchaldir.gm.app.parse.economy

import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.core.model.economy.job.Job
import at.orchaldir.gm.core.model.economy.job.JobId
import io.ktor.http.*
import io.ktor.server.util.*

fun parseJobId(parameters: Parameters, param: String) = JobId(parseInt(parameters, param))

fun parseJob(id: JobId, parameters: Parameters): Job {
    val name = parameters.getOrFail(NAME)

    return Job(id, name)
}
