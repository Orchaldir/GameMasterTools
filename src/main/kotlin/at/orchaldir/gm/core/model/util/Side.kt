package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.app.LEFT
import kotlinx.serialization.Serializable

@Serializable
enum class Side {
    Left,
    Right;

    fun <T> get(pair: Pair<T, T>) = if (this == Left) {
        pair.first
    } else {
        pair.second
    }
}