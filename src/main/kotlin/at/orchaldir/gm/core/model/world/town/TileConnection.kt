package at.orchaldir.gm.core.model.world.town

enum class TileConnection {
    Curve,
    Horizontal,
    Vertical;

    fun canConnectHorizontal() = this != Vertical
    fun canConnectVertical() = this != Horizontal
}