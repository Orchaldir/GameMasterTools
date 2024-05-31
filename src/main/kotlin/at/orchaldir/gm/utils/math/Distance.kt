package at.orchaldir.gm.utils.math

data class Distance(val value: Float) {

    init {
        require(value > 0.0) { "Distance muster be greater 0!" }
    }

}
