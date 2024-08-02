package at.orchaldir.gm.core.model.item.style

import kotlinx.serialization.Serializable

@Serializable
data class ButtonColumn(
    val button: Button = Button(),
    val count: UByte = 3u,
)
