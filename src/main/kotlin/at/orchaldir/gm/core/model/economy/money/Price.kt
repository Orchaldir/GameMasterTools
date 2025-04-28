package at.orchaldir.gm.core.model.economy.money

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class Price(val value: Int) {

    init {
        require(value >= 0) { "Price is negative!" }
    }
}
