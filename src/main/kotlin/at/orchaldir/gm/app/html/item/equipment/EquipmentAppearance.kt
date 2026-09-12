package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.APPEARANCE
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.item.equipment.data.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.*
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showEquipmentAppearance(
    call: ApplicationCall,
    state: State,
    appearance: EquipmentAppearance,
) {
    showDetails("Appearance", true) {
        field("Type", appearance.getType())

        when (appearance) {
            is OneHandedAxe -> showOneHandedAxe(call, state, appearance)
            is TwoHandedAxe -> showTwoHandedAxe(call, state, appearance)
            is Belt -> showBelt(call, state, appearance)
            is BodyArmour -> showBodyArmour(call, state, appearance)
            is Bow -> showBow(call, state, appearance)
            is OneHandedClub -> showOneHandedClub(call, state, appearance)
            is TwoHandedClub -> showTwoHandedClub(call, state, appearance)
            is Coat -> showCoat(call, state, appearance)
            is Dress -> showDress(call, state, appearance)
            is Earring -> showEarring(call, state, appearance)
            is EyePatch -> showEyePatch(call, state, appearance)
            is Footwear -> showFootwear(call, state, appearance)
            is Glasses -> showGlasses(call, state, appearance)
            is Gloves -> showGloves(call, state, appearance)
            is Hat -> showHat(call, state, appearance)
            is Helmet -> showHelmet(call, state, appearance)
            is IounStone -> showIounStone(call, state, appearance)
            is Necklace -> showNecklace(call, state, appearance)
            is Pants -> showPants(call, state, appearance)
            is Polearm -> showPolearm(call, state, appearance)
            is Shield -> showShield(call, state, appearance)
            is Shirt -> showShirt(call, state, appearance)
            is Skirt -> showSkirt(call, state, appearance)
            is Sling -> showSling(call, state, appearance)
            is Socks -> showSocks(call, state, appearance)
            is SuitJacket -> showSuitJacket(call, state, appearance)
            is OneHandedSword -> showOneHandedSword(call, state, appearance)
            is TwoHandedSword -> showTwoHandedSword(call, state, appearance)
            is Tie -> showTie(call, state, appearance)
            is Tunic -> showTunic(call, state, appearance)
        }
    }
}

// edit

fun HtmlBlockTag.editEquipmentAppearance(
    state: State,
    appearance: EquipmentAppearance,
) {
    showDetails("Appearance", true) {
        selectValue(
            "Type",
            combine(APPEARANCE, TYPE),
            EquipmentAppearanceType.entries,
            appearance.getType(),
        )

        when (appearance) {
            is OneHandedAxe -> editOneHandedAxe(state, appearance)
            is TwoHandedAxe -> editTwoHandedAxe(state, appearance)
            is Belt -> editBelt(state, appearance)
            is BodyArmour -> editBodyArmour(state, appearance)
            is Bow -> editBow(state, appearance)
            is OneHandedClub -> editOneHandedClub(state, appearance)
            is TwoHandedClub -> editTwoHandedClub(state, appearance)
            is Coat -> editCoat(state, appearance)
            is Dress -> editDress(state, appearance)
            is Earring -> editEarring(state, appearance)
            is EyePatch -> editEyePatch(state, appearance)
            is Footwear -> editFootwear(state, appearance)
            is Glasses -> editGlasses(state, appearance)
            is Gloves -> editGloves(state, appearance)
            is Hat -> editHat(state, appearance)
            is Helmet -> editHelmet(state, appearance)
            is IounStone -> editIounStone(state, appearance)
            is Necklace -> editNecklace(state, appearance)
            is Pants -> editPants(state, appearance)
            is Polearm -> editPolearm(state, appearance)
            is Shield -> editShield(state, appearance)
            is Shirt -> editShirt(state, appearance)
            is Skirt -> editSkirt(state, appearance)
            is Sling -> editSling(state, appearance)
            is Socks -> editSocks(state, appearance)
            is SuitJacket -> editSuitJacket(state, appearance)
            is OneHandedSword -> editOneHandedSword(state, appearance)
            is TwoHandedSword -> editTwoHandedSword(state, appearance)
            is Tie -> editTie(state, appearance)
            is Tunic -> editTunic(state, appearance)
        }
    }
}

// parse

fun parseEquipmentAppearance(
    state: State,
    parameters: Parameters,
) = when (parse(parameters, combine(APPEARANCE, TYPE), EquipmentAppearanceType.Belt)) {
    EquipmentAppearanceType.OneHandedAxe -> parseOneHandedAxe(state, parameters)
    EquipmentAppearanceType.TwoHandedAxe -> parseTwoHandedAxe(state, parameters)
    EquipmentAppearanceType.Belt -> parseBelt(state, parameters)
    EquipmentAppearanceType.BodyArmour -> parseBodyArmour(state, parameters)
    EquipmentAppearanceType.Bow -> parseBow(state, parameters)
    EquipmentAppearanceType.OneHandedClub -> parseOneHandedClub(state, parameters)
    EquipmentAppearanceType.TwoHandedClub -> parseTwoHandedClub(state, parameters)
    EquipmentAppearanceType.Coat -> parseCoat(state, parameters)
    EquipmentAppearanceType.Dress -> parseDress(state, parameters)
    EquipmentAppearanceType.Earring -> parseEarring(state, parameters)
    EquipmentAppearanceType.EyePatch -> parseEyePatch(state, parameters)
    EquipmentAppearanceType.Footwear -> parseFootwear(state, parameters)
    EquipmentAppearanceType.Glasses -> parseGlasses(state, parameters)
    EquipmentAppearanceType.Gloves -> parseGloves(state, parameters)
    EquipmentAppearanceType.Hat -> parseHat(state, parameters)
    EquipmentAppearanceType.Helmet -> parseHelmet(state, parameters)
    EquipmentAppearanceType.IounStone -> parseIounStone(state, parameters)
    EquipmentAppearanceType.Necklace -> parseNecklace(state, parameters)
    EquipmentAppearanceType.Pants -> parsePants(state, parameters)
    EquipmentAppearanceType.Polearm -> parsePolearm(state, parameters)
    EquipmentAppearanceType.Shield -> parseShield(state, parameters)
    EquipmentAppearanceType.Shirt -> parseShirt(state, parameters)
    EquipmentAppearanceType.Skirt -> parseSkirt(state, parameters)
    EquipmentAppearanceType.Sling -> parseSling(state, parameters)
    EquipmentAppearanceType.Socks -> parseSocks(state, parameters)
    EquipmentAppearanceType.SuitJacket -> parseSuitJacket(state, parameters)
    EquipmentAppearanceType.OneHandedSword -> parseOneHandedSword(state, parameters)
    EquipmentAppearanceType.TwoHandedSword -> parseTwoHandedSword(state, parameters)
    EquipmentAppearanceType.Tie -> parseTie(state, parameters)
    EquipmentAppearanceType.Tunic -> parseTunic(state, parameters)
}
