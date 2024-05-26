package at.orchaldir.gm.core.model.character.appearance

import kotlinx.serialization.Serializable

@Serializable
enum class EarType {
    CatLike,
    LongAndPointed,
    None,
    Pointed,
    Round,
}