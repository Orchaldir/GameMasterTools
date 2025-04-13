package at.orchaldir.gm.core.reducer.culture

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteFashion
import at.orchaldir.gm.core.action.UpdateFashion
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.beard.BeardStyleType
import at.orchaldir.gm.core.model.character.appearance.hair.HairStyle
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.fashion.AppearanceStyle
import at.orchaldir.gm.core.model.culture.fashion.ClothingSet
import at.orchaldir.gm.core.model.culture.fashion.ClothingStyle
import at.orchaldir.gm.core.model.culture.fashion.Fashion
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.model.util.GenderMap
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.OneOrNone
import at.orchaldir.gm.core.reducer.*
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class FashionTest {

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an existing fashion`() {
            val state = State(Storage(Fashion(FASHION_ID_0)))
            val action = DeleteFashion(FASHION_ID_0)

            assertEquals(0, REDUCER.invoke(state, action).first.getFashionStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteFashion(FASHION_ID_0)

            assertIllegalArgument("Requires unknown Fashion 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete a fashion used by a culture`() {
            val culture = Culture(CULTURE_ID_0, fashion = GenderMap(FASHION_ID_0))
            val state = State(listOf(Storage(culture), Storage(Fashion(FASHION_ID_0))))
            val action = DeleteFashion(FASHION_ID_0)

            assertIllegalArgument("Fashion 0 is used") { REDUCER.invoke(state, action) }
        }
    }

    @Nested
    inner class UpdateAppearanceStyleTest {

        @Test
        fun `Some beard styles require at least 1 goatee`() {
            val state = State(Storage(Fashion(FASHION_ID_0)))
            val style = AppearanceStyle(
                OneOf(BeardStyleType.Goatee),
                OneOrNone(),
            )
            val fashion = Fashion(FASHION_ID_0, appearance = style)
            val action = UpdateFashion(fashion)

            assertIllegalArgument("Available beard styles requires at least 1 goatee!") {
                REDUCER.invoke(state, action)
            }
        }

        @Test
        fun `Some beard styles require at least 1 moustache`() {
            val state = State(Storage(Fashion(FASHION_ID_0)))
            val style = AppearanceStyle(
                OneOf(BeardStyleType.Moustache),
                moustacheStyles = OneOrNone(),
            )
            val fashion = Fashion(FASHION_ID_0, appearance = style)
            val action = UpdateFashion(fashion)

            assertIllegalArgument("Available beard styles requires at least 1 moustache!") {
                REDUCER.invoke(state, action)
            }
        }

        @Test
        fun `Requires at least 1 bun style`() {
            val state = State(Storage(Fashion(FASHION_ID_0)))
            val style = AppearanceStyle(
                hairStyles = OneOf(HairStyle.Bun),
                bunStyles = OneOrNone(),
            )
            val fashion = Fashion(FASHION_ID_0, appearance = style)
            val action = UpdateFashion(fashion)

            assertIllegalArgument("Requires at least 1 bun style!") {
                REDUCER.invoke(state, action)
            }
        }

        @Test
        fun `Requires at least 1 long hair style`() {
            val state = State(Storage(Fashion(FASHION_ID_0)))
            val style = AppearanceStyle(
                hairStyles = OneOf(HairStyle.Long),
                longHairStyles = OneOrNone(),
            )
            val fashion = Fashion(FASHION_ID_0, appearance = style)
            val action = UpdateFashion(fashion)

            assertIllegalArgument("Requires at least 1 long hair style!") {
                REDUCER.invoke(state, action)
            }
        }

        @Test
        fun `Requires at least 1 ponytail style`() {
            val state = State(Storage(Fashion(FASHION_ID_0)))
            val style = AppearanceStyle(
                hairStyles = OneOf(HairStyle.Ponytail),
                ponytailStyles = OneOrNone(),
            )
            val fashion = Fashion(FASHION_ID_0, appearance = style)
            val action = UpdateFashion(fashion)

            assertIllegalArgument("Requires at least 1 ponytail style!") {
                REDUCER.invoke(state, action)
            }
        }

        @Test
        fun `Requires at least 1 ponytail position`() {
            val state = State(Storage(Fashion(FASHION_ID_0)))
            val style = AppearanceStyle(
                hairStyles = OneOf(HairStyle.Ponytail),
                ponytailPositions = OneOrNone(),
            )
            val fashion = Fashion(FASHION_ID_0, appearance = style)
            val action = UpdateFashion(fashion)

            assertIllegalArgument("Requires at least 1 ponytail position!") {
                REDUCER.invoke(state, action)
            }
        }

    }
    @Nested
    inner class UpdateClothingStyleTest {

        @Test
        fun `Successfully update a fashion`() {
            val state =
                State(listOf(Storage(Fashion(FASHION_ID_0)), Storage(Equipment(EQUIPMENT_ID_0, data = Dress()))))
            val style = ClothingStyle(
                clothingSets = OneOf(ClothingSet.Dress),
                equipmentRarityMap = mapOf(
                    EquipmentDataType.Dress to OneOrNone(EQUIPMENT_ID_0),
                    EquipmentDataType.Hat to OneOrNone()
                )
            )
            val fashion = Fashion(FASHION_ID_0, clothing = style)
            val resultStyle = ClothingStyle(
                clothingSets = OneOf(ClothingSet.Dress),
                equipmentRarityMap = mapOf(EquipmentDataType.Dress to OneOrNone(EQUIPMENT_ID_0))
            )
            val result = Fashion(FASHION_ID_0, clothing = resultStyle)
            val action = UpdateFashion(fashion)

            assertEquals(result, REDUCER.invoke(state, action).first.getFashionStorage().get(FASHION_ID_0))
        }

        @Test
        fun `Cannot update unknown fashion`() {
            val action = UpdateFashion(Fashion(FASHION_ID_0))

            assertIllegalArgument("Requires unknown Fashion 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot use unknown equipment`() {
            val state = State(Storage(Fashion(FASHION_ID_0)))
            val style = ClothingStyle(equipmentRarityMap = mapOf(EquipmentDataType.Dress to OneOrNone(EQUIPMENT_ID_0)))
            val fashion = Fashion(FASHION_ID_0, clothing = style)
            val action = UpdateFashion(fashion)

            assertIllegalArgument("Requires unknown Equipment 0!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Clothing set Dress requires at least 1 dress`() {
            val state = State(Storage(Fashion(FASHION_ID_0)))
            val style = ClothingStyle(clothingSets = OneOf(ClothingSet.Dress))
            val fashion = Fashion(FASHION_ID_0, clothing = style)
            val action = UpdateFashion(fashion)

            assertIllegalArgument("Clothing set Dress requires at least one Dress!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Equipment must have the correct type`() {
            val state =
                State(listOf(Storage(Fashion(FASHION_ID_0)), Storage(Equipment(EQUIPMENT_ID_0, data = Hat()))))
            val style = ClothingStyle(
                clothingSets = OneOf(ClothingSet.Dress),
                equipmentRarityMap = mapOf(EquipmentDataType.Dress to OneOrNone(EQUIPMENT_ID_0))
            )
            val fashion = Fashion(FASHION_ID_0, clothing = style)
            val action = UpdateFashion(fashion)

            assertIllegalArgument("Type Dress has item 0 of wrong type!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Clothing set PantsAndShirt requires at least 1 pants`() {
            testSetWith2Items(
                ClothingSet.PantsAndShirt,
                EquipmentDataType.Shirt,
                Shirt(),
                "Clothing set PantsAndShirt requires at least one Pants!"
            )
        }

        @Test
        fun `Clothing set PantsAndShirt requires at least 1 shirt`() {
            testSetWith2Items(
                ClothingSet.PantsAndShirt,
                EquipmentDataType.Pants,
                Pants(),
                "Clothing set PantsAndShirt requires at least one Shirt!"
            )
        }

        @Test
        fun `Clothing set ShirtAndSkirt requires at least 1 shirt`() {
            testSetWith2Items(
                ClothingSet.ShirtAndSkirt,
                EquipmentDataType.Skirt,
                Skirt(),
                "Clothing set ShirtAndSkirt requires at least one Shirt!"
            )
        }

        @Test
        fun `Clothing set ShirtAndSkirt requires at least 1 skirt`() {
            testSetWith2Items(
                ClothingSet.ShirtAndSkirt,
                EquipmentDataType.Shirt,
                Shirt(),
                "Clothing set ShirtAndSkirt requires at least one Skirt!"
            )
        }

        @Test
        fun `Clothing set Suit requires at least 1 Coat`() {
            testSuit(
                EquipmentDataType.Pants,
                EquipmentDataType.Shirt,
                Pants(),
                Shirt(),
                "Clothing set Suit requires at least one Coat!"
            )
        }

        @Test
        fun `Clothing set Suit requires at least 1 Pants`() {
            testSuit(
                EquipmentDataType.Coat,
                EquipmentDataType.Shirt,
                Coat(),
                Shirt(),
                "Clothing set Suit requires at least one Pants!"
            )
        }

        @Test
        fun `Clothing set Suit requires at least 1 Shirt`() {
            testSuit(
                EquipmentDataType.Coat,
                EquipmentDataType.Pants,
                Coat(),
                Pants(),
                "Clothing set Suit requires at least one Shirt!"
            )
        }

        private fun testSetWith2Items(
            set: ClothingSet,
            type: EquipmentDataType,
            equipment: EquipmentData,
            message: String,
        ) {
            val state = State(
                listOf(
                    Storage(Fashion(FASHION_ID_0)),
                    Storage(Equipment(EQUIPMENT_ID_0, data = equipment))
                )
            )
            val style = ClothingStyle(
                clothingSets = OneOf(set),
                equipmentRarityMap = mapOf(type to OneOrNone(EQUIPMENT_ID_0))
            )
            val fashion = Fashion(FASHION_ID_0, clothing = style)
            val action = UpdateFashion(fashion)

            assertIllegalArgument(message) { REDUCER.invoke(state, action) }
        }

        private fun testSuit(
            type0: EquipmentDataType,
            type1: EquipmentDataType,
            equipment0: EquipmentData,
            equipment1: EquipmentData,
            message: String,
        ) {
            val state = State(
                listOf(
                    Storage(Fashion(FASHION_ID_0)),
                    Storage(
                        listOf(
                            Equipment(EQUIPMENT_ID_0, data = equipment0),
                            Equipment(EQUIPMENT_ID_1, data = equipment1)
                        )
                    ),
                )
            )
            val style = ClothingStyle(
                clothingSets = OneOf(ClothingSet.Suit),
                equipmentRarityMap = mapOf(type0 to OneOrNone(EQUIPMENT_ID_0), type1 to OneOrNone(EQUIPMENT_ID_1))
            )
            val fashion = Fashion(FASHION_ID_0, clothing = style)
            val action = UpdateFashion(fashion)

            assertIllegalArgument(message) { REDUCER.invoke(state, action) }
        }
    }

}