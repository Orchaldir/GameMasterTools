package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.APPEARANCE
import at.orchaldir.gm.app.COLOR
import at.orchaldir.gm.app.EQUIPMENT
import at.orchaldir.gm.app.SCHEME
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.money.parsePriceLookup
import at.orchaldir.gm.app.html.economy.money.selectPriceLookup
import at.orchaldir.gm.app.html.economy.money.showPriceLookupDetails
import at.orchaldir.gm.app.html.item.equipment.data.*
import at.orchaldir.gm.app.html.rpg.equipment.editEquipmentStats
import at.orchaldir.gm.app.html.rpg.equipment.parseEquipmentStats
import at.orchaldir.gm.app.html.rpg.equipment.showEquipmentStats
import at.orchaldir.gm.app.html.util.color.editColorSchemeOption
import at.orchaldir.gm.app.html.util.color.fieldColorSchemeOption
import at.orchaldir.gm.app.html.util.color.parseColorSchemeOption
import at.orchaldir.gm.app.html.util.math.parseWeightLookup
import at.orchaldir.gm.app.html.util.math.selectWeightLookup
import at.orchaldir.gm.app.html.util.math.showWeightLookupDetails
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.selector.character.getCharacterTemplates
import at.orchaldir.gm.core.selector.character.getCharactersWith
import at.orchaldir.gm.core.selector.culture.getFashions
import at.orchaldir.gm.core.selector.gm.treasure.getTreasureParcelsWith
import at.orchaldir.gm.core.selector.item.equipment.CalculateVolumeConfig
import at.orchaldir.gm.core.selector.item.equipment.calculateCostFactors
import at.orchaldir.gm.core.selector.item.equipment.calculateVolumePerMaterial
import at.orchaldir.gm.core.selector.item.equipment.calculateWeightBasedOnType
import at.orchaldir.gm.core.selector.item.getUniforms
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.showEquipmentData(
    call: ApplicationCall,
    state: State,
    data: EquipmentData,
) {
    showDetails("Appearance", true) {
        field("Type", data.getType())

        when (data) {
            is OneHandedAxe -> showOneHandedAxe(call, state, data)
            is TwoHandedAxe -> showTwoHandedAxe(call, state, data)
            is Belt -> showBelt(call, state, data)
            is BodyArmour -> showBodyArmour(call, state, data)
            is Bow -> showBow(call, state, data)
            is OneHandedClub -> showOneHandedClub(call, state, data)
            is TwoHandedClub -> showTwoHandedClub(call, state, data)
            is Coat -> showCoat(call, state, data)
            is Dress -> showDress(call, state, data)
            is Earring -> showEarring(call, state, data)
            is EyePatch -> showEyePatch(call, state, data)
            is Footwear -> showFootwear(call, state, data)
            is Glasses -> showGlasses(call, state, data)
            is Gloves -> showGloves(call, state, data)
            is Hat -> showHat(call, state, data)
            is Helmet -> showHelmet(call, state, data)
            is IounStone -> showIounStone(call, state, data)
            is Necklace -> showNecklace(call, state, data)
            is Pants -> showPants(call, state, data)
            is Polearm -> showPolearm(call, state, data)
            is Shield -> showShield(call, state, data)
            is Shirt -> showShirt(call, state, data)
            is Skirt -> showSkirt(call, state, data)
            is Sling -> showSling(call, state, data)
            is Socks -> showSocks(call, state, data)
            is SuitJacket -> showSuitJacket(call, state, data)
            is OneHandedSword -> showOneHandedSword(call, state, data)
            is TwoHandedSword -> showTwoHandedSword(call, state, data)
            is Tie -> showTie(call, state, data)
            is Tunic -> showTunic(call, state, data)
        }
    }
}

// edit

fun HtmlBlockTag.editEquipmentData(
    state: State,
    data: EquipmentData,
) {
    showDetails("Appearance", true) {
        selectValue(
            "Type",
            combine(APPEARANCE, TYPE),
            EquipmentDataType.entries,
            data.getType(),
        )

        when (data) {
            is OneHandedAxe -> editOneHandedAxe(state, data)
            is TwoHandedAxe -> editTwoHandedAxe(state, data)
            is Belt -> editBelt(state, data)
            is BodyArmour -> editBodyArmour(state, data)
            is Bow -> editBow(state, data)
            is OneHandedClub -> editOneHandedClub(state, data)
            is TwoHandedClub -> editTwoHandedClub(state, data)
            is Coat -> editCoat(state, data)
            is Dress -> editDress(state, data)
            is Earring -> editEarring(state, data)
            is EyePatch -> editEyePatch(state, data)
            is Footwear -> editFootwear(state, data)
            is Glasses -> editGlasses(state, data)
            is Gloves -> editGloves(state, data)
            is Hat -> editHat(state, data)
            is Helmet -> editHelmet(state, data)
            is IounStone -> editIounStone(state, data)
            is Necklace -> editNecklace(state, data)
            is Pants -> editPants(state, data)
            is Polearm -> editPolearm(state, data)
            is Shield -> editShield(state, data)
            is Shirt -> editShirt(state, data)
            is Skirt -> editSkirt(state, data)
            is Sling -> editSling(state, data)
            is Socks -> editSocks(state, data)
            is SuitJacket -> editSuitJacket(state, data)
            is OneHandedSword -> editOneHandedSword(state, data)
            is TwoHandedSword -> editTwoHandedSword(state, data)
            is Tie -> editTie(state, data)
            is Tunic -> editTunic(state, data)
        }
    }
}

// parse

fun parseEquipmentData(
    state: State,
    parameters: Parameters,
) = when (parse(parameters, combine(APPEARANCE, TYPE), EquipmentDataType.Belt)) {
    EquipmentDataType.OneHandedAxe -> parseOneHandedAxe(state, parameters)
    EquipmentDataType.TwoHandedAxe -> parseTwoHandedAxe(state, parameters)
    EquipmentDataType.Belt -> parseBelt(state, parameters)
    EquipmentDataType.BodyArmour -> parseBodyArmour(state, parameters)
    EquipmentDataType.Bow -> parseBow(state, parameters)
    EquipmentDataType.OneHandedClub -> parseOneHandedClub(state, parameters)
    EquipmentDataType.TwoHandedClub -> parseTwoHandedClub(state, parameters)
    EquipmentDataType.Coat -> parseCoat(state, parameters)
    EquipmentDataType.Dress -> parseDress(state, parameters)
    EquipmentDataType.Earring -> parseEarring(state, parameters)
    EquipmentDataType.EyePatch -> parseEyePatch(state, parameters)
    EquipmentDataType.Footwear -> parseFootwear(state, parameters)
    EquipmentDataType.Glasses -> parseGlasses(state, parameters)
    EquipmentDataType.Gloves -> parseGloves(state, parameters)
    EquipmentDataType.Hat -> parseHat(state, parameters)
    EquipmentDataType.Helmet -> parseHelmet(state, parameters)
    EquipmentDataType.IounStone -> parseIounStone(state, parameters)
    EquipmentDataType.Necklace -> parseNecklace(state, parameters)
    EquipmentDataType.Pants -> parsePants(state, parameters)
    EquipmentDataType.Polearm -> parsePolearm(state, parameters)
    EquipmentDataType.Shield -> parseShield(state, parameters)
    EquipmentDataType.Shirt -> parseShirt(state, parameters)
    EquipmentDataType.Skirt -> parseSkirt(state, parameters)
    EquipmentDataType.Sling -> parseSling(state, parameters)
    EquipmentDataType.Socks -> parseSocks(state, parameters)
    EquipmentDataType.SuitJacket -> parseSuitJacket(state, parameters)
    EquipmentDataType.OneHandedSword -> parseOneHandedSword(state, parameters)
    EquipmentDataType.TwoHandedSword -> parseTwoHandedSword(state, parameters)
    EquipmentDataType.Tie -> parseTie(state, parameters)
    EquipmentDataType.Tunic -> parseTunic(state, parameters)
}
