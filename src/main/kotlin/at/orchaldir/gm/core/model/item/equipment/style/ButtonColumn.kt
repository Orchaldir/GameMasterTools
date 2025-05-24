package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.part.MadeFromParts
import kotlinx.serialization.Serializable

@Serializable
data class ButtonColumn(
    val button: Button = Button(),
    val count: UByte = 3u,
) : MadeFromParts {

    override fun parts() = listOf(button.part)

}
