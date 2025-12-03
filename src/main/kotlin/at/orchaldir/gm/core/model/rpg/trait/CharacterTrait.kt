package at.orchaldir.gm.core.model.rpg.trait

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing
import kotlinx.serialization.Serializable

const val CHARACTER_TRAIT_TYPE = "Character Trait"

@JvmInline
@Serializable
value class CharacterTraitId(val value: Int) : Id<CharacterTraitId> {

    override fun next() = CharacterTraitId(value + 1)
    override fun type() = CHARACTER_TRAIT_TYPE
    override fun value() = value

}

@JvmInline
@Serializable
value class CharacterTraitGroup(val value: Int)

@Serializable
data class CharacterTrait(
    val id: CharacterTraitId,
    val name: Name = Name.init(id),
    val group: CharacterTraitGroup? = null,
    val cost: Int = 0,
) : ElementWithSimpleName<CharacterTraitId> {

    override fun id() = id
    override fun name() = name.text
    override fun validate(state: State) = doNothing()

}