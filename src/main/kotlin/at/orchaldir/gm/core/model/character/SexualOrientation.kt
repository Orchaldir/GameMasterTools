package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.core.model.character.SexualOrientation.*

val SEXUAL_ORIENTATION_FOR_GENDERLESS = setOf(Asexual, Pansexual, Demisexual)

enum class SexualOrientation {
    Asexual,

    /**
     * Regardless of sex or gender.
     */
    Pansexual,
    Bisexual,

    /**
     * Attracted to someone after getting to know them for a certain period of time.
     */
    Demisexual,
    Heterosexual,
    Homosexual,
}