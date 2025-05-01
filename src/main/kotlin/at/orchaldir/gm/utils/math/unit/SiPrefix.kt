package at.orchaldir.gm.utils.math.unit

enum class SiPrefix {
    Kilo,
    Base,
    Milli,
    Micro;

    fun resolveUnit() = when (this) {
        Kilo -> "k"
        Base -> ""
        Milli -> "m"
        Micro -> "µ"
    }
}