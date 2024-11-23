package at.orchaldir.gm.core.model.name

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class ComplexName

@Serializable
@SerialName("Simple")
data class SimpleName(val name: String) : ComplexName()

@Serializable
@SerialName("Reference")
data class NameWithReference(
    val prefix: String?,
    val postfix: String,
) : ComplexName()
