package at.orchaldir.gm.core.model.item.equipment

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import kotlin.test.Test

class EquipmentSlotTest {

    @Nested
    inner class GetAllBodySlotCombinationsTest {

        @Test
        fun `An empty input results in an empty output`() {
            assertEquals(emptySet<Set<BodySlot>>(), emptySet<EquipmentSlot>().getAllBodySlotCombinations())
        }
    }
}