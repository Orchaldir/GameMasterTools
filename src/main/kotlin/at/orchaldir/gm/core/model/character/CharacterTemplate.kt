package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.culture.language.ComprehensionLevel
import at.orchaldir.gm.core.model.culture.language.LanguageId
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.race.RaceLookup
import at.orchaldir.gm.core.model.rpg.statblock.StatblockLookup
import at.orchaldir.gm.core.model.rpg.statblock.UndefinedStatblockLookup
import at.orchaldir.gm.core.model.util.BeliefStatus
import at.orchaldir.gm.core.model.util.HasBelief
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.UndefinedBeliefStatus
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.util.source.HasDataSources
import at.orchaldir.gm.core.reducer.character.validateEquipped
import at.orchaldir.gm.core.reducer.race.validateRaceLookup
import at.orchaldir.gm.core.reducer.rpg.validateStatblockLookup
import at.orchaldir.gm.core.reducer.util.checkBeliefStatus
import at.orchaldir.gm.core.selector.rpg.statblock.getStatblock
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
    val name: Name = Name.init(id),
    val race: RaceLookup,
    val gender: Gender? = null,
    val culture: CultureId? = null,
    val languages: Map<LanguageId, ComprehensionLevel> = emptyMap(),
    val belief: BeliefStatus = UndefinedBeliefStatus,
    val statblock: StatblockLookup = UndefinedStatblockLookup,
    val equipped: Equipped = UndefinedEquipped,
    val sources: Set<DataSourceId> = emptySet(),
) : ElementWithSimpleName<CharacterTemplateId>, HasBelief, HasDataSources {

    override fun id() = id
    override fun name() = name.text
    override fun belief() = History(belief)
    override fun sources() = sources

    override fun clone(cloneId: CharacterTemplateId) =
        copy(id = cloneId, name = Name.init("Clone ${cloneId.value}"))

    override fun validate(state: State) {
        state.getCultureStorage().requireOptional(culture)
        state.getDataSourceStorage().require(sources)
        state.getLanguageStorage().require(languages.keys)
        validateRaceLookup(state, race)
        validateEquipped(state, equipped, statblock)
        validateStatblockLookup(state, state.getStatblock(race), statblock)
        checkBeliefStatus(state, belief)
    }
}