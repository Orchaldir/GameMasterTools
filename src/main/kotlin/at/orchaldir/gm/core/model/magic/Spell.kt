package at.orchaldir.gm.core.model.magic

import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.core.model.name.Name
import at.orchaldir.gm.core.model.source.DataSourceId
import at.orchaldir.gm.core.model.source.HasDataSources
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.Created
import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.HasStartDate
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val SPELL_TYPE = "Spell"

@JvmInline
@Serializable
value class SpellId(val value: Int) : Id<SpellId> {

    override fun next() = SpellId(value + 1)
    override fun type() = SPELL_TYPE
    override fun value() = value

}

@Serializable
data class Spell(
    val id: SpellId,
    val name: Name = Name.init("Spell ${id.value}"),
    val date: Date? = null,
    val language: LanguageId? = null,
    val origin: SpellOrigin = UndefinedSpellOrigin,
    val sources: Set<DataSourceId> = emptySet(),
) : ElementWithSimpleName<SpellId>, Created, HasDataSources, HasStartDate {

    override fun id() = id
    override fun name() = name.text
    override fun creator() = origin.creator()
    override fun sources() = sources
    override fun startDate() = date

}