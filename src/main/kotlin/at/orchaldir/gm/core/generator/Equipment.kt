package at.orchaldir.gm.core.generator

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.EquipmentMap
import at.orchaldir.gm.core.model.culture.fashion.ClothingSet
import at.orchaldir.gm.core.model.culture.fashion.Fashion
import at.orchaldir.gm.core.model.item.equipment.ACCESSORIES
import at.orchaldir.gm.core.model.item.equipment.EquipmentDataType
import at.orchaldir.gm.core.model.item.equipment.EquipmentDataType.*
import at.orchaldir.gm.core.model.item.equipment.EquipmentId
import at.orchaldir.gm.core.model.item.equipment.getAllBodySlotCombinations
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.Rarity
import at.orchaldir.gm.core.model.util.RarityMap
import at.orchaldir.gm.core.selector.getFashion
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
        fun create(state: State, characterId: CharacterId): EquipmentGenerator? {
            val character = state.getCharacterStorage().getOptional(characterId) ?: return null
            val fashion = state.getFashion(character) ?: return null

            return EquipmentGenerator(
                RandomNumberGenerator(Random),
                state.rarityGenerator,
                character,
                fashion,
            )
        }
    }

    fun generate(): EquipmentMap<EquipmentId> {
        val result = mutableMapOf<EquipmentId, EquipmentDataType>()

        when (generate(fashion.clothing.clothingSets)) {
            ClothingSet.Dress -> generate(result, Dress)
            ClothingSet.PantsAndShirt -> generatePantsAndShirt(result)
            ClothingSet.ShirtAndSkirt -> generateShirtAndSkirt(result)
            ClothingSet.Suit -> generateSuit(result)
        }

        ACCESSORIES.forEach { accessory ->
            generateAccessory(result, accessory)
        }

        return EquipmentMap(result.mapValues { setOf(it.value.slots().getAllBodySlotCombinations().first()) })
    }

    private fun generatePantsAndShirt(result: MutableMap<EquipmentId, EquipmentDataType>) {
        generate(result, Pants)
        generate(result, Shirt)
    }

    private fun generateShirtAndSkirt(result: MutableMap<EquipmentId, EquipmentDataType>) {
        generate(result, Shirt)
        generate(result, Skirt)
    }

    private fun generateSuit(result: MutableMap<EquipmentId, EquipmentDataType>) {
        generate(result, Coat)
        generate(result, Pants)
        generate(result, Shirt)
    }

    private fun generateAccessory(result: MutableMap<EquipmentId, EquipmentDataType>, type: EquipmentDataType) {
        if (requiresAccessory(type)) {
            generate(result, type)
        }
    }

    private fun requiresAccessory(type: EquipmentDataType): Boolean {
        val rarity = fashion.clothing.accessories.getRarity(type)

        if (rarity == Rarity.Everyone) {
            return true
        }

        val rarityMap = OneOf(mapOf(true to rarity, false to Rarity.Common))

        return generate(rarityMap)
    }

    private fun generate(result: MutableMap<EquipmentId, EquipmentDataType>, type: EquipmentDataType) {
        val options = fashion.clothing.getOptions(type)
        result[generate(options)] = type
    }

    private fun <T> generate(map: RarityMap<T>) = rarityGenerator.generate(map, numberGenerator)

}
