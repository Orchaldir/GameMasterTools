package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.DeleteFashion
import at.orchaldir.gm.core.action.UpdateFashion
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.fashion.ClothingSet
import at.orchaldir.gm.core.model.fashion.Fashion
import at.orchaldir.gm.core.model.fashion.FashionId
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.model.util.GenderMap
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.OneOrNone
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val ID0 = FashionId(0)
private val CULTURE0 = CultureId(0)
private val ITEM0 = EquipmentId(0)
private val ITEM1 = EquipmentId(1)

class FashionTest {

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an existing fashion`() {
            val state = State(Storage(Fashion(ID0)))
            val action = DeleteFashion(ID0)

            assertEquals(0, REDUCER.invoke(state, action).first.getFashionStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteFashion(ID0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete a fashion used by a culture`() {
            val culture = Culture(CULTURE0, clothingStyles = GenderMap(ID0))
            val state = State(listOf(Storage(culture), Storage(Fashion(ID0))))
            val action = DeleteFashion(ID0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Successfully update a fashion`() {
            val state =
                State(listOf(Storage(Fashion(ID0)), Storage(ItemTemplate(ITEM0, equipment = Dress()))))
            val fashion = Fashion(
                ID0,
                clothingSets = OneOf(ClothingSet.Dress),
                itemRarityMap = mapOf(EquipmentDataType.Dress to OneOrNone(ITEM0), EquipmentDataType.Hat to OneOrNone())
            )
            val result = Fashion(
                ID0,
                clothingSets = OneOf(ClothingSet.Dress),
                itemRarityMap = mapOf(EquipmentDataType.Dress to OneOrNone(ITEM0))
            )
            val action = UpdateFashion(fashion)

            assertEquals(result, REDUCER.invoke(state, action).first.getFashionStorage().get(ID0))
        }

        @Test
        fun `Cannot update unknown fashion`() {
            val action = UpdateFashion(Fashion(ID0))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot use unknown item templates`() {
            val state = State(Storage(Fashion(ID0)))
            val fashion = Fashion(ID0, itemRarityMap = mapOf(EquipmentDataType.Dress to OneOrNone(ITEM0)))
            val action = UpdateFashion(fashion)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Clothing set Dress requires at least 1 dress`() {
            val state = State(Storage(Fashion(ID0)))
            val fashion = Fashion(ID0, clothingSets = OneOf(ClothingSet.Dress))
            val action = UpdateFashion(fashion)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Equipment must have the correct type`() {
            val state =
                State(listOf(Storage(Fashion(ID0)), Storage(ItemTemplate(ITEM0, equipment = Hat()))))
            val fashion = Fashion(
                ID0,
                clothingSets = OneOf(ClothingSet.Dress),
                itemRarityMap = mapOf(EquipmentDataType.Dress to OneOrNone(ITEM0))
            )
            val action = UpdateFashion(fashion)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Clothing set PantsAndShirt requires at least 1 pants`() {
            testSetWith2Items(ClothingSet.PantsAndShirt, EquipmentDataType.Shirt, Shirt())
        }

        @Test
        fun `Clothing set PantsAndShirt requires at least 1 shirt`() {
            testSetWith2Items(ClothingSet.PantsAndShirt, EquipmentDataType.Pants, Pants())
        }

        @Test
        fun `Clothing set ShirtAndSkirt requires at least 1 shirt`() {
            testSetWith2Items(ClothingSet.ShirtAndSkirt, EquipmentDataType.Skirt, Skirt())
        }

        @Test
        fun `Clothing set ShirtAndSkirt requires at least 1 skirt`() {
            testSetWith2Items(ClothingSet.ShirtAndSkirt, EquipmentDataType.Shirt, Shirt())
        }

        @Test
        fun `Clothing set Suit requires at least 1 Coat`() {
            testSuit(EquipmentDataType.Pants, EquipmentDataType.Shirt, Pants(), Shirt())
        }

        @Test
        fun `Clothing set Suit requires at least 1 Pants`() {
            testSuit(EquipmentDataType.Coat, EquipmentDataType.Shirt, Coat(), Shirt())
        }

        @Test
        fun `Clothing set Suit requires at least 1 Shirt`() {
            testSuit(EquipmentDataType.Coat, EquipmentDataType.Pants, Coat(), Pants())
        }

        private fun testSetWith2Items(set: ClothingSet, type: EquipmentDataType, equipment: EquipmentData) {
            val state = State(
                listOf(
                    Storage(Fashion(ID0)),
                    Storage(ItemTemplate(ITEM0, equipment = equipment))
                )
            )
            val fashion = Fashion(
                ID0,
                clothingSets = OneOf(set),
                itemRarityMap = mapOf(type to OneOrNone(ITEM0))
            )
            val action = UpdateFashion(fashion)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }

        private fun testSuit(
            type0: EquipmentDataType,
            type1: EquipmentDataType,
            equipment0: EquipmentData,
            equipment1: EquipmentData,
        ) {
            val state = State(
                listOf(
                    Storage(Fashion(ID0)),
                    Storage(
                        listOf(
                            ItemTemplate(ITEM0, equipment = equipment0),
                            ItemTemplate(ITEM1, equipment = equipment1)
                        )
                    ),
                )
            )
            val fashion = Fashion(
                ID0,
                clothingSets = OneOf(ClothingSet.Suit),
                itemRarityMap = mapOf(type0 to OneOrNone(ITEM0), type1 to OneOrNone(ITEM1))
            )
            val action = UpdateFashion(fashion)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }
    }

}