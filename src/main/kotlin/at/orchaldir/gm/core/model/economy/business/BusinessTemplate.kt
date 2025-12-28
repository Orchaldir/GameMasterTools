package at.orchaldir.gm.core.model.economy.business

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.util.source.HasDataSources
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val BUSINESS_TEMPLATE_TYPE = "Business Template"

@JvmInline
@Serializable
value class BusinessTemplateId(val value: Int) : Id<BusinessTemplateId> {

    override fun next() = BusinessTemplateId(value + 1)
    override fun type() = BUSINESS_TEMPLATE_TYPE
    override fun value() = value

}

@Serializable
data class BusinessTemplate(
    val id: BusinessTemplateId,
    val name: Name = Name.init(id),
    val sources: Set<DataSourceId> = emptySet(),
) : ElementWithSimpleName<BusinessTemplateId>, HasDataSources {

    override fun id() = id
    override fun name() = name.text
    override fun sources() = sources

    override fun validate(state: State) {
    }

}