package at.orchaldir.gm.core.model.item.equipment

enum class EquipmentSlot {
    BeltSlot,
    BottomSlot,
    EarSlot,
    EyeSlot,
    EyesSlot,
    FootSlot,
    FootUnderwearSlot,
    HandSlot,
    HeadSlot,
    HeldInOneHandSlot,
    HeldInTwoHandsSlot,
    InnerTopSlot,
    NeckSlot,
    OuterSlot,
    TopSlot;

    fun toBodySlots() = when (this) {
        BeltSlot -> setOf(setOf(BodySlot.Belt))
        BottomSlot -> setOf(setOf(BodySlot.Bottom))
        EarSlot -> setOf(setOf(BodySlot.LeftEar), setOf(BodySlot.RightEar))
        EyeSlot -> setOf(setOf(BodySlot.LeftEye), setOf(BodySlot.RightEye))
        EyesSlot -> setOf(setOf(BodySlot.LeftEye, BodySlot.RightEye))
        FootSlot -> setOf(setOf(BodySlot.Foot))
        FootUnderwearSlot -> setOf(setOf(BodySlot.FootUnderwear))
        HandSlot -> setOf(setOf(BodySlot.Hand))
        HeadSlot -> setOf(setOf(BodySlot.Head))
        HeldInOneHandSlot -> setOf(setOf(BodySlot.HeldInLeftHand), setOf(BodySlot.HeldInLeftHand))
        HeldInTwoHandsSlot -> setOf(setOf(BodySlot.HeldInLeftHand, BodySlot.HeldInLeftHand))
        InnerTopSlot -> setOf(setOf(BodySlot.InnerTop))
        NeckSlot -> setOf(setOf(BodySlot.Neck))
        OuterSlot -> setOf(setOf(BodySlot.Outer))
        TopSlot -> setOf(setOf(BodySlot.Top))
    }
}

fun Set<EquipmentSlot>.getAllBodySlotCombinations(): Set<Set<BodySlot>> {
    val slotCombinations: MutableSet<MutableSet<BodySlot>> = mutableSetOf()

    this.forEach { equipmentSlot ->
        val isFirst = slotCombinations.isEmpty()

        equipmentSlot.toBodySlots().forEach { bodySlots ->
            if (isFirst) {
                slotCombinations.add(bodySlots.toMutableSet())
            } else {
                slotCombinations.forEach { set -> set.addAll(bodySlots) }
            }
        }
    }

    return slotCombinations
}