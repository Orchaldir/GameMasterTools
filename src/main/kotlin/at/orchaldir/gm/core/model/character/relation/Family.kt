package at.orchaldir.gm.core.model.character.relation

import at.orchaldir.gm.core.model.character.Gender

enum class RelativeType {
    GrandParent,
    Pibling,  // Uncle or Aunt
    Parent,
    Cousin,
    Sibling,
    Nibling, // Nephew or Niece
    Child,
    GrandChild;

    fun reverse() = when (this) {
        GrandParent -> GrandChild
        Pibling -> Nibling
        Nibling -> Pibling
        Parent -> Child
        Cousin -> Cousin
        Sibling -> Sibling
        Child -> Parent
        GrandChild -> GrandParent
    }

    fun getGenderSpecificString(gender: Gender) = when (gender) {
        Gender.Male -> {
            when (this) {
                GrandParent -> "grandfather"
                Pibling -> "uncle"
                Nibling -> "nephew"
                Parent -> "father"
                Cousin -> "cousin"
                Sibling -> "brother"
                Child -> "son"
                GrandChild -> "grandson"
            }
        }

        Gender.Female -> {
            when (this) {
                GrandParent -> "grandmother"
                Pibling -> "aunt"
                Nibling -> "niece"
                Parent -> "mother"
                Cousin -> "cousin"
                Sibling -> "sister"
                Child -> "daughter"
                GrandChild -> "granddaughter"
            }
        }

        Gender.Genderless -> this.toString().lowercase()

    }
}