package at.orchaldir.gm.core.model.economy.material

/**
 * The texture of the material formed when it is fractured.
 */
enum class Fracture {
    /**
     * curved, mussel-like
     */
    Conchoidal,

    /**
     * sof like soil
     */
    Earthy,

    /**
     * jagged, sharp
     */
    Hackly,

    /**
     * sharp, elongated points
     */
    Splintery,

    /**
     * rough and random
     */
    Uneven,
}