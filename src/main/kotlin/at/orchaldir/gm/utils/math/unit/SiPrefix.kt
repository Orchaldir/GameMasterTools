package at.orchaldir.gm.utils.math.unit

enum class SiPrefix {
    Kilo,
    Base,
    Centi,
    Milli,
    Micro;

    fun resolveUnit() = when (this) {
        Kilo -> "k"
        Base -> ""
        Centi -> "c"
        Milli -> "m"
        Micro -> "µ"
    }
}