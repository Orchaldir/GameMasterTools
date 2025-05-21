package at.orchaldir.gm.core.model.magic

import at.orchaldir.gm.core.model.SpellId
import at.orchaldir.gm.core.model.culture.language.LanguageId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.Creation
import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.HasOrigin
import at.orchaldir.gm.core.model.util.HasStartDate
import at.orchaldir.gm.core.model.util.Origin
import at.orchaldir.gm.core.model.util.UndefinedOrigin
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.util.source.HasDataSources
import kotlinx.serialization.Serializable

const val SPELL_TYPE = "Spell"

@Serializable
data class Spell(
    val id: SpellId,
    val name: Name = Name.init("Spell ${id.value}"),
    val language: LanguageId? = null,
    val origin: Origin<SpellId> = UndefinedOrigin(),
    val sources: Set<DataSourceId> = emptySet(),
) : ElementWithSimpleName<SpellId>, HasDataSources, HasOrigin<SpellId> {

    override fun id() = id
    override fun name() = name.text
    override fun origin() = origin
    override fun sources() = sources

}