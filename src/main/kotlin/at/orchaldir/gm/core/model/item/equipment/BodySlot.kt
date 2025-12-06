package at.orchaldir.gm.core.model.item.equipment

import at.orchaldir.gm.core.model.item.equipment.BodySlot.IounStone0
import at.orchaldir.gm.core.model.item.equipment.BodySlot.IounStone1
import at.orchaldir.gm.core.model.item.equipment.BodySlot.IounStone2
import at.orchaldir.gm.core.model.item.equipment.BodySlot.IounStone3

// Outer > X > Inner X > Under X
enum class BodySlot {
    Belt,
    Bottom,
    Foot,
    FootUnderwear,
    Hand,
    Head,
    HeldInLeftHand,
    HeldInRightHand,
    InnerTop,
    IounStone0,
    IounStone1,
    IounStone2,
    IounStone3,
    LeftEar,
    LeftEye,
    Neck,
    Outer,
    RightEar,
    RightEye,
    Top;

    companion object {

        fun getIounStoneSlot(index: Int) = when (index) {
            0 -> IounStone0
            1 -> IounStone1
            2 -> IounStone2
            3 -> IounStone3
            else -> error("Unsupported index $index for ioun stone slot!")
        }

    }
}