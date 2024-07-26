package at.orchaldir.gm.core.generator

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.appearance.RarityMap
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.culture.style.AppearanceStyle
import at.orchaldir.gm.core.model.race.appearance.*
import at.orchaldir.gm.utils.NumberGenerator
import at.orchaldir.gm.utils.RandomNumberGenerator
import kotlin.random.Random

data class EquipmentGenerator(
    val numberGenerator: NumberGenerator,
    val rarityGenerator: RarityGenerator,
    val character: Character,
    val appearanceOptions: AppearanceOptions,
    val appearanceStyle: AppearanceStyle,
) {

    companion object {
        fun create(state: State, character: Character): EquipmentGenerator {
            val race = state.races.getOrThrow(character.race)
            val culture = state.cultures.getOrThrow(character.culture)

            return EquipmentGenerator(
                RandomNumberGenerator(Random),
                state.rarityGenerator,
                character,
                race.appearance,
                culture.appearanceStyle
            )
        }
    }

    fun <T> generate(map: RarityMap<T>) = rarityGenerator.generate(map, numberGenerator)

    fun <T> select(list: List<T>) = numberGenerator.select(list)

}
