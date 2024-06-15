package at.orchaldir.gm.core.model.culture.name

import kotlinx.serialization.Serializable

@Serializable
enum class NameOrder {
    GivenNameFirst,
    FamilyNameFirst,
}

@Serializable
enum class MiddleNameOptions {
    None,
    Random,
    Patronym,
    Matronym,

    /**
     * Patronym or Matronym based on the own gender.
     */
    Genonym,
}