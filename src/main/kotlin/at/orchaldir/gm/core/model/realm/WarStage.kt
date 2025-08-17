package at.orchaldir.gm.core.model.realm

import kotlinx.serialization.Serializable

@Serializable
data class WarStage(
    val sides: List<WarSide>,
)