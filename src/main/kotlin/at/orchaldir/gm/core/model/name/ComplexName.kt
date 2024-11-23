package at.orchaldir.gm.core.model.name

import at.orchaldir.gm.core.model.State
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class ComplexName {

    abstract fun resolve(state: State): String

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
            is ReferencedFamilyName -> TODO()
            is ReferencedFullName -> TODO()
            is ReferencedMoon -> state.getMoonStorage().getOrThrow(reference.id)
            is ReferencedMountain -> state.getMountainStorage().getOrThrow(reference.id)
            is ReferencedRiver -> state.getRiverStorage().getOrThrow(reference.id)
            is ReferencedTown -> state.getTownStorage().getOrThrow(reference.id)
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
