package at.orchaldir.gm.core.model.name

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.FamilyName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class ComplexNameType {
    Simple,
    Reference,
}

@Serializable
sealed class ComplexName {

    abstract fun resolve(state: State): String

    fun getType() = when (this) {
        is NameWithReference -> ComplexNameType.Reference
        is SimpleName -> ComplexNameType.Simple
    }
}

@Serializable
@SerialName("Simple")
data class SimpleName(val name: String) : ComplexName() {

    override fun resolve(state: State) = name

}

@Serializable
@SerialName("Reference")
data class NameWithReference(
    val reference: ReferenceForName,
    val prefix: String?,
    val postfix: String?,
) : ComplexName() {

    override fun resolve(state: State): String {
        val referencedName = when (reference) {
            is ReferencedFamilyName -> {
                val character = state.getCharacterStorage().getOrThrow(reference.id)
                when (character.name) {
                    is FamilyName -> character.name.family
                    else -> error("A referenced family name requires a family name!")
                }
            }

            is ReferencedFullName -> state.getCharacterStorage().getOrThrow(reference.id).name(state)
            is ReferencedMoon -> state.getMoonStorage().getOrThrow(reference.id).name
            is ReferencedMountain -> state.getMountainStorage().getOrThrow(reference.id).name
            is ReferencedRiver -> state.getRiverStorage().getOrThrow(reference.id).name
            is ReferencedTown -> state.getTownStorage().getOrThrow(reference.id).name
        }

        return if (prefix != null && postfix != null) {
            "$prefix $referencedName $postfix"
        } else if (prefix != null) {
            "$prefix $referencedName"
        } else if (postfix != null) {
            "$referencedName $postfix"
        } else {
            error("Prefix & postfix are null!")
        }
    }

}
