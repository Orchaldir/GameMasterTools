package at.orchaldir.gm.core.model.culture.name

import kotlinx.serialization.Serializable

@Serializable
enum class NameOrder {
    GivenNameFirst,
    FamilyNameFirst,
}

@Serializable
enum class MiddleNameOption {
    None,
    Random,
}