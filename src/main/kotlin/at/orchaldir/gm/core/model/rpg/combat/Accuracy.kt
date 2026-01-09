package at.orchaldir.gm.core.model.rpg.combat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class AccuracyType {
    Simple,
    Scope,
    Undefined,
}

@Serializable
sealed class Accuracy {

    fun getType() = when (this) {
        is SimpleAccuracy -> AccuracyType.Simple
        is AccuracyWithScope -> AccuracyType.Scope
        is UndefinedAccuracy -> AccuracyType.Undefined
    }
}

@Serializable
@SerialName("Simple")
data class SimpleAccuracy(
    val modifier: Int,
) : Accuracy()

@Serializable
@SerialName("Simple")
data class AccuracyWithScope(
    val base: Int,
    val scope: Int,
) : Accuracy()

@Serializable
@SerialName("Undefined")
data object UndefinedAccuracy : Accuracy()
