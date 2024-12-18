package at.orchaldir.gm.core.model.economy.job

import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val JOB = "Job"

@JvmInline
@Serializable
value class JobId(val value: Int) : Id<JobId> {

    override fun next() = JobId(value + 1)
    override fun type() = JOB
    override fun value() = value

}

@Serializable
data class Job(
    val id: JobId,
    val name: String = "Job ${id.value}",
) : ElementWithSimpleName<JobId> {

    override fun id() = id
    override fun name() = name

}