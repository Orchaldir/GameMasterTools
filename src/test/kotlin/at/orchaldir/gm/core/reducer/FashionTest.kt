package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.DeleteFashion
import at.orchaldir.gm.core.action.UpdateFashion
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.appearance.GenderMap
import at.orchaldir.gm.core.model.appearance.OneOf
import at.orchaldir.gm.core.model.appearance.OneOrNone
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.fashion.ClothingSet
import at.orchaldir.gm.core.model.fashion.Fashion
import at.orchaldir.gm.core.model.fashion.FashionId
import at.orchaldir.gm.core.model.item.*
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val ID0 = FashionId(0)
private val CULTURE0 = CultureId(0)
private val ITEM0 = ItemTemplateId(0)
private val ITEM1 = ItemTemplateId(1)

class FashionTest {

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an existing fashion`() {
            val state = State(fashion = Storage(listOf(Fashion(ID0))))
            val action = DeleteFashion(ID0)

            assertEquals(0, REDUCER.invoke(state, action).first.fashion.getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteFashion(ID0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete a fashion used by a culture`() {
            val culture = Culture(CULTURE0, clothingStyles = GenderMap(ID0))
            val state = State(
                cultures = Storage(listOf(culture)),
                fashion = Storage(listOf(Fashion(ID0)))
            )
            val action = DeleteFashion(ID0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Successfully update a fashion`() {
            val state = State(
                fashion = Storage(listOf(Fashion(ID0))),
                itemTemplates = Storage(listOf(ItemTemplate(ITEM0, equipment = Dress()))),
            )
            val fashion = Fashion(
                ID0,
                clothingSets = OneOf(ClothingSet.Dress),
                itemRarityMap = mapOf(EquipmentType.Dress to OneOrNone(ITEM0))
            )
            val action = UpdateFashion(fashion)

            assertEquals(fashion, REDUCER.invoke(state, action).first.fashion.get(ID0))
        }

        @Test
        fun `Cannot update unknown fashion`() {
            val action = UpdateFashion(Fashion(ID0))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot use unknown item templates`() {
            val state = State(fashion = Storage(listOf(Fashion(ID0))))
            val fashion = Fashion(ID0, itemRarityMap = mapOf(EquipmentType.Dress to OneOrNone(ITEM0)))
            val action = UpdateFashion(fashion)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Clothing set Dress requires at least 1 dress`() {
            val state = State(fashion = Storage(listOf(Fashion(ID0))))
            val fashion = Fashion(ID0, clothingSets = OneOf(ClothingSet.Dress))
            val action = UpdateFashion(fashion)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Clothing set PantsAndShirt requires at least 1 pants`() {
            val state = State(
                fashion = Storage(listOf(Fashion(ID0))),
                itemTemplates = Storage(listOf(ItemTemplate(ITEM0, equipment = Shirt()))),
            )
            val fashion = Fashion(
                ID0,
                clothingSets = OneOf(ClothingSet.PantsAndShirt),
                itemRarityMap = mapOf(EquipmentType.Shirt to OneOrNone(ITEM0))
            )
            val action = UpdateFashion(fashion)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Clothing set PantsAndShirt requires at least 1 shirt`() {
            val state = State(
                fashion = Storage(listOf(Fashion(ID0))),
                itemTemplates = Storage(listOf(ItemTemplate(ITEM0, equipment = Pants()))),
            )
            val fashion = Fashion(
                ID0,
                clothingSets = OneOf(ClothingSet.PantsAndShirt),
                itemRarityMap = mapOf(EquipmentType.Pants to OneOrNone(ITEM0))
            )
            val action = UpdateFashion(fashion)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Clothing set ShirtAndSkirt requires at least 1 shirt`() {
            val state = State(
                fashion = Storage(listOf(Fashion(ID0))),
                itemTemplates = Storage(listOf(ItemTemplate(ITEM0, equipment = Skirt()))),
            )
            val fashion = Fashion(
                ID0,
                clothingSets = OneOf(ClothingSet.ShirtAndSkirt),
                itemRarityMap = mapOf(EquipmentType.Skirt to OneOrNone(ITEM0))
            )
            val action = UpdateFashion(fashion)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Clothing set ShirtAndSkirt requires at least 1 skirt`() {
            val state = State(
                fashion = Storage(listOf(Fashion(ID0))),
                itemTemplates = Storage(listOf(ItemTemplate(ITEM0, equipment = Shirt()))),
            )
            val fashion = Fashion(
                ID0,
                clothingSets = OneOf(ClothingSet.ShirtAndSkirt),
                itemRarityMap = mapOf(EquipmentType.Shirt to OneOrNone(ITEM0))
            )
            val action = UpdateFashion(fashion)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Clothing set Suit requires at least 1 Coat`() {
            testSuit(EquipmentType.Pants, EquipmentType.Shirt, Pants(), Shirt())
        }

        @Test
        fun `Clothing set Suit requires at least 1 Pants`() {
            testSuit(EquipmentType.Coat, EquipmentType.Shirt, Coat(), Shirt())
        }

        @Test
        fun `Clothing set Suit requires at least 1 Shirt`() {
            testSuit(EquipmentType.Coat, EquipmentType.Pants, Coat(), Pants())
        }


        private fun testSuit(type0: EquipmentType, type1: EquipmentType, equipment0: Equipment, equipment1: Equipment) {
            val state = State(
                fashion = Storage(listOf(Fashion(ID0))),
                itemTemplates = Storage(
                    listOf(
                        ItemTemplate(ITEM0, equipment = equipment0),
                        ItemTemplate(ITEM1, equipment = equipment1)
                    )
                ),
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