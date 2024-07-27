package at.orchaldir.gm.core.generator

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.appearance.RarityMap
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.EquipmentMap
import at.orchaldir.gm.core.model.fashion.ClothingSet
import at.orchaldir.gm.core.model.fashion.Fashion
import at.orchaldir.gm.core.model.item.EquipmentType
import at.orchaldir.gm.core.model.item.EquipmentType.*
import at.orchaldir.gm.core.model.item.ItemTemplateId
import at.orchaldir.gm.utils.NumberGenerator
import at.orchaldir.gm.utils.RandomNumberGenerator
import kotlin.random.Random

data class EquipmentGenerator(
    val numberGenerator: NumberGenerator,
    val rarityGenerator: RarityGenerator,
    val character: Character,
    val fashion: Fashion,
) {

    companion object {
        fun create(state: State, character: Character): EquipmentGenerator {
            val culture = state.cultures.getOrThrow(character.culture)

            return EquipmentGenerator(
                RandomNumberGenerator(Random),
                state.rarityGenerator,
                character,
                state.fashion.getOrThrow(culture.getFashion(character)),
            )
        }
    }

    fun generate(): EquipmentMap {
        val map = mutableMapOf<EquipmentType, ItemTemplateId>()

        when (generate(fashion.clothingSets)) {
            ClothingSet.Dress -> generate(map, Dress)
            ClothingSet.PantsAndShirt -> generatePantsAndShirt(map)
            ClothingSet.ShirtAndSkirt -> generateShirtAndSkirt(map)
        }

        generate(map, Footwear)
        generate(map, Hat)

        return EquipmentMap(map)
    }

    private fun generatePantsAndShirt(map: MutableMap<EquipmentType, ItemTemplateId>) {
        generate(map, Pants)
        generate(map, Shirt)
    }

    private fun generateShirtAndSkirt(map: MutableMap<EquipmentType, ItemTemplateId>) {
        generate(map, Shirt)
        generate(map, Skirt)
    }

    private fun generate(map: MutableMap<EquipmentType, ItemTemplateId>, type: EquipmentType) {
        val options = fashion.getOptions(type)
        map[type] = generate(options)
    }

    private fun <T> generate(map: RarityMap<T>) = rarityGenerator.generate(map, numberGenerator)

    private fun <T> select(list: List<T>) = numberGenerator.select(list)

}
