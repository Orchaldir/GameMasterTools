package at.orchaldir.gm.core.model.visualization

enum class BrickPattern {
    BasketWeaveSingle,
    BasketWeave,
    Grid,
    Herringbone,

    /**
     * A pinwheel where each curl is a brick of size n * (n - 1)
     */
    Pinwheel,

    /**
     * A pinwheel where each curl is (n - 1) bricks of length n
     */
    PinwheelSplit,

    /**
     * A pinwheel where the center has the size n - 1
     */
    PinwheelWithBigCenter,

    /**
     * Aka Hopscotch pattern, Windmill pattern
     *
     * https://en.wikipedia.org/wiki/Pythagorean_tiling
     */
    PythagoreanTiling,
    Running,
    Stack,
}