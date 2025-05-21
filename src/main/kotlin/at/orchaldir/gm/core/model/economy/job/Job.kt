package at.orchaldir.gm.core.model.economy.job

import at.orchaldir.gm.core.model.SpellId
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.item.UniformId
import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.GenderMap
import at.orchaldir.gm.core.model.util.SomeOf
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val JOB_TYPE = "Job"

@JvmInline
@Serializable
value class JobId(val value: Int) : Id<JobId> {

    override fun next() = JobId(value + 1)
    override fun type() = JOB_TYPE
    override fun value() = value

}

@Serializable
data class Job(
    val id: JobId,
    val name: Name = Name.init("Job ${id.value}"),
    val employerType: EmployerType = EmployerType.Business,
    val income: Income = UndefinedIncome,
    val preferredGender: Gender? = null,
    val uniforms: GenderMap<UniformId?> = GenderMap(null),
    val spells: SomeOf<SpellId> = SomeOf(),
) : ElementWithSimpleName<JobId> {

    override fun id() = id
    override fun name() = name.text

}