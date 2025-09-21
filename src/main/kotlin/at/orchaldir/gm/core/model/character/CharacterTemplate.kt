package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.core.model.character.statistic.Statblock
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.culture.language.ComprehensionLevel
import at.orchaldir.gm.core.model.culture.language.LanguageId
import at.orchaldir.gm.core.model.item.UniformId
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.util.BeliefStatus
import at.orchaldir.gm.core.model.util.HasBelief
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.UndefinedBeliefStatus
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
    val race: RaceId,
    val gender: Gender? = null,
    val culture: CultureId? = null,
    val languages: Map<LanguageId, ComprehensionLevel> = emptyMap(),
    val belief: BeliefStatus = UndefinedBeliefStatus,
    val uniform: UniformId? = null,
    val statblock: Statblock = Statblock(),
    val sources: Set<DataSourceId> = emptySet(),
) : ElementWithSimpleName<CharacterTemplateId>, HasBelief, HasDataSources {

    override fun id() = id
    override fun name() = name.text
    override fun belief() = History(belief)
    override fun sources() = sources
}