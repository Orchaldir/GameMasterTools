package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.name.*

private const val REQUIRED_REFERENCE = "Reference for complex name is unknown!"

fun checkComplexName(
    state: State,
    name: ComplexName,
) {
    when (name) {
        is NameWithReference -> {
            require(!(name.prefix.isNullOrEmpty() && name.postfix.isNullOrEmpty())) { "The prefix & the postfix of the complex must not be empty at the same time!" }

            when (name.reference) {
                is ReferencedFamilyName -> state.getCharacterStorage().require(name.reference.id) { REQUIRED_REFERENCE }
                is ReferencedFullName -> state.getCharacterStorage().require(name.reference.id) { REQUIRED_REFERENCE }
                is ReferencedMoon -> state.getMoonStorage().require(name.reference.id) { REQUIRED_REFERENCE }
                is ReferencedMountain -> state.getMountainStorage().require(name.reference.id) { REQUIRED_REFERENCE }
                is ReferencedRiver -> state.getRiverStorage().require(name.reference.id) { REQUIRED_REFERENCE }
                is ReferencedTown -> state.getTownStorage().require(name.reference.id) { REQUIRED_REFERENCE }
            }
        }

        is SimpleName -> require(name.name.isNotEmpty()) { "Simple must not be empty!" }
    }
}
