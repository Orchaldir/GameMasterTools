package at.orchaldir.gm.core.reducer.race

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.FeatureColorType
import at.orchaldir.gm.core.model.character.appearance.hair.HairType
import at.orchaldir.gm.core.model.economy.material.Fur
import at.orchaldir.gm.core.model.race.appearance.RaceAppearance
import at.orchaldir.gm.core.model.race.appearance.WingOptions

fun validateSkin(state: State, appearance: RaceAppearance) {
    val options = appearance.skin

    if (options.fur != null) {
        val fur = state.getMaterialStorage().getOrThrow(options.fur)

        require(fur.properties.category is Fur) {
            "${fur.id.print()} is not a fur!"
        }
    }
}

fun validateTails(appearance: RaceAppearance) {
    val options = appearance.tail

    options.simpleShapes.getValidValues().forEach {
        require(options.simpleOptions.containsKey(it)) { "No options for $it tail!" }
    }

    if (!appearance.hair.hairTypes.contains(HairType.Normal)) {
        options.simpleOptions.forEach { (shape, shapeOptions) ->
            require(shapeOptions.type != FeatureColorType.Hair) { "Tail options for $shape require hair!" }
        }
    }
}

fun validateWings(options: WingOptions) {
    if (options.hasWings()) {
        require(options.types.isNotEmpty()) { "Having wings requires wing types!" }
    }
}
