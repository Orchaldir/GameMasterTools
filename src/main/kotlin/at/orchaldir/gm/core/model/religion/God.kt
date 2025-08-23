package at.orchaldir.gm.core.model.religion

import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.character.PersonalityTraitId
import at.orchaldir.gm.core.model.util.HasStartDate
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.name.NotEmptyString
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.util.source.HasDataSources
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
    val name: Name = Name.init(id),
    val title: NotEmptyString? = null,
    val gender: Gender = Gender.Genderless,
    val personality: Set<PersonalityTraitId> = emptySet(),
    val domains: Set<DomainId> = emptySet(),
    val authenticity: Authenticity = UndefinedAuthenticity,
    val sources: Set<DataSourceId> = emptySet(),
) : ElementWithSimpleName<GodId>, HasDataSources, HasStartDate {

    override fun id() = id
    override fun name() = name.text
    override fun sources() = sources
    override fun startDate() = null

}