package at.orchaldir.gm.core.model.religion

import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.character.PersonalityTraitId
import at.orchaldir.gm.core.model.name.Name
import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.HasStartDate
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val GOD_TYPE = "God"

@JvmInline
@Serializable
value class GodId(val value: Int) : Id<GodId> {

    override fun next() = GodId(value + 1)
    override fun type() = GOD_TYPE
    override fun value() = value

}

@Serializable
data class God(
    val id: GodId,
    val name: Name = Name.init("God ${id.value}"),
    val title: String? = null,
    val gender: Gender = Gender.Genderless,
    val personality: Set<PersonalityTraitId> = emptySet(),
    val domains: Set<DomainId> = emptySet(),
) : ElementWithSimpleName<GodId>, HasStartDate {

    override fun id() = id
    override fun name() = name.text

    override fun startDate() = null

}