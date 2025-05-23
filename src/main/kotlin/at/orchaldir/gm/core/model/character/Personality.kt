package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val PERSONALITY_TRAIT_TYPE = "Personality Trait"

@JvmInline
@Serializable
value class PersonalityTraitId(val value: Int) : Id<PersonalityTraitId> {

    override fun next() = PersonalityTraitId(value + 1)
    override fun type() = PERSONALITY_TRAIT_TYPE
    override fun value() = value

}

@JvmInline
@Serializable
value class PersonalityTraitGroup(val value: Int)

@Serializable
data class PersonalityTrait(
    val id: PersonalityTraitId,
    val name: Name = Name.init("Personality Trait ${id.value}"),
    val group: PersonalityTraitGroup? = null,
) : ElementWithSimpleName<PersonalityTraitId> {

    override fun id() = id
    override fun name() = name.text

}