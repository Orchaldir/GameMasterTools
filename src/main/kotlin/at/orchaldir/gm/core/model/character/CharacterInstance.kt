package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.character.appearance.UndefinedAppearance
import at.orchaldir.gm.core.model.item.equipment.EquipmentIdMap
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.realm.ALLOWED_WAR_PARTICIPANTS
import at.orchaldir.gm.core.model.rpg.statblock.Statblock
import at.orchaldir.gm.core.model.util.NoReference
import at.orchaldir.gm.core.model.util.Reference
import at.orchaldir.gm.core.model.util.ReferenceType
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.reducer.character.validateEquipmentMap
import at.orchaldir.gm.core.reducer.character.validateEquipped
import at.orchaldir.gm.core.reducer.race.validateRaceLookup
import at.orchaldir.gm.core.reducer.rpg.validateStatblock
import at.orchaldir.gm.core.reducer.rpg.validateStatblockLookup
import at.orchaldir.gm.core.reducer.util.checkBeliefStatus
import at.orchaldir.gm.core.reducer.util.validateReference
import at.orchaldir.gm.core.selector.rpg.statblock.getStatblock
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing
import kotlinx.serialization.Serializable

const val CHARACTER_INSTANCE_TYPE = "Character Instance"

val ALLOWED_BASED_ON_TYPES = listOf(
    ReferenceType.None,
    ReferenceType.Character,
    ReferenceType.CharacterTemplate,
)

@JvmInline
@Serializable
value class CharacterInstanceId(val value: Int) : Id<CharacterInstanceId> {

    override fun next() = CharacterInstanceId(value + 1)
    override fun type() = CHARACTER_INSTANCE_TYPE
    override fun value() = value

}

@Serializable
data class CharacterInstance(
    val id: CharacterInstanceId,
    val name: Name = Name.init(id),
    val basedOn: Reference = NoReference,
    val race: RaceId,
    val gender: Gender = Gender.Genderless,
    val appearance: Appearance = UndefinedAppearance,
    val statblock: Statblock = Statblock(),
    val equipped: EquipmentIdMap = EquipmentIdMap(),
) : ElementWithSimpleName<CharacterInstanceId> {

    override fun id() = id
    override fun name() = name.text

    override fun validate(state: State) {
        validateReference(state, basedOn, null, "Base", ALLOWED_BASED_ON_TYPES)
        val race = state.getRaceStorage().getOrThrow(race)
        require(race.genders.contains(gender)) { "Gender $gender not allowed by ${this.race.print()}!" }
        validateEquipmentMap(state, equipped)
        validateStatblock(state, statblock)
    }
}