package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.name.ComplexName
import at.orchaldir.gm.core.model.name.NameWithReference
import at.orchaldir.gm.core.model.name.SimpleName

fun checkComplexName(
    state: State,
    name: ComplexName,
) {
    when (name) {
        is NameWithReference -> {
            require(!(name.prefix.isNullOrEmpty() && name.postfix.isNullOrEmpty())) { "The prefix & the postfix of the complex must not be empty at the same time!" }
        }

        is SimpleName -> require(name.name.isNotEmpty()) { "Simple must not be empty!" }
    }
}
