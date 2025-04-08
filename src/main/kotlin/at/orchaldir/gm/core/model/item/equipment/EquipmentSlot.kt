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
    OuterSlot,
    TieSlot,
    TopSlot;

    fun toBodySlots() = when (this) {
        BeltSlot -> setOf(setOf(BodySlot.BeltSlot))
        BottomSlot -> setOf(setOf(BodySlot.BottomSlot))
        EarSlot -> setOf(setOf(BodySlot.EarSlotLeft), setOf(BodySlot.EarSlotRight))
        EyeSlot -> setOf(setOf(BodySlot.EyeSlotLeft), setOf(BodySlot.EyeSlotRight))
        EyesSlot -> setOf(setOf(BodySlot.EyeSlotLeft, BodySlot.EyeSlotRight))
        FootSlot -> setOf(setOf(BodySlot.FootSlot))
        FootUnderwearSlot -> setOf(setOf(BodySlot.FootUnderwearSlot))
        HandSlot -> setOf(setOf(BodySlot.HandSlot))
        HeadSlot -> setOf(setOf(BodySlot.HeadSlot))
        OuterSlot -> setOf(setOf(BodySlot.OuterSlot))
        TieSlot -> setOf(setOf(BodySlot.TieSlot))
        TopSlot -> setOf(setOf(BodySlot.TopSlot))
    }
}