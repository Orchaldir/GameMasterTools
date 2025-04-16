package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.core.model.character.SexualOrientation.*

val SEXUAL_ORIENTATION_FOR_GENDERLESS = setOf(Asexuality, Pansexuality, Demisexuality)

enum class SexualOrientation {
    Asexuality,

    /**
     * Regardless of sex or gender.
     */
    Pansexuality,
    Bisexuality,

    /**
     * Attracted to someone after getting to know them for a certain period of time.
     */
    Demisexuality,
    Heterosexuality,
    Homosexuality,
}