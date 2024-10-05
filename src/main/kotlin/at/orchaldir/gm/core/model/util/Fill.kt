package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.utils.map.MapSize2d
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Fill

@Serializable
@SerialName("Solid")
data class Solid(
    val color: Color,
) : Fill()

@Serializable
@SerialName("VerticalStripes")
data class VerticalStripes(
    val color0: Color,
    val color1: Color,
    val width: UByte = 1u,
) : Fill()

@Serializable
@SerialName("HorizontalStripes")
data class HorizontalStripes(
    val color0: Color,
    val color1: Color,
    val width: UByte = 1u,
) : Fill()

@Serializable
@SerialName("Tiles")
data class Tiles(
    val fill: Color,
    val background: Color?,
    val width: UByte = 3u,
    val border: UByte = 1u,
) : Fill()

