package at.orchaldir.gm.core.model.util

import kotlinx.serialization.Serializable

@Serializable
enum class Side {
    Left,
    Right;

    fun flip() = if (this == Left) {
        Right
    } else {
        Left
    }

    fun <T> get(pair: Pair<T, T>) = if (this == Left) {
        pair.first
    } else {
        pair.second
    }
}