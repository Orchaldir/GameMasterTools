package at.orchaldir.gm.core.model.item.equipment

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

        fun getIounStoneMaxIndex() = getIounStoneNumber() - 1

        fun getIounStoneNumber() = 4
    }

    fun getIounStoneIndex() = getOptionalIounStoneIndex()
        ?: error("No index, because $this is not an ioun stone!")

    fun getOptionalIounStoneIndex() = when (this) {
        IounStone0 -> 0
        IounStone1 -> 1
        IounStone2 -> 2
        IounStone3 -> 3
        else -> null
    }
}