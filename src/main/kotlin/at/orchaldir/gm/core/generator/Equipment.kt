package at.orchaldir.gm.core.generator

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.EquipmentMap
import at.orchaldir.gm.core.model.fashion.ClothingSet
import at.orchaldir.gm.core.model.fashion.Fashion
import at.orchaldir.gm.core.model.item.equipment.EquipmentDataType
import at.orchaldir.gm.core.model.item.equipment.EquipmentDataType.*
import at.orchaldir.gm.core.model.item.equipment.ItemTemplateId
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.Rarity
import at.orchaldir.gm.core.model.util.RarityMap
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
            val culture = state.getCultureStorage().getOrThrow(character.culture)

            return EquipmentGenerator(
                RandomNumberGenerator(Random),
                state.rarityGenerator,
                character,
                state.getFashionStorage().getOrThrow(culture.getFashion(character)),
            )
        }
    }

    fun generate(): EquipmentMap {
        val result = mutableMapOf<EquipmentDataType, ItemTemplateId>()

        when (generate(fashion.clothingSets)) {
            ClothingSet.Dress -> generate(result, Dress)
            ClothingSet.PantsAndShirt -> generatePantsAndShirt(result)
            ClothingSet.ShirtAndSkirt -> generateShirtAndSkirt(result)
            ClothingSet.Suit -> generateSuit(result)
        }

        generateAccessory(result, Footwear)
        generateAccessory(result, Gloves)
        generateAccessory(result, Hat)

        return EquipmentMap(result)
    }

    private fun generatePantsAndShirt(result: MutableMap<EquipmentDataType, ItemTemplateId>) {
        generate(result, Pants)
        generate(result, Shirt)
    }

    private fun generateShirtAndSkirt(result: MutableMap<EquipmentDataType, ItemTemplateId>) {
        generate(result, Shirt)
        generate(result, Skirt)
    }

    private fun generateSuit(result: MutableMap<EquipmentDataType, ItemTemplateId>) {
        generate(result, Coat)
        generate(result, Pants)
        generate(result, Shirt)
    }

    private fun generateAccessory(result: MutableMap<EquipmentDataType, ItemTemplateId>, type: EquipmentDataType) {
        if (requiresAccessory(type)) {
            generate(result, type)
        }
    }

    private fun requiresAccessory(type: EquipmentDataType): Boolean {
        val rarity = fashion.accessories.getRarity(type)

        if (rarity == Rarity.Everyone) {
            return true
        }

        val rarityMap = OneOf(mapOf(true to rarity, false to Rarity.Common))

        return generate(rarityMap)
    }

    private fun generate(result: MutableMap<EquipmentDataType, ItemTemplateId>, type: EquipmentDataType) {
        val options = fashion.getOptions(type)
        result[type] = generate(options)
    }

    private fun <T> generate(map: RarityMap<T>) = rarityGenerator.generate(map, numberGenerator)

}
