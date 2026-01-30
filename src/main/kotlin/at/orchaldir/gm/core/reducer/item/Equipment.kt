package at.orchaldir.gm.core.reducer.item

import at.orchaldir.gm.core.action.Action
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.render.COLOR_SCHEME_TYPE
import at.orchaldir.gm.core.reducer.rpg.validateArmorStats
import at.orchaldir.gm.core.reducer.rpg.validateMeleeWeaponStats
import at.orchaldir.gm.core.reducer.rpg.validateRangedWeaponStats
import at.orchaldir.gm.core.reducer.rpg.validateShieldStats
import at.orchaldir.gm.core.selector.item.equipment.canDeleteEquipment
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.validateFactor
import at.orchaldir.gm.utils.math.checkInt
import at.orchaldir.gm.utils.redux.noFollowUps

fun updateEquipment(state: State, equipment: Equipment): Pair<State, List<Action>> {
    val oldEquipment = state.getEquipmentStorage().getOrThrow(equipment.id)

    validateEquipment(state, equipment)

    if (equipment.data.javaClass != oldEquipment.data.javaClass) {
        require(state.canDeleteEquipment(equipment.id).canDelete()) {
            "Cannot change equipment ${equipment.id.value} while it is equipped"
        }
    }

    return noFollowUps(state.updateStorage(state.getEquipmentStorage().update(equipment)))
}

fun validateEquipment(
    state: State,
    equipment: Equipment,
) {
    val requiredSchemaColors = equipment.data.requiredSchemaColors()

    state.getMaterialStorage().require(equipment.data.materials())
    state.getColorSchemeStorage().require(equipment.colorSchemes)

    require(requiredSchemaColors == 0 || equipment.colorSchemes.isNotEmpty()) {
        "Requires at least 1 $COLOR_SCHEME_TYPE"
    }

    state.getColorSchemeStorage().get(equipment.colorSchemes)
        .forEach { scheme ->
            require(scheme.data.count() >= requiredSchemaColors) { "${scheme.id.print()} has too few colors!" }
        }

    when (equipment.data) {
        is BodyArmour -> checkBodyArmour(equipment.data)
        is Polearm -> checkPolearmHead(equipment.data.head)
        is OneHandedSword -> checkOneHandedSword(equipment.data)
        is TwoHandedSword -> checkTwoHandedSword(equipment.data)
        else -> doNothing()
    }

    equipment.data.getArmorStats()?.let { validateArmorStats(state, it) }
    equipment.data.getMeleeWeaponStats()?.let { validateMeleeWeaponStats(state, it) }
    equipment.data.getRangedWeaponStats()?.let { validateRangedWeaponStats(state, it) }
    equipment.data.getShieldStats()?.let { validateShieldStats(state, it) }
}

private fun checkBodyArmour(armour: BodyArmour) = when (armour.style) {
    is ChainMail -> doNothing()

    is LamellarArmour -> {
        checkLamellarLacing(armour.style.lacing)
        checkArmourColumns(armour.style.columns)
    }

    is ScaleArmour -> {
        validateFactor(armour.style.overlap, "Overlap", MIN_SCALE_OVERLAP, MAX_SCALE_OVERLAP)
        checkArmourColumns(armour.style.columns)
    }

    is SegmentedArmour -> {
        checkArmourColumns(armour.style.rows)
        checkInt(armour.style.breastplateRows, "Breastplate Rows", 1, armour.style.rows - 1)
    }
}

private fun checkLamellarLacing(lacing: LamellarLacing) = when (lacing) {
    NoLacing -> doNothing()
    is DiagonalLacing -> checkLacingThickness(lacing.thickness)
    is FourSidesLacing -> {
        checkLacingLength(lacing.lacingLength)
        checkLacingThickness(lacing.lacingThickness)
    }

    is LacingAndStripe -> {
        checkLacingLength(lacing.lacingLength)
        checkLacingThickness(lacing.lacingThickness)
        validateFactor(lacing.stripeWidth, "Stripe Width", MIN_STRIPE_WIDTH, MAX_STRIPE_WIDTH)
    }
}

private fun checkLacingLength(length: Factor) {
    validateFactor(length, "Lacing Length", MIN_LENGTH, MAX_LENGTH)
}

private fun checkLacingThickness(thickness: Factor) {
    validateFactor(thickness, "Lacing Thickness", MIN_THICKNESS, MAX_THICKNESS)
}

private fun checkArmourColumns(columns: Int) {
    checkInt(columns, "columns", MIN_SCALE_COLUMNS, MAX_SCALE_COLUMNS)
}

private fun checkPolearmHead(head: PolearmHead) = when (head) {
    NoPolearmHead -> doNothing()
    RoundedPolearmHead -> doNothing()
    SharpenedPolearmHead -> doNothing()
    is PolearmHeadWithSegments -> doNothing()
    is PolearmHeadWithAxeHead -> checkPolearmFixation(head.fixation)
    is PolearmHeadWithSpearHead -> {
        checkPolearmFixation(head.fixation)
        checkSpearHead(head.spear)
    }
}

private fun checkPolearmFixation(fixation: HeadFixation) = when (fixation) {
    NoHeadFixation -> doNothing()
    is BoundHeadHead -> checkFixationLength(fixation.length)
    is Langets -> validateFactor(fixation.length, "Langets Length", MIN_LANGETS_LENGTH, MAX_LANGETS_LENGTH)
    is SocketedHeadHead -> checkFixationLength(fixation.length)
}

private fun checkFixationLength(length: Factor) =
    validateFactor(length, "Fixation Length", MIN_FIXATION_LENGTH, MAX_FIXATION_LENGTH)

private fun checkSpearHead(head: SpearHead) {
    validateFactor(head.length, "Spearhead Length", MIN_SPEAR_LENGTH, MAX_SPEAR_LENGTH)
    validateFactor(head.width, "Spearhead Width", MIN_SPEAR_WIDTH, MAX_SPEAR_WIDTH)
}

private fun checkOneHandedSword(sword: OneHandedSword) {
    checkBlade(sword.blade, MIN_1H_BLADE_LENGTH, MAX_1H_BLADE_LENGTH)
    checkSwordHilt(sword.hilt)
}

private fun checkTwoHandedSword(sword: TwoHandedSword) {
    checkBlade(sword.blade, MIN_2H_BLADE_LENGTH, MAX_2H_BLADE_LENGTH)
    checkSwordHilt(sword.hilt)
}

private fun checkBlade(
    blade: Blade,
    minLength: Factor,
    maxLength: Factor,
) = when (blade) {
    is SimpleBlade -> {
        validateFactor(blade.length, "Blade Length", minLength, maxLength)
        validateFactor(blade.width, "Blade Width", MIN_BLADE_WIDTH, MAX_BLADE_WIDTH)
    }
}

private fun checkSwordHilt(hilt: SwordHilt) = when (hilt) {
    is SimpleSwordHilt -> {
        checkSwordGuard(hilt.guard)
        checkSwordGrip(hilt.grip)
    }
}

private fun checkSwordGuard(guard: SwordGuard) = when (guard) {
    NoSwordGuard -> doNothing()
    is SimpleSwordGuard -> {
        validateFactor(guard.height, "Guard Height", MIN_GUARD_HEIGHT, MAX_GUARD_HEIGHT)
        validateFactor(guard.width, "Guard Width", MIN_GUARD_WIDTH, MAX_GUARD_WIDTH)
    }
}

private fun checkSwordGrip(grip: Grip) = when (grip) {
    is SimpleGrip -> doNothing()
    is BoundGrip -> checkInt(grip.rows, "Grip's Rows", MIN_GRIP_ROWS, MAX_GRIP_ROWS)
}
