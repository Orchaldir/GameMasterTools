package at.orchaldir.gm.core.model.economy.material

/**
 * The behavior when deformed.
 */
enum class Tenacity {
    /**
     * Breaks or powders.
     */
    Brittle,

    /**
     * May be pounded into thin sheets.
     */
    Malleable,

    /**
     * May be drawn into a wire.
     */
    Ductile,

    /**
     * May be cut with a knife.
     */
    Sectile,

    /**
     * Returns to the original shape after bending.
     */
    Elastic,

    /**
     * Stays in the new shape after bending.
     */
    Plastic,
}