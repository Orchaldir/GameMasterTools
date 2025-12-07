package at.orchaldir.gm.core.model.religion

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.ALLOWED_CAUSES_OF_DEATH_FOR_CHARACTER
import at.orchaldir.gm.core.model.character.ALLOWED_VITAL_STATUS_FOR_CHARACTER
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.rpg.trait.CharacterTraitId
import at.orchaldir.gm.core.model.rpg.trait.CharacterTraitType
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.name.NotEmptyString
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.util.source.HasDataSources
import at.orchaldir.gm.core.reducer.util.checkAuthenticity
import at.orchaldir.gm.core.reducer.util.validateHasStartAndEnd
import at.orchaldir.gm.core.reducer.util.validateVitalStatus
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val GOD_TYPE = "God"
val ALLOWED_GOD_AUTHENTICITY = listOf(
    AuthenticityType.Undefined,
    AuthenticityType.Authentic,
    AuthenticityType.Invented,
    AuthenticityType.Mask,
)
val ALLOWED_VITAL_STATUS_FOR_GOD = ALLOWED_VITAL_STATUS_FOR_CHARACTER
val ALLOWED_CAUSES_OF_DEATH_FOR_GOD = ALLOWED_CAUSES_OF_DEATH_FOR_CHARACTER

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
    val status: VitalStatus = Alive,
    val personality: Set<CharacterTraitId> = emptySet(),
    val domains: Set<DomainId> = emptySet(),
    val authenticity: Authenticity = UndefinedAuthenticity,
    val sources: Set<DataSourceId> = emptySet(),
) : ElementWithSimpleName<GodId>, HasDataSources, HasVitalStatus {

    override fun id() = id
    override fun name() = name.text
    override fun sources() = sources
    override fun startDate() = null
    override fun vitalStatus() = status

    override fun validate(state: State) {
        validateVitalStatus(
            state,
            id,
            status,
            null,
            ALLOWED_VITAL_STATUS_FOR_GOD,
            ALLOWED_CAUSES_OF_DEATH_FOR_GOD,
        )
        validateHasStartAndEnd(state, this)
        state.getDomainStorage().require(domains)
        state.getCharacterTraitStorage().getOrThrow(personality)
            .forEach { require(it.type == CharacterTraitType.Personality) { "${it.id.print()} has type other than Personality!" } }
        checkAuthenticity(state, authenticity)
        state.getDataSourceStorage().require(sources)
    }

}