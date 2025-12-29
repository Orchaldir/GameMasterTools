package at.orchaldir.gm.utils.math.unit

enum class AreaUnit {
    SquareKiloMeter,
    Hectare,
    Acre,
    SquareMeter,
    SquareCentiMeter,
    SquareMilliMeter;

    fun resolveUnit() = when (this) {
        SquareKiloMeter -> "km^2"
        Hectare -> "ha"
        Acre -> "acre"
        SquareMeter -> "m^2"
        SquareCentiMeter -> "cm^2"
        SquareMilliMeter -> "mm^2"
    }
}