package at.orchaldir.gm.utils.math.unit

val LARGE_AREA_UNITS = setOf(
    AreaUnit.SquareKiloMeter,
    AreaUnit.Hectare,
    AreaUnit.Acre,
)

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