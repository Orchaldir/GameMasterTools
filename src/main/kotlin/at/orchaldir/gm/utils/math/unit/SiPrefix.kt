package at.orchaldir.gm.utils.math.unit

enum class SiPrefix {
    Kilo,
    Base,
    Milli,
    Micro;

    fun resolveUnit() = when (this) {
        SiPrefix.Kilo -> "k"
        SiPrefix.Base -> ""
        SiPrefix.Milli -> "m"
        SiPrefix.Micro -> "µ"
    }
}