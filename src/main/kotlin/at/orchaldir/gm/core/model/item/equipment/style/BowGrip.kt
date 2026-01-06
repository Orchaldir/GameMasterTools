package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class BowGripType {
    None,
    Simple,
}

@Serializable
sealed class BowGrip : MadeFromParts {

    fun getType() = when (this) {
        is NoBowGrip -> BowGripType.None
        is SimpleBowGrip -> BowGripType.Simple
    }

    override fun parts() = when (this) {
        is NoBowGrip -> emptyList()
        is SimpleBowGrip -> grip.parts()
    }
}

@Serializable
@SerialName("None")
data object NoBowGrip : BowGrip()

@Serializable
@SerialName("Simple")
data class SimpleBowGrip(
    val size: Size = Size.Medium,
    val grip: Grip = SimpleGrip(),
) : BowGrip()
