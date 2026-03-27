package at.orchaldir.gm.core.generator

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.CharacterTemplate
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.culture.fashion.Fashion
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.Rarity
import at.orchaldir.gm.core.model.util.RarityMap
import at.orchaldir.gm.core.model.util.render.ColorSchemeId
import at.orchaldir.gm.core.selector.culture.getFashion
import at.orchaldir.gm.utils.NumberGenerator
import at.orchaldir.gm.utils.RandomNumberGenerator
import kotlin.random.Random

data class EquipmentGenerator(
    val state: State,
    val numberGenerator: NumberGenerator,
    val rarityGenerator: RarityGenerator,
    val fashion: Fashion,
) {

    companion object {
        fun create(state: State, characterId: CharacterId): EquipmentGenerator? {
            val character = state.getCharacterStorage().getOptional(characterId) ?: return null

            return create(state, character)
        }

        fun create(state: State, character: Character) =
            create(state, character.culture, character.gender)

        fun create(state: State, culture: CultureId?, gender: Gender): EquipmentGenerator? {
            val fashion = state.getFashion(culture, gender) ?: return null

            return EquipmentGenerator(
                state,
                RandomNumberGenerator(Random),
                state.rarityGenerator,
                fashion,
            )
        }
    }

    fun generate(): EquipmentIdMap {
        val result = mutableMapOf<EquipmentId, EquipmentDataType>()

        generate(fashion.clothing.clothingSets).getTypes().forEach { type ->
            generate(result, type)
        }

        ACCESSORIES.forEach { accessory ->
            generateAccessory(result, accessory)
        }

        return EquipmentMap.fromSlotAsValueMap(
            result
                .mapKeys { entry -> Pair(entry.key, generateColorScheme(entry.key)) }
                .mapValues { setOf(it.value.slots().getAllBodySlotCombinations().first()) })
    }

    private fun generateColorScheme(id: EquipmentId): ColorSchemeId? {
        val equipment = state.getEquipmentStorage().getOrThrow(id)

        return if (equipment.colorSchemes.isEmpty()) {
            null
        } else {
            numberGenerator.select(equipment.colorSchemes.toList())
        }
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
