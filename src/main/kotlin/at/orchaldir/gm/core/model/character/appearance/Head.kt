package at.orchaldir.gm.core.model.character.appearance

import kotlinx.serialization.Serializable

@Serializable
data class Head(
    val earType: EarType,
)