package at.orchaldir.gm.core.model.religion

import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.model.magic.SpellId
import at.orchaldir.gm.core.model.util.SomeOf
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val DOMAIN_TYPE = "Domain"

@JvmInline
@Serializable
value class DomainId(val value: Int) : Id<DomainId> {

    override fun next() = DomainId(value + 1)
    override fun type() = DOMAIN_TYPE
    override fun value() = value

}

@Serializable
data class Domain(
    val id: DomainId,
    val name: Name = Name.init(id),
    val spells: SomeOf<SpellId> = SomeOf(),
    val jobs: Set<JobId> = emptySet(),
) : ElementWithSimpleName<DomainId> {

    override fun id() = id
    override fun name() = name.text

}