package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.util.source.HasDataSources
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val CHARACTER_TEMPLATE_TYPE = "Character Template"

@JvmInline
@Serializable
value class CharacterTemplateId(val value: Int) : Id<CharacterTemplateId> {

    override fun next() = CharacterTemplateId(value + 1)
    override fun type() = CHARACTER_TEMPLATE_TYPE
    override fun value() = value

}

@Serializable
data class CharacterTemplate(
    val id: CharacterTemplateId,
    val name: Name = Name.init("Character Template ${id.value}"),
    val sources: Set<DataSourceId> = emptySet(),
) : ElementWithSimpleName<CharacterTemplateId>, HasDataSources {

    override fun id() = id

    override fun name() = name.text
    override fun sources() = sources
}