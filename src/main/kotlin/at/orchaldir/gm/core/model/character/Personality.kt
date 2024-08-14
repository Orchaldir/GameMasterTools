package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val PERSONALITY_TRAIT = "PersonalityTrait"

@JvmInline
@Serializable
value class PersonalityTraitId(val value: Int) : Id<PersonalityTraitId> {

    override fun next() = PersonalityTraitId(value + 1)
    override fun type() = PERSONALITY_TRAIT
    override fun value() = value

}

@JvmInline
@Serializable
value class PersonalityTraitGroup(val value: Int)

@Serializable
data class PersonalityTrait(
    val id: PersonalityTraitId,
    val name: String = "Personality Trait ${id.value}",
    val group: PersonalityTraitGroup? = null,
) : Element<PersonalityTraitId> {

    override fun id() = id

}