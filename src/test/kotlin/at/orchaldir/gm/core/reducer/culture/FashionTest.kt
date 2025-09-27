package at.orchaldir.gm.core.reducer.culture

import at.orchaldir.gm.EQUIPMENT_ID_0
import at.orchaldir.gm.EQUIPMENT_ID_1
import at.orchaldir.gm.FASHION_ID_0
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.action.UpdateAction
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.beard.BeardStyleType
import at.orchaldir.gm.core.model.character.appearance.hair.HairStyle
import at.orchaldir.gm.core.model.culture.fashion.*
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.OneOrNone
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class FashionTest {

    @Nested
    inner class UpdateBeardFashionTest {

        @Test
        fun `Some beard styles require at least 1 goatee`() {
            assertBeardFashion(
                BeardFashion(
                    OneOf(BeardStyleType.Goatee),
                    goateeStyles = OneOrNone(),
                ),
                "Available beard styles require at least 1 goatee style!"
            )
        }

        @Test
        fun `Some beard styles require at least 1 moustache`() {
            assertBeardFashion(
                BeardFashion(
                    OneOf(BeardStyleType.Moustache),
                    moustacheStyles = OneOrNone(),
                ),
                "Available beard styles require at least 1 moustache style!"
            )
        }

        @Test
        fun `A full beard requires at least 1 full beard style`() {
            assertBeardFashion(
                BeardFashion(
                    OneOf(BeardStyleType.Full),
                    fullBeardStyles = OneOrNone(),
                ),
                "Available beard styles require at least 1 full beard style!"
            )
        }

        @Test
        fun `Some beard styles require at least 1 beard length`() {
            assertBeardFashion(
                BeardFashion(
                    OneOf(BeardStyleType.Full),
                    beardLength = OneOrNone(),
                ),
                "Available beard styles require at least 1 beard length!"
            )
        }

        private fun assertBeardFashion(beardFashion: BeardFashion, message: String) {
            val state = State(Storage(Fashion(FASHION_ID_0)))
            val fashion = Fashion(FASHION_ID_0, appearance = AppearanceFashion(beard = beardFashion))
            val action = UpdateAction(fashion)

            assertIllegalArgument(message) {
                REDUCER.invoke(state, action)
            }
        }
    }

    @Nested
    inner class UpdateHairFashionTest {

        @Test
        fun `Requires at least 1 bun style`() {
            assertHairFashion(
                HairFashion(
                    OneOf(HairStyle.Bun),
                    bunStyles = OneOrNone(),
                ),
                "Requires at least 1 bun style!"
            )
        }

        @Test
        fun `Requires at least 1 long hair style`() {
            assertHairFashion(
                HairFashion(
                    OneOf(HairStyle.Long),
                    longHairStyles = OneOrNone(),
                ),
                "Requires at least 1 long hair style!"
            )
        }

        @Test
        fun `Requires at least 1 ponytail style`() {
            assertHairFashion(
                HairFashion(
                    OneOf(HairStyle.Ponytail),
                    ponytailStyles = OneOrNone(),
                ),
                "Requires at least 1 ponytail style!"
            )
        }

        @Test
        fun `Requires at least 1 ponytail position`() {
            assertHairFashion(
                HairFashion(
                    OneOf(HairStyle.Ponytail),
                    ponytailPositions = OneOrNone(),
                ),
                "Requires at least 1 ponytail position!"
            )
        }

        @Test
        fun `Requires at least 1 short style`() {
            assertHairFashion(
                HairFashion(
                    OneOf(HairStyle.Short),
                    shortHairStyles = OneOrNone(),
                ),
                "Requires at least 1 short hair style!"
            )
        }

        @Test
        fun `Requires at least 1 hair length`() {
            assertHairFashion(
                HairFashion(
                    OneOf(HairStyle.Long),
                    hairLengths = OneOrNone(),
                ),
                "Available hair styles require at least 1 hair length!"
            )
        }

        private fun assertHairFashion(hairFashion: HairFashion, message: String) {
            val state = State(Storage(Fashion(FASHION_ID_0)))
            val fashion = Fashion(FASHION_ID_0, appearance = AppearanceFashion(hair = hairFashion))
            val action = UpdateAction(fashion)

            assertIllegalArgument(message) {
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
            val style = ClothingFashion(
                clothingSets = OneOf(ClothingSet.Dress),
                equipmentRarityMap = mapOf(
                    EquipmentDataType.Dress to OneOrNone(EQUIPMENT_ID_0),
                    EquipmentDataType.Hat to OneOrNone()
                )
            )
            val fashion = Fashion(FASHION_ID_0, clothing = style)
            val resultStyle = ClothingFashion(
                clothingSets = OneOf(ClothingSet.Dress),
                equipmentRarityMap = mapOf(EquipmentDataType.Dress to OneOrNone(EQUIPMENT_ID_0))
            )
            val result = Fashion(FASHION_ID_0, clothing = resultStyle)
            val action = UpdateAction(fashion)

            assertEquals(result, REDUCER.invoke(state, action).first.getFashionStorage().get(FASHION_ID_0))
        }

        @Test
        fun `Cannot update unknown fashion`() {
            val action = UpdateAction(Fashion(FASHION_ID_0))

            assertIllegalArgument("Requires unknown Fashion 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot use unknown equipment`() {
            val state = State(Storage(Fashion(FASHION_ID_0)))
            val style =
                ClothingFashion(equipmentRarityMap = mapOf(EquipmentDataType.Dress to OneOrNone(EQUIPMENT_ID_0)))
            val fashion = Fashion(FASHION_ID_0, clothing = style)
            val action = UpdateAction(fashion)

            assertIllegalArgument("Requires unknown Equipment 0!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Clothing set Dress requires at least 1 dress`() {
            val state = State(Storage(Fashion(FASHION_ID_0)))
            val style = ClothingFashion(clothingSets = OneOf(ClothingSet.Dress))
            val fashion = Fashion(FASHION_ID_0, clothing = style)
            val action = UpdateAction(fashion)

            assertIllegalArgument("Clothing set Dress requires at least one Dress!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Equipment must have the correct type`() {
            val state =
                State(listOf(Storage(Fashion(FASHION_ID_0)), Storage(Equipment(EQUIPMENT_ID_0, data = Hat()))))
            val style = ClothingFashion(
                clothingSets = OneOf(ClothingSet.Dress),
                equipmentRarityMap = mapOf(EquipmentDataType.Dress to OneOrNone(EQUIPMENT_ID_0))
            )
            val fashion = Fashion(FASHION_ID_0, clothing = style)
            val action = UpdateAction(fashion)

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
                "Clothing set Suit requires at least one SuitJacket!"
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
            val style = ClothingFashion(
                clothingSets = OneOf(set),
                equipmentRarityMap = mapOf(type to OneOrNone(EQUIPMENT_ID_0))
            )
            val fashion = Fashion(FASHION_ID_0, clothing = style)
            val action = UpdateAction(fashion)

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
            val style = ClothingFashion(
                clothingSets = OneOf(ClothingSet.Suit),
                equipmentRarityMap = mapOf(type0 to OneOrNone(EQUIPMENT_ID_0), type1 to OneOrNone(EQUIPMENT_ID_1))
            )
            val fashion = Fashion(FASHION_ID_0, clothing = style)
            val action = UpdateAction(fashion)

            assertIllegalArgument(message) { REDUCER.invoke(state, action) }
        }
    }

}