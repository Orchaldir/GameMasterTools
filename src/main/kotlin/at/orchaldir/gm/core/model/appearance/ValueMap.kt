package at.orchaldir.gm.core.model.appearance

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class ValueMap<Key, Value> {

    abstract fun get(value: Key): Value?

}

@Serializable
@SerialName("Shared")
data class SharedValue<Key, Value>(
    private val value: Value,
) : ValueMap<Key, Value>() {

    override fun get(value: Key) = this.value

}

@Serializable
@SerialName("Individual")
data class IndividualValues<Key, Value>(
    private val values: Map<Key, Value>,
) : ValueMap<Key, Value>() {

    override fun get(value: Key) = values[value]

}


