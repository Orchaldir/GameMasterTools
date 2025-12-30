package at.orchaldir.gm.utils.math.unit

interface HasArea {
    fun area(): AreaLookup
    fun useDistrictsForAreaCalculation(): Boolean = false
    fun useRealmsForAreaCalculation(): Boolean = false
    fun useTownsForAreaCalculation(): Boolean = false
}