package at.orchaldir.gm.visualization.grammar

import at.orchaldir.gm.core.model.util.part.MadeFromStone
import at.orchaldir.gm.core.model.visualization.BrickPattern
import at.orchaldir.gm.core.model.visualization.BrickPatternGrammar
import at.orchaldir.gm.core.model.visualization.RectangularShapeGrammar
import at.orchaldir.gm.core.model.visualization.RowsAndColumns
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.map.assertTilemap
import at.orchaldir.gm.visualization.grammar.brick.Brick
import at.orchaldir.gm.visualization.grammar.brick.BrickTile
import at.orchaldir.gm.visualization.grammar.brick.EmptyTile
import at.orchaldir.gm.visualization.grammar.brick.OccupiedTile
import at.orchaldir.gm.visualization.grammar.brick.createBrickPattern
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class BrickPatternCreationTest {

    private val brickGrammar = RectangularShapeGrammar(MadeFromStone())
    private val singleBrick = Brick(brickGrammar, MapSize2d(1, 1))
    private val brickW2 = Brick(brickGrammar, MapSize2d(2, 1))
    private val brickW3 = Brick(brickGrammar, MapSize2d(3, 1))
    private val brickH2 = Brick(brickGrammar, MapSize2d(1, 2))
    private val brickH3 = Brick(brickGrammar, MapSize2d(1, 3))
    private val brick22 = Brick(brickGrammar, MapSize2d(2, 2))
    private val brick23 = Brick(brickGrammar, MapSize2d(2, 3))
    private val brick32 = Brick(brickGrammar, MapSize2d(3, 2))
    private val empty = EmptyTile
    private val occupied = OccupiedTile

    private val line_3_ExW2 = listOf(empty, brickW2, occupied)
    private val line_3_SxW2 = listOf(singleBrick, brickW2, occupied)
    private val line_3_W2xS = listOf(brickW2, occupied, singleBrick)
    private val line_3_W2xW2 = listOf(brickW2, occupied, brickW2)
    private val line_4_ExExH3xH3 = listOf(empty, empty, brickH3, brickH3)
    private val line_4_H3xH3xH3xS = listOf(brickH3, brickH3, brickH3, singleBrick)
    private val line_4_H3xH3xH3xW3 = listOf(brickH3, brickH3, brickH3, brickW3)
    private val line_4_H3xH3xOxO = listOf(brickH3, brickH3, occupied, occupied)
    private val line_4_H3xW3 = listOf(brickH3, brickW3, occupied, occupied)
    private val line_4_Ox22xH3 = listOf(occupied, brick22, occupied, brickH3)
    private val line_4_OxOxOxH3 = listOf(occupied, occupied, occupied, brickH3)
    private val line_4_OxOxOxO = listOf(occupied, occupied, occupied, occupied)
    private val line_4_OxOxW2 = listOf(occupied, occupied, brickW2, occupied)
    private val line_4_SxW2xS = listOf(singleBrick, brickW2, occupied, singleBrick)
    private val line_4_W2xH3xH3 = listOf(brickW2, occupied, brickH3, brickH3)
    private val line_4_W2xW2 = listOf(brickW2, occupied, brickW2, occupied)
    private val line_4_W3xO = listOf(brickW3, occupied, occupied, occupied)
    private val line_5_22xOxOxS = listOf(brick22, occupied, occupied, occupied, singleBrick)
    private val line_5_23x32 = listOf(brick23, occupied, brick32, occupied, occupied)
    private val line_5_32 = listOf(brick32, occupied, occupied, occupied, occupied)
    private val line_5_ExExOxOxH2 = listOf(empty, empty, occupied, occupied, brickH2)
    private val line_5_ExH2xH2xW2 = listOf(empty, brickH2, brickH2, brickW2, occupied)
    private val line_5_ExOxOxH3xS = listOf(empty, occupied, occupied, brickH3, singleBrick)
    private val line_5_ExOxOxSxH2 = listOf(empty, occupied, occupied, singleBrick, brickH2)
    private val line_5_ExOxOxW2 = listOf(empty, occupied, occupied, brickW2, occupied)
    private val line_5_ExSx22xO = listOf(empty, singleBrick, brick22, occupied, occupied)
    private val line_5_H2xH2xW2xH2 = listOf(brickH2, brickH2, brickW2, occupied, brickH2)
    private val line_5_H2xOxOxSx22 = listOf(brickH2, occupied, occupied, singleBrick, brick22)
    private val line_5_H2xOxOxSxH2 = listOf(brickH2, occupied, occupied, singleBrick, brickH2)
    private val line_5_H2xW2xH2xH2 = listOf(brickH2, brickW2, occupied, brickH2, brickH2)
    private val line_5_H3xH3xW3 = listOf(brickH3, brickH3, brickW3, occupied, occupied)
    private val line_5_H3xW3xO = listOf(brickH3, brickW3, occupied, occupied, occupied)
    private val line_5_H3xW3xS = listOf(brickH3, brickW3, occupied, occupied, singleBrick)
    private val line_5_OxH3xW3 = listOf(occupied, brickH3, brickW3, occupied, occupied)
    private val line_5_OxOxH3xW2 = listOf(occupied, occupied, brickH3, brickW2, occupied)
    private val line_5_OxOxH3xW3 = listOf(occupied, occupied, brickH3, brickW3, occupied)
    private val line_5_OxOxOxOxO = listOf(occupied, occupied, occupied, occupied, occupied)
    private val line_5_OxOxSx22 = listOf(occupied, occupied, singleBrick, brick22, occupied)
    private val line_5_OxOxSx23 = listOf(occupied, occupied, singleBrick, brick23, occupied)
    private val line_5_OxOxSxH3xH3 = listOf(occupied, occupied, singleBrick, brickH3, brickH3)
    private val line_5_OxOxSxW2 = listOf(occupied, occupied, singleBrick, brickW2, occupied)
    private val line_5_OxOxW2xE = listOf(occupied, occupied, brickW2, occupied, occupied)
    private val line_5_OxOxW3 = listOf(occupied, occupied, brickW3, occupied, occupied)
    private val line_5_OxSx22xE = listOf(occupied, singleBrick, brick22, occupied, occupied)
    private val line_5_OxW2xExE = listOf(occupied, brickW2, occupied, occupied, occupied)
    private val line_5_Sx22xExE = listOf(singleBrick, brick22, occupied, empty, empty)
    private val line_5_Sx22xW2 = listOf(singleBrick, brick22, occupied, brickW2, occupied)
    private val line_5_SxH2xH2xW2 = listOf(singleBrick, brickH2, brickH2, brickW2, occupied)
    private val line_5_SxOxOxH2xW3 = listOf(singleBrick, occupied, occupied, brickH2, brickW3)
    private val line_5_SxOxOxW2 = listOf(singleBrick, occupied, occupied, brickW2, occupied)
    private val line_5_W2xH2xH2 = listOf(brickW2, occupied, brickH2, brickH2)
    private val line_5_W2xH2xH2xS = line_5_W2xH2xH2 + singleBrick
    private val line_5_W2xH2xH2xW2 = line_5_W2xH2xH2 + brickW2
    private val line_5_W2xOxO = listOf(brickW2, occupied, occupied, occupied)
    private val line_5_W2xOxOxS = line_5_W2xOxO + singleBrick
    private val line_5_W2xOxOxW2 = line_5_W2xOxO + brickW2
    private val line_5_W3xOxO = listOf(brickW3, occupied, occupied, occupied, occupied)
    private val line_5_W3xSxH2 = listOf(brickW3, occupied, occupied, singleBrick, brickH2)
    private val line_6_ExExExExExE = listOf(empty, empty, empty, empty, empty, empty)
    private val line_6_ExExExSxSxS = listOf(occupied, occupied, occupied, singleBrick, singleBrick, singleBrick)
    private val line_6_ExExW2xExE = listOf(empty, empty, brickW2, occupied, empty, empty)
    private val line_6_H2xH2xH2xW3 = listOf(brickH2, brickH2, brickH2, brickW3, occupied, occupied)
    private val line_6_H2xH2xW2xH2xH2 = listOf(brickH2, brickH2, brickW2, occupied, brickH2, brickH2)
    private val line_6_H3xH3xH3xW3 = listOf(brickH3, brickH3, brickH3, brickW3, occupied, occupied)
    private val line_6_H3xW3xOxO = listOf(brickH3, brickW3, occupied, occupied, occupied, occupied)
    private val line_6_OxH2xW3xO = listOf(occupied, brickH2, brickW3, occupied, occupied, occupied)
    private val line_6_OxH3xW3xO = listOf(occupied, brickH3, brickW3, occupied, occupied, occupied)
    private val line_6_OxOxH3xW3 = listOf(occupied, occupied, brickH3, brickW3, occupied, occupied)
    private val line_6_OxOxOxH3xH3xH3 = listOf(occupied, occupied, occupied, brickH3, brickH3, brickH3)
    private val line_6_OxOxOxOxOxO = listOf(occupied, occupied, occupied, occupied, occupied, occupied)
    private val line_6_OxOxSxW3 = listOf(occupied, occupied, singleBrick, brickW3, occupied, occupied)
    private val line_6_OxOxW2xOxO = listOf(occupied, occupied, brickW2, occupied, occupied, occupied)
    private val line_6_SxOxOxH2xW2 = listOf(singleBrick, occupied, occupied, brickH2, brickW2, occupied)
    private val line_6_SxOxOxH3xW2 = listOf(singleBrick, occupied, occupied, brickH3, brickW2, occupied)
    private val line_6_SxSxSxH2xH2xH2 = listOf(singleBrick, singleBrick, singleBrick, brickH2, brickH2, brickH2)
    private val line_6_SxSxW2xSxS = listOf(singleBrick, singleBrick, brickW2, occupied, singleBrick, singleBrick)
    private val line_6_W2xExExH3xS = listOf(brickW2, occupied, empty, empty, brickH3, singleBrick)
    private val line_6_W2xH2xH2xW2 = listOf(brickW2, occupied, brickH2, brickH2, brickW2, occupied)
    private val line_6_W2xOxOxSxS = listOf(brickW2, occupied, occupied, occupied, singleBrick, singleBrick)
    private val line_6_W2xOxOxW2 = listOf(brickW2, occupied, occupied, occupied, brickW2, occupied)
    private val line_6_W3xExExE = listOf(brickW3, occupied, occupied, empty, empty, empty)
    private val line_6_W3xExOxH3 = listOf(brickW3, occupied, occupied, empty, occupied, brickH3)
    private val line_6_W3xOxOxO = listOf(brickW3, occupied, occupied, occupied, occupied, occupied)
    private val line_6_W3xSxH2xH3 = listOf(brickW3, occupied, occupied, singleBrick, brickH2, brickH3)
    private val line_9_H3xH3xH3xW3xH3xH3xH3 = listOf(brickH3, brickH3, brickH3, brickW3, occupied, occupied, brickH3, brickH3, brickH3)
    private val line_9_OxPxOxW3xOxOxO = listOf(occupied, occupied, occupied, brickW3, occupied, occupied, occupied, occupied, occupied)
    private val line_9_W3xH3xH3xH3xW3 = listOf(brickW3, occupied, occupied, brickH3, brickH3, brickH3, brickW3, occupied, occupied)
    private val line_9_W3xOxOxOxW3 = listOf(brickW3, occupied, occupied, occupied, occupied, occupied, brickW3, occupied, occupied)

    private val basketWeaves_H3_V3_H3 = listOf(line_9_W3xH3xH3xH3xW3, line_9_W3xOxOxOxW3, line_9_W3xOxOxOxW3)
    private val basketWeaves_V3_H3_V3 =
        listOf(line_9_H3xH3xH3xW3xH3xH3xH3, line_9_OxPxOxW3xOxOxO, line_9_OxPxOxW3xOxOxO)

    @Nested
    inner class BasketWeavePatternWithLength2Test {

        @Nested
        inner class HorizontalAndVerticalRepetitionTest {

            @Test
            fun `Test origin tile`() {
                testPatterA(0, 0)
            }

            @Test
            fun `Alternate Pattern 1 tile to the right`() {
                testPatterB(1, 0)
            }

            @Test
            fun `Same Pattern 2 tiles to the right`() {
                testPatterA(2, 0)
            }

            @Test
            fun `Same Pattern 1 tile to the bottom`() {
                testPatterA(0, 1)
                testPatterB(1, 1)
                testPatterA(2, 1)
            }

            @Test
            fun `Same Pattern 2 tiles to the bottom`() {
                testPatterA(0, 2)
                testPatterB(1, 2)
                testPatterA(2, 2)
            }

            private fun testPatterA(tileX: Int, tileY: Int) {
                val expected = listOf(line_6_W2xH2xH2xW2, line_6_W2xOxOxW2, line_6_H2xH2xW2xH2xH2, line_6_OxOxW2xOxO)

                test(tileX, tileY, expected)
            }

            private fun testPatterB(tileX: Int, tileY: Int) {
                val expected = listOf(line_6_H2xH2xW2xH2xH2, line_6_OxOxW2xOxO, line_6_W2xH2xH2xW2, line_6_W2xOxOxW2)

                test(tileX, tileY, expected)
            }
        }

        @Nested
        inner class BorderCuttingOffBricksTest {
            @Test
            fun `Left border`() {
                val expected = listOf(line_5_SxH2xH2xW2, line_5_SxOxOxW2, line_5_H2xW2xH2xH2, line_5_OxW2xExE)

                testWithLeftAndRight(1, true, true, expected)
            }

            @Test
            fun `Right border`() {
                val expected = listOf(line_5_W2xH2xH2xS, line_5_W2xOxOxS, line_5_H2xH2xW2xH2, line_5_OxOxW2xE)

                testWithLeftAndRight(0, true, true, expected)
            }

            @Test
            fun `Bottom border`() {
                val expected = listOf(line_6_W2xH2xH2xW2, line_6_W2xOxOxW2, line_6_SxSxW2xSxS)

                testWithTopAndBottom(0, true, true, expected)
            }

            @Test
            fun `Top border`() {
                val expected = listOf(line_6_SxSxW2xSxS, line_6_W2xH2xH2xW2, line_6_W2xOxOxW2)

                testWithTopAndBottom(1, true, true, expected)
            }
        }

        @Nested
        inner class BricksAcrossTheBorderTest {

            @Test
            fun `Left border`() {
                val expected = listOf(line_5_ExH2xH2xW2, line_5_ExOxOxW2, line_5_H2xW2xH2xH2, line_5_OxW2xExE)

                testWithLeftAndRight(1, false, true, expected)
            }

            @Test
            fun `Right border`() {
                val expected = listOf(line_5_W2xH2xH2xW2, line_5_W2xOxOxW2, line_5_H2xH2xW2xH2, line_5_OxOxW2xE)

                testWithLeftAndRight(0, true, false, expected)
            }

            @Test
            fun `Bottom border`() {
                val expected = listOf(line_6_W2xH2xH2xW2, line_6_W2xOxOxW2, line_6_H2xH2xW2xH2xH2)

                testWithTopAndBottom(0, true, false, expected)
            }

            @Test
            fun `Top border`() {
                val expected = listOf(line_6_ExExW2xExE, line_6_W2xH2xH2xW2, line_6_W2xOxOxW2)

                testWithTopAndBottom(1, false, true, expected)
            }
        }

        private fun test(tileX: Int, tileY: Int, expected: List<List<BrickTile>>) {
            val size = MapSize2d(6, 4)
            val pattern = BrickPatternGrammar(
                brickGrammar,
                RowsAndColumns(size),
                BrickPattern.BasketWeave,
                2,
            )

            val result = createBrickPattern(pattern, Borders(true, tileX, tileY))

            assertTilemap(result, size, expected)
        }

        private fun testWithLeftAndRight(tileX: Int, isLeft: Boolean, isRight: Boolean, expected: List<List<BrickTile>>) =
            test(
                MapSize2d(5, 4),
                Borders(true, isLeft, isRight, true, tileX),
                expected,
            )

        private fun testWithTopAndBottom(tileY: Int, isTop: Boolean, isBottom: Boolean, expected: List<List<BrickTile>>) =
            test(
                MapSize2d(6, 3),
                Borders(isBottom, true, true, isTop, 0, tileY),
                expected,
            )

        private fun test(size: MapSize2d, borders: Borders, expected: List<List<BrickTile>>) =
            test(size, BrickPattern.BasketWeave, 2, borders, expected)
    }

    @Nested
    inner class BasketWeavePatternWithLength3Test {

        @Nested
        inner class HorizontalAndVerticalRepetitionTest {

            @Test
            fun `Test origin tile`() {
                testPatterA(0, 0)
            }

            @Test
            fun `Alternate Pattern 1 tile to the right`() {
                testPatterB(1, 0)
            }

            @Test
            fun `Same Pattern 2 tiles to the right`() {
                testPatterA(2, 0)
            }

            @Test
            fun `Same Pattern 1 tile to the bottom`() {
                testPatterA(0, 1)
                testPatterB(1, 1)
                testPatterA(2, 1)
            }

            @Test
            fun `Same Pattern 2 tiles to the bottom`() {
                testPatterA(0, 2)
                testPatterB(1, 2)
                testPatterA(2, 2)
            }

            private fun testPatterA(tileX: Int, tileY: Int) = test(
                tileX, tileY,
                basketWeaves_H3_V3_H3 + basketWeaves_V3_H3_V3,
            )

            private fun testPatterB(tileX: Int, tileY: Int) = test(
                tileX, tileY,
                basketWeaves_V3_H3_V3 + basketWeaves_H3_V3_H3,
            )
        }

        private fun test(tileX: Int, tileY: Int, expected: List<List<BrickTile>>) {
            val size = MapSize2d(9, 6)
            val borders = Borders(true, tileX, tileY)

            test(size, borders, expected)
        }

        private fun test(size: MapSize2d, borders: Borders, expected: List<List<BrickTile>>) =
            test(size, BrickPattern.BasketWeave, 3, borders, expected)
    }

    @Nested
    inner class BasketWeaveSinglePatternWithLength3Test {

        @Nested
        inner class HorizontalAndVerticalRepetitionTest {

            @Test
            fun `Test origin tile`() {
                testDefault(0, 0)
            }

            @Test
            fun `Alternate Pattern 1 tile to the right`() {
                testDefault(1, 0)
            }

            @Test
            fun `Same Pattern 2 tiles to the right`() {
                testDefault(2, 0)
            }

            @Test
            fun `Same Pattern 1 tile to the bottom`() {
                testDefault(0, 1)
                testDefault(1, 1)
                testDefault(2, 1)
            }

            @Test
            fun `Same Pattern 2 tiles to the bottom`() {
                testDefault(0, 2)
                testDefault(1, 2)
                testDefault(2, 2)
            }

            private fun testDefault(tileX: Int, tileY: Int) {
                val expected = listOf(line_6_H3xH3xH3xW3, line_6_OxOxOxH3xH3xH3, line_6_OxOxOxOxOxO, line_6_W3xOxOxO)

                test(tileX, tileY, expected)
            }
        }

        @Nested
        inner class BorderCuttingOffBricksTest {

            @Test
            fun `Left border`() {
                val expected = listOf(line_4_W2xH3xH3, line_4_H3xH3xOxO, line_4_OxOxOxO, line_4_OxOxW2)

                testWithLeftAndRight(1, true, true, expected)
            }

            @Test
            fun `Right border`() {
                val expected = listOf(line_4_H3xH3xH3xS, line_4_OxOxOxH3, line_4_OxOxOxO, line_4_W3xO)

                testWithLeftAndRight(0, true, true, expected)
            }

            @Test
            fun `Bottom border`() {
                val expected = listOf(line_6_H2xH2xH2xW3, line_6_ExExExSxSxS)

                testWithTopAndBottom(0, true, true, expected)
            }

            @Test
            fun `Top border`() {
                val expected = listOf(line_6_SxSxSxH2xH2xH2, line_6_W3xOxOxO)

                testWithTopAndBottom(1, true, true, expected)
            }
        }

        @Nested
        inner class BricksAcrossTheBorderTest {

            @Test
            fun `Left border`() {
                val expected = listOf(line_4_ExExH3xH3, line_4_H3xH3xOxO, line_4_OxOxOxO, line_4_OxOxW2)

                testWithLeftAndRight(1, false, true, expected)
            }

            @Test
            fun `Right border`() {
                val expected = listOf(line_4_H3xH3xH3xW3, line_4_OxOxOxH3, line_4_OxOxOxO, line_4_W3xO)

                testWithLeftAndRight(0, true, false, expected)
            }

            @Test
            fun `Bottom border`() {
                val expected = listOf(line_6_H3xH3xH3xW3, line_6_OxOxOxH3xH3xH3)

                testWithTopAndBottom(0, true, false, expected)
            }

            @Test
            fun `Top border`() {
                val expected = listOf(line_6_ExExExExExE, line_6_W3xExExE)

                testWithTopAndBottom(1, false, true, expected)
            }
        }

        private fun testWithLeftAndRight(tileX: Int, isLeft: Boolean, isRight: Boolean, expected: List<List<BrickTile>>) =
            test(
                MapSize2d(4, 4),
                Borders(true, isLeft, isRight, true, tileX),
                expected,
            )

        private fun testWithTopAndBottom(tileY: Int, isTop: Boolean, isBottom: Boolean, expected: List<List<BrickTile>>) =
            test(
                MapSize2d(6, 2),
                Borders(isBottom, true, true, isTop, 0, tileY),
                expected,
            )

        private fun test(tileX: Int, tileY: Int, expected: List<List<BrickTile>>) {
            val size = MapSize2d(6, 4)
            val borders = Borders(true, tileX, tileY)

            test(size, borders, expected)
        }

        private fun test(size: MapSize2d, borders: Borders, expected: List<List<BrickTile>>) =
            test(size, BrickPattern.BasketWeaveSingle, 3, borders, expected)
    }

    @Nested
    inner class GridPatternTest {

        @Test
        fun `Test creating a grid`() {
            val size = MapSize2d(3, 2)
            val pattern = BrickPatternGrammar(
                brickGrammar,
                RowsAndColumns(size),
                BrickPattern.Grid,
            )

            val result = createBrickPattern(pattern, Borders())

            assertTilemap(result, size, singleBrick)
        }

    }

    @Nested
    inner class HerringbonePatternWithLength3Test {

        @Nested
        inner class HorizontalAndVerticalRepetitionTest {

            @Test
            fun `Test origin tile`() {
                testDefault(0, 0)
            }

            @Test
            fun `Alternate Pattern 1 tile to the right`() {
                testDefault(1, 0)
            }

            @Test
            fun `Same Pattern 2 tiles to the right`() {
                testDefault(2, 0)
            }

            @Test
            fun `Same Pattern 1 tile to the bottom`() {
                testDefault(0, 1)
                testDefault(1, 1)
                testDefault(2, 1)
            }

            @Test
            fun `Same Pattern 2 tiles to the bottom`() {
                testDefault(0, 2)
                testDefault(1, 2)
                testDefault(2, 2)
            }

            private fun testDefault(tileX: Int, tileY: Int) = test(
                tileX, tileY, listOf(
                    line_6_W3xSxH2xH3,
                    line_6_H3xW3xOxO,
                    line_6_OxH3xW3xO,
                    line_6_OxOxH3xW3,
                    line_6_SxOxOxH2xW2,
                    line_6_W2xOxOxSxS,
                )
            )
        }

        @Nested
        inner class BricksAcrossTheBorderTest {

            @Test
            fun `Left border`() {
                testAcrossLeftBorder(0)
            }

            @Test
            fun `Left border with y = 1`() {
                testAcrossLeftBorder(1)
            }

            @Test
            fun `Right border`() {
                testAcrossRightBorder(0)
            }

            @Test
            fun `Right border y = 1`() {
                testAcrossRightBorder(1)
            }

            @Test
            fun `Bottom border`() {
                testAcrossBottomBorder(0)
            }

            @Test
            fun `Bottom border with x = 1`() {
                testAcrossBottomBorder(1)
            }

            @Test
            fun `Top border`() {
                testAcrossTopBorder(0)
            }

            @Test
            fun `Top border with x = 1`() {
                testAcrossTopBorder(1)
            }

            private fun testAcrossBottomBorder(tileX: Int) {
                val expected = listOf(
                    line_6_W3xSxH2xH3,
                    line_6_H3xW3xOxO,
                    line_6_OxH3xW3xO,
                    line_6_OxOxH3xW3,
                    line_6_SxOxOxH3xW2,
                )

                testWithTopAndBottom(tileX, 0, true, false, expected)
            }

            private fun testAcrossLeftBorder(tileY: Int) {
                val expected = listOf(
                    line_5_H3xW3xS,
                    line_5_OxH3xW3,
                    line_5_OxOxH3xW2,
                    line_5_ExOxOxH3xS,
                    line_5_ExExOxOxH2,
                    line_5_W3xOxO,
                )

                testWithLeftAndRight(1, tileY, false, true, expected)
            }

            private fun testAcrossRightBorder(tileY: Int) {
                val expected = listOf(
                    line_5_W3xSxH2,
                    line_5_H3xW3xO,
                    line_5_OxH3xW3,
                    line_5_OxOxH3xW3,
                    line_5_SxOxOxH2xW3,
                    line_5_W2xOxOxS,
                )

                testWithLeftAndRight(0, tileY, true, false, expected)
            }

            private fun testAcrossTopBorder(tileX: Int) {
                val expected = listOf(
                    line_6_W2xExExH3xS,
                    line_6_W3xExOxH3,
                    line_6_H3xW3xOxO,
                    line_6_OxH2xW3xO,
                    line_6_OxOxSxW3,
                )

                testWithTopAndBottom(tileX, 1, false, true, expected)
            }
        }

        private fun testWithLeftAndRight(
            tileX: Int,
            tileY: Int,
            isLeft: Boolean,
            isRight: Boolean,
            expected: List<List<BrickTile>>,
        ) =
            test(
                MapSize2d(5, 6),
                Borders(true, isLeft, isRight, true, tileX, tileY),
                expected,
            )

        private fun testWithTopAndBottom(
            tileX: Int,
            tileY: Int,
            isTop: Boolean,
            isBottom: Boolean,
            expected: List<List<BrickTile>>,
        ) =
            test(
                MapSize2d(6, 5),
                Borders(isBottom, true, true, isTop, tileX, tileY),
                expected,
            )


        private fun test(tileX: Int, tileY: Int, expected: List<List<BrickTile>>) {
            val size = MapSize2d.square(6)
            val borders = Borders(true, tileX, tileY)

            test(size, borders, expected)
        }

        private fun test(size: MapSize2d, borders: Borders, expected: List<List<BrickTile>>) =
            test(size, BrickPattern.Herringbone, 3, borders, expected)
    }

    @Nested
    inner class PinwheelPatternWithLength3Test {

        @Nested
        inner class HorizontalAndVerticalRepetitionTest {

            @Test
            fun `Test origin tile`() {
                testDefault(0, 0)
            }

            @Test
            fun `Alternate Pattern 1 tile to the right`() {
                testDefault(1, 0)
            }

            @Test
            fun `Same Pattern 2 tiles to the right`() {
                testDefault(2, 0)
            }

            @Test
            fun `Same Pattern 1 tile to the bottom`() {
                testDefault(0, 1)
                testDefault(1, 1)
                testDefault(2, 1)
            }

            @Test
            fun `Same Pattern 2 tiles to the bottom`() {
                testDefault(0, 2)
                testDefault(1, 2)
                testDefault(2, 2)
            }

            private fun testDefault(tileX: Int, tileY: Int) {
                val expected = listOf(line_5_23x32, line_5_OxOxOxOxO, line_5_OxOxSx23, line_5_32, line_5_OxOxOxOxO)

                test(tileX, tileY, expected)
            }
        }

        private fun test(tileX: Int, tileY: Int, expected: List<List<BrickTile>>) {
            val size = MapSize2d(5, 5)
            val borders = Borders(true, tileX, tileY)

            test(size, borders, expected)
        }

        private fun test(size: MapSize2d, borders: Borders, expected: List<List<BrickTile>>) =
            test(size, BrickPattern.Pinwheel, 3, borders, expected)
    }

    @Nested
    inner class PinwheelSplitPatternWithLength3Test {

        @Nested
        inner class HorizontalAndVerticalRepetitionTest {

            @Test
            fun `Test origin tile`() {
                testDefault(0, 0)
            }

            @Test
            fun `Alternate Pattern 1 tile to the right`() {
                testDefault(1, 0)
            }

            @Test
            fun `Same Pattern 2 tiles to the right`() {
                testDefault(2, 0)
            }

            @Test
            fun `Same Pattern 1 tile to the bottom`() {
                testDefault(0, 1)
                testDefault(1, 1)
                testDefault(2, 1)
            }

            @Test
            fun `Same Pattern 2 tiles to the bottom`() {
                testDefault(0, 2)
                testDefault(1, 2)
                testDefault(2, 2)
            }

            private fun testDefault(tileX: Int, tileY: Int) {
                val expected = listOf(line_5_H3xH3xW3, line_5_OxOxW3, line_5_OxOxSxH3xH3, line_5_W3xOxO, line_5_W3xOxO)

                test(tileX, tileY, expected)
            }
        }

        private fun test(tileX: Int, tileY: Int, expected: List<List<BrickTile>>) {
            val size = MapSize2d(5, 5)
            val borders = Borders(true, tileX, tileY)

            test(size, borders, expected)
        }

        private fun test(size: MapSize2d, borders: Borders, expected: List<List<BrickTile>>) =
            test(size, BrickPattern.PinwheelSplit, 3, borders, expected)
    }

    @Nested
    inner class PinwheelWithBigCenterPatternWithLength3Test {

        @Nested
        inner class HorizontalAndVerticalRepetitionTest {

            @Test
            fun `Test origin tile`() {
                testDefault(0, 0)
            }

            @Test
            fun `Alternate Pattern 1 tile to the right`() {
                testDefault(1, 0)
            }

            @Test
            fun `Same Pattern 2 tiles to the right`() {
                testDefault(2, 0)
            }

            @Test
            fun `Same Pattern 1 tile to the bottom`() {
                testDefault(0, 1)
                testDefault(1, 1)
                testDefault(2, 1)
            }

            @Test
            fun `Same Pattern 2 tiles to the bottom`() {
                testDefault(0, 2)
                testDefault(1, 2)
                testDefault(2, 2)
            }

            private fun testDefault(tileX: Int, tileY: Int) {
                val expected = listOf(line_4_H3xW3, line_4_Ox22xH3, line_4_OxOxOxO, line_4_W3xO)

                test(tileX, tileY, expected)
            }
        }

        private fun test(tileX: Int, tileY: Int, expected: List<List<BrickTile>>) {
            val size = MapSize2d(4, 4)
            val borders = Borders(true, tileX, tileY)

            test(size, borders, expected)
        }

        private fun test(size: MapSize2d, borders: Borders, expected: List<List<BrickTile>>) =
            test(size, BrickPattern.PinwheelWithBigCenter, 3, borders, expected)
    }

    @Nested
    inner class PythagoreanTilingWithLength2Test {

        @Nested
        inner class HorizontalAndVerticalRepetitionTest {

            @Test
            fun `Test origin tile`() {
                testDefault(0, 0)
            }

            @Test
            fun `Alternate Pattern 1 tile to the right`() {
                testDefault(1, 0)
            }

            @Test
            fun `Same Pattern 2 tiles to the right`() {
                testDefault(2, 0)
            }

            @Test
            fun `Same Pattern 1 tile to the bottom`() {
                testDefault(0, 1)
                testDefault(1, 1)
                testDefault(2, 1)
            }

            @Test
            fun `Same Pattern 2 tiles to the bottom`() {
                testDefault(0, 2)
                testDefault(1, 2)
                testDefault(2, 2)
            }

            private fun testDefault(tileX: Int, tileY: Int) {
                val expected = listOf(
                    line_5_Sx22xW2,
                    line_5_H2xOxOxSxH2,
                    line_5_OxSx22xE,
                    line_5_22xOxOxS,
                    line_5_OxOxSxW2,
                )

                test(tileX, tileY, expected)
            }
        }

        @Nested
        inner class BricksAcrossTheBorderTest {

            @Test
            fun `Left border`() {
                testAcrossLeftBorder(0)
            }

            @Test
            fun `Left border with y = 1`() {
                testAcrossLeftBorder(1)
            }

            @Test
            fun `Right border`() {
                testAcrossRightBorder(0)
            }

            @Test
            fun `Right border y = 1`() {
                testAcrossRightBorder(1)
            }

            @Test
            fun `Bottom border`() {
                testAcrossBottomBorder(0)
            }

            @Test
            fun `Bottom border with x = 1`() {
                testAcrossBottomBorder(1)
            }

            @Test
            fun `Top border`() {
                testAcrossTopBorder(0)
            }

            @Test
            fun `Top border with x = 1`() {
                testAcrossTopBorder(1)
            }

            private fun testAcrossBottomBorder(tileX: Int) {
                val expected = listOf(
                    line_5_Sx22xW2,
                    line_5_H2xOxOxSxH2,
                    line_5_OxSx22xE,
                    line_5_22xOxOxS,
                    line_5_OxOxSx22,
                )

                test(tileX, 0, false, true, true, true, expected)
            }

            private fun testAcrossLeftBorder(tileY: Int) {
                val expected = listOf(
                    line_5_Sx22xW2,
                    line_5_ExOxOxSxH2,
                    line_5_ExSx22xO,
                    line_5_22xOxOxS,
                    line_5_OxOxSxW2,
                )

                test(1, tileY, true, false, true, true, expected)
            }

            private fun testAcrossRightBorder(tileY: Int) {
                val expected = listOf(
                    line_5_Sx22xW2,
                    line_5_H2xOxOxSx22,
                    line_5_OxSx22xE,
                    line_5_22xOxOxS,
                    line_5_OxOxSxW2,
                )

                test(0, tileY, true, true, false, true, expected)
            }

            private fun testAcrossTopBorder(tileX: Int) {
                val expected = listOf(
                    line_5_Sx22xExE,
                    line_5_H2xOxOxSxH2,
                    line_5_OxSx22xE,
                    line_5_22xOxOxS,
                    line_5_OxOxSxW2,
                )

                test(tileX, 1, true, true, true, false, expected)
            }
        }

        private fun test(
            tileX: Int,
            tileY: Int,
            isBottom: Boolean,
            isLeft: Boolean,
            isRight: Boolean,
            isTop: Boolean,
            expected: List<List<BrickTile>>,
        ) = test(
            Borders(isBottom, isLeft, isRight, isTop, tileX, tileY),
            expected,
        )

        private fun test(tileX: Int, tileY: Int, expected: List<List<BrickTile>>) =
            test(Borders(true, tileX, tileY), expected)

        private fun test(borders: Borders, expected: List<List<BrickTile>>) =
            test(MapSize2d.square(5), borders, expected)

        private fun test(size: MapSize2d, borders: Borders, expected: List<List<BrickTile>>) =
            test(size, BrickPattern.PythagoreanTiling, 2, borders, expected)
    }

    @Nested
    inner class RunningPatternTest {

        @Test
        fun `Even lines with full bricks`() {
            testWithoutPartialBricks(0)
        }

        @Test
        fun `Another tile with even lines with full bricks`() {
            testWithoutPartialBricks(1)
        }

        @Test
        fun `Even lines with partial bricks on the right border`() {
            val expected = listOf(line_3_W2xS, line_3_SxW2)

            testWithPartialBricks(0, true, true, expected)
        }

        @Test
        fun `Even lines with partial bricks on the left border`() {
            val expected = listOf(line_3_SxW2, line_3_W2xS)

            testWithPartialBricks(1, true, true, expected)
        }

        @Test
        fun `Even lines with bricks across the right border`() {
            val expected = listOf(line_3_W2xW2, line_3_SxW2)

            testWithPartialBricks(0, true, false, expected)
        }

        @Test
        fun `Even lines with bricks across the left border`() {
            val expected = listOf(line_3_ExW2, line_3_W2xS)

            testWithPartialBricks(1, false, true, expected)
        }

        private fun testWithoutPartialBricks(tileX: Int) {
            val size = MapSize2d(4, 2)
            val borders = Borders(true, tileX)

            test(size, borders, listOf(line_4_W2xW2, line_4_SxW2xS))
        }

        private fun testWithPartialBricks(tileX: Int, isLeft: Boolean, isRight: Boolean, expected: List<List<BrickTile>>) {
            val size = MapSize2d(3, 2)
            val borders = Borders(true, isLeft, isRight, true, tileX)

            test(size, borders, expected)
        }

        private fun test(size: MapSize2d, borders: Borders, expected: List<List<BrickTile>>) =
            test(size, BrickPattern.Running, 2, borders, expected)
    }

    @Nested
    inner class StackPatternTest {

        @Test
        fun `Without partial bricks`() {
            testWithoutPartialBricks(0)
        }

        @Test
        fun `Another tile without partial bricks`() {
            testWithoutPartialBricks(1)
        }

        @Test
        fun `Partial bricks on the right border`() {
            testWithPartialBricks(0, true, listOf(line_3_W2xS, line_3_W2xS))
        }

        @Test
        fun `Partial bricks on the left border`() {
            testWithPartialBricks(1, true, listOf(line_3_SxW2, line_3_SxW2))
        }

        @Test
        fun `Bricks across the right border`() {
            testWithPartialBricks(0, false, listOf(line_3_W2xW2, line_3_W2xW2))
        }

        @Test
        fun `Bricks across the left border`() {
            testWithPartialBricks(1, false, listOf(line_3_ExW2, line_3_ExW2))
        }

        private fun testWithoutPartialBricks(tileX: Int) {
            val expected = listOf(line_4_W2xW2, line_4_W2xW2)

            test(MapSize2d(4, 2), tileX, true, expected)
        }

        private fun testWithPartialBricks(tileX: Int, isBorder: Boolean, expected: List<List<BrickTile>>) =
            test(MapSize2d(3, 2), tileX, isBorder, expected)

        private fun test(size: MapSize2d, tileX: Int, isBorder: Boolean, expected: List<List<BrickTile>>) =
            test(size, BrickPattern.Stack, 2, Borders(isBorder, tileX), expected)
    }

    private fun test(
        size: MapSize2d,
        pattern: BrickPattern,
        length: Int,
        borders: Borders,
        expected: List<List<BrickTile>>,
    ) {
        val grammar = BrickPatternGrammar(
            brickGrammar,
            RowsAndColumns(size),
            pattern,
            length,
        )

        val result = createBrickPattern(grammar, borders)

        assertTilemap(result, size, expected)
    }
}