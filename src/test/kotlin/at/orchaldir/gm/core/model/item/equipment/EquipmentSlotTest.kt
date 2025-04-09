package at.orchaldir.gm.core.model.item.equipment

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import kotlin.test.Test

class EquipmentSlotTest {

    @Nested
    inner class GetAllBodySlotCombinationsTest {

        @Test
        fun `An empty input results in an empty output`() {
            assertEquals(
                emptySet<Set<BodySlot>>(),
                emptySet<EquipmentSlot>().getAllBodySlotCombinations()
            )
        }

        @Test
        fun `A single equipment slot that is a single body slot`() {
            assertEquals(
                setOf(setOf(BodySlot.Belt)),
                setOf(EquipmentSlot.BeltSlot).getAllBodySlotCombinations()
            )
        }

        @Test
        fun `A single equipment slot that is a set of two body slots`() {
            assertEquals(
                setOf(setOf(BodySlot.LeftEye, BodySlot.RightEye)),
                setOf(EquipmentSlot.EyesSlot).getAllBodySlotCombinations()
            )
        }

        @Test
        fun `A single equipment slot that is two sets of 1 body slot`() {
            assertEquals(
                setOf(setOf(BodySlot.LeftEar), setOf(BodySlot.RightEar)),
                setOf(EquipmentSlot.EarSlot).getAllBodySlotCombinations()
            )
        }

        @Test
        fun `Two equipment slots that are a set of two body slots`() {
            assertEquals(
                setOf(setOf(BodySlot.Top, BodySlot.Bottom)),
                setOf(EquipmentSlot.TopSlot, EquipmentSlot.BottomSlot).getAllBodySlotCombinations()
            )
        }
    }
}