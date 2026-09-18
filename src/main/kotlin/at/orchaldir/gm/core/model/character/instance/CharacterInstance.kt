package at.orchaldir.gm.core.model.character.instance

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.character.appearance.UndefinedAppearance
import at.orchaldir.gm.core.model.item.equipment.EquipmentIdMap
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.rpg.statblock.Statblock
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing
import kotlinx.serialization.Serializable

const val CHARACTER_INSTANCE_TYPE = "Character Instance"

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
    val base: BaseOfInstance,
    val race: RaceId,
    val gender: Gender = Gender.Genderless,
    val appearance: Appearance = UndefinedAppearance,
    val statblock: Statblock = Statblock(),
    val equipped: EquipmentIdMap = EquipmentIdMap(),
) : ElementWithSimpleName<CharacterInstanceId> {

    override fun id() = id
    override fun name() = name.text

    override fun validate(state: State) = doNothing()
}