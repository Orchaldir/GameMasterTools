package at.orchaldir.gm.visualization.grammar

import at.orchaldir.gm.core.model.util.part.MadeFromStone
import at.orchaldir.gm.core.model.visualization.BrickPattern
import at.orchaldir.gm.core.model.visualization.BrickPatternGrammar
import at.orchaldir.gm.core.model.visualization.RectangularShapeGrammar
import at.orchaldir.gm.core.model.visualization.RowsAndColumns
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.map.assertTilemap
import at.orchaldir.gm.visualization.grammar.brick.Brick
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

    private val line_3_ExW2 = listOf(null, brickW2, null)
    private val line_3_SxW2 = listOf(singleBrick, brickW2, null)
    private val line_3_W2xS = listOf(brickW2, null, singleBrick)
    private val line_3_W2xW2 = listOf(brickW2, null, brickW2)
    private val line_4_SxW2xS = listOf(singleBrick, brickW2, null, singleBrick)
    private val line_4_W2xW2 = listOf(brickW2, null, brickW2, null)
    private val line_4_W2xH3xH3 = listOf(brickW2, null, brickH3, brickH3)
    private val line_4_ExExH3xH3 = listOf(null, null, brickH3, brickH3)
    private val line_4_H3xH3xH3xS = listOf(brickH3, brickH3, brickH3, singleBrick)
    private val line_4_H3xH3xH3xW3 = listOf(brickH3, brickH3, brickH3, brickW3)
    private val line_4_H3xH3xExE = listOf(brickH3, brickH3, null, null)
    private val line_4_ExExExH3 = listOf(null, null, null, brickH3)
    private val line_4_ExExExE = listOf(null, null, null, null)
    private val line_4_ExExW2 = listOf(null, null, brickW2, null)
    private val line_4_W3xE = listOf(brickW3, null, null, null)
    private val line_5_ExExExW2 = listOf(null, null, null, brickW2, null)
    private val line_5_ExExW2xE = listOf(null, null, brickW2, null, null)
    private val line_5_ExH2xH2xW2 = listOf(null, brickH2, brickH2, brickW2, null)
    private val line_5_ExW2xExE = listOf(null, brickW2, null, null, null)
    private val line_5_H2xH2xW2xH2 = listOf(brickH2, brickH2, brickW2, null, brickH2)
    private val line_5_H2xW2xH2xH2 = listOf(brickH2, brickW2, null, brickH2, brickH2)
    private val line_5_SxExExW2 = listOf(singleBrick, null, null, brickW2, null)
    private val line_5_SxH2xH2xW2 = listOf(singleBrick, brickH2, brickH2, brickW2, null)
    private val line_5_W2xExE = listOf(brickW2, null, null, null)
    private val line_5_W2xExExS = line_5_W2xExE + singleBrick
    private val line_5_W2xExExW2 = line_5_W2xExE + brickW2
    private val line_5_W2xH2xH2 = listOf(brickW2, null, brickH2, brickH2)
    private val line_5_W2xH2xH2xS = line_5_W2xH2xH2 + singleBrick
    private val line_5_W2xH2xH2xW2 = line_5_W2xH2xH2 + brickW2
    private val line_6_ExExExExExE = listOf(null, null, null, null, null, null)
    private val line_6_ExH3xW3xE = listOf(null, brickH3, brickW3, null, null, null)
    private val line_6_ExExH3xW3 = listOf(null, null, brickH3, brickW3, null, null)
    private val line_6_SxSxSxH2xH2xH2 = listOf(singleBrick, singleBrick, singleBrick, brickH2, brickH2, brickH2)
    private val line_6_ExExExH3xH3xH3 = listOf(null, null, null, brickH3, brickH3, brickH3)
    private val line_6_ExExExSxSxS = listOf(null, null, null, singleBrick, singleBrick, singleBrick)
    private val line_6_ExExW2xExE = listOf(null, null, brickW2, null, null, null)
    private val line_6_H2xH2xW2xH2xH2 = listOf(brickH2, brickH2, brickW2, null, brickH2, brickH2)
    private val line_6_H3xH3xH3xW3 = listOf(brickH3, brickH3, brickH3, brickW3, null, null)
    private val line_6_H3xW3xExE = listOf(brickH3, brickW3, null, null, null, null)
    private val line_5_H3xW3xE = listOf(brickH3, brickW3, null, null, null,)
    private val line_6_H2xH2xH2xW3 = listOf(brickH2, brickH2, brickH2, brickW3, null, null)
    private val line_6_SxSxW2xSxS = listOf(singleBrick, singleBrick, brickW2, null, singleBrick, singleBrick)
    private val line_6_SxExExH2xW2 = listOf(singleBrick, null, null, brickH2, brickW2, null)
    private val line_5_SxExExH2xW3 = listOf(singleBrick, null, null, brickH2, brickW3)
    private val line_5_ExExExH2xW3 = listOf(singleBrick, null, null, brickH2, brickW3)
    private val line_6_W2xExExW2 = listOf(brickW2, null, null, null, brickW2, null)
    private val line_6_W2xH2xH2xW2 = listOf(brickW2, null, brickH2, brickH2, brickW2, null)
    private val line_6_W2xExExSxS = listOf(brickW2, null, null, null, singleBrick, singleBrick)
    private val line_6_W3xExExE = listOf(brickW3, null, null, null, null, null)
    private val line_6_W3xSxH2xH3 = listOf(brickW3, null, null, singleBrick, brickH2, brickH3)
    private val line_5_W3xSxH2 = listOf(brickW3, null, null, singleBrick, brickH2)
    private val line_5_H3xW3xS = listOf(brickH3, brickW3, null, null, singleBrick)
    private val line_5_ExH3xW3 = listOf(null, brickH3, brickW3, null, null)
    private val line_5_ExExH3xW2 = listOf(null, null, brickH3, brickW2, null)
    private val line_5_ExExH3xW3 = listOf(null, null, brickH3, brickW3, null)
    private val line_5_ExExExH3xS = listOf(null, null, null, brickH3, singleBrick)
    private val line_5_ExExExExH2 = listOf(null, null, null, null, brickH2)
    private val line_5_ExExExExE = listOf(null, null, null, null, null)
    private val line_5_W3xExE = listOf(brickW3, null, null, null, null)
    private val line_9_W3xH3xH3xH3xW3 = listOf(brickW3, null, null, brickH3, brickH3, brickH3, brickW3, null, null)
    private val line_9_W3xExExExW3 = listOf(brickW3, null, null, null, null, null, brickW3, null, null)
    private val line_9_H3xH3xH3xW3xH3xH3xH3 = listOf(brickH3, brickH3, brickH3, brickW3, null, null, brickH3, brickH3, brickH3)
    private val line_9_ExExExW3xExExE = listOf(null, null, null, brickW3, null, null, null, null, null)

    private val basketWeaves_H3_V3_H3 = listOf(line_9_W3xH3xH3xH3xW3, line_9_W3xExExExW3, line_9_W3xExExExW3)
    private val basketWeaves_V3_H3_V3 = listOf(line_9_H3xH3xH3xW3xH3xH3xH3, line_9_ExExExW3xExExE, line_9_ExExExW3xExExE)

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
                val expected = listOf(line_6_W2xH2xH2xW2, line_6_W2xExExW2, line_6_H2xH2xW2xH2xH2, line_6_ExExW2xExE)

                test(tileX, tileY, expected)
            }

            private fun testPatterB(tileX: Int, tileY: Int) {
                val expected = listOf(line_6_H2xH2xW2xH2xH2, line_6_ExExW2xExE, line_6_W2xH2xH2xW2, line_6_W2xExExW2)

                test(tileX, tileY, expected)
            }
        }

        @Nested
        inner class BorderCuttingOffBricksTest {
            @Test
            fun `Left border`() {
                val expected = listOf(line_5_SxH2xH2xW2, line_5_SxExExW2, line_5_H2xW2xH2xH2, line_5_ExW2xExE)

                testWithLeftAndRight(1, true, true, expected)
            }

            @Test
            fun `Right border`() {
                val expected = listOf(line_5_W2xH2xH2xS, line_5_W2xExExS, line_5_H2xH2xW2xH2, line_5_ExExW2xE)

                testWithLeftAndRight(0, true, true, expected)
            }

            @Test
            fun `Bottom border`() {
                val expected = listOf(line_6_W2xH2xH2xW2, line_6_W2xExExW2, line_6_SxSxW2xSxS)

                testWithTopAndBottom(0, true, true, expected)
            }

            @Test
            fun `Top border`() {
                val expected = listOf(line_6_SxSxW2xSxS, line_6_W2xH2xH2xW2, line_6_W2xExExW2)

                testWithTopAndBottom(1, true, true, expected)
            }
        }

        @Nested
        inner class BricksAcrossTheBorderTest {

            @Test
            fun `Left border`() {
                val expected = listOf(line_5_ExH2xH2xW2, line_5_ExExExW2, line_5_H2xW2xH2xH2, line_5_ExW2xExE)

                testWithLeftAndRight(1, false, true, expected)
            }

            @Test
            fun `Right border`() {
                val expected = listOf(line_5_W2xH2xH2xW2, line_5_W2xExExW2, line_5_H2xH2xW2xH2, line_5_ExExW2xE)

                testWithLeftAndRight(0, true, false, expected)
            }

            @Test
            fun `Bottom border`() {
                val expected = listOf(line_6_W2xH2xH2xW2, line_6_W2xExExW2, line_6_H2xH2xW2xH2xH2)

                testWithTopAndBottom(0, true, false, expected)
            }

            @Test
            fun `Top border`() {
                val expected = listOf(line_6_ExExW2xExE, line_6_W2xH2xH2xW2, line_6_W2xExExW2)

                testWithTopAndBottom(1, false, true, expected)
            }
        }

        private fun test(tileX: Int, tileY: Int, expected: List<List<Brick?>>) {
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

        private fun testWithLeftAndRight(tileX: Int, isLeft: Boolean, isRight: Boolean, expected: List<List<Brick?>>) =
            test(
                MapSize2d(5, 4),
                Borders(true, isLeft, isRight, true, tileX),
                expected,
            )

        private fun testWithTopAndBottom(tileY: Int, isTop: Boolean, isBottom: Boolean, expected: List<List<Brick?>>) =
            test(
                MapSize2d(6, 3),
                Borders(isBottom, true, true, isTop, 0, tileY),
                expected,
            )

        private fun test(size: MapSize2d, borders: Borders, expected: List<List<Brick?>>) =
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

            private fun testPatterA(tileX: Int, tileY: Int) = test(tileX, tileY,
                basketWeaves_H3_V3_H3 + basketWeaves_V3_H3_V3,
            )

            private fun testPatterB(tileX: Int, tileY: Int) = test(tileX, tileY,
                basketWeaves_V3_H3_V3 + basketWeaves_H3_V3_H3,
            )
        }

        private fun test(tileX: Int, tileY: Int, expected: List<List<Brick?>>) {
            val size = MapSize2d(9, 6)
            val borders = Borders(true, tileX, tileY)

            test(size, borders, expected)
        }

        private fun test(size: MapSize2d, borders: Borders, expected: List<List<Brick?>>) =
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
                val expected = listOf(line_6_H3xH3xH3xW3, line_6_ExExExH3xH3xH3, line_6_ExExExExExE, line_6_W3xExExE)

                test(tileX, tileY, expected,)
            }
        }

        @Nested
        inner class BorderCuttingOffBricksTest {

            @Test
            fun `Left border`() {
                val expected = listOf(line_4_W2xH3xH3, line_4_H3xH3xExE, line_4_ExExExE, line_4_ExExW2)

                testWithLeftAndRight(1, true, true, expected)
            }

            @Test
            fun `Right border`() {
                val expected = listOf(line_4_H3xH3xH3xS, line_4_ExExExH3, line_4_ExExExE, line_4_W3xE)

                testWithLeftAndRight(0, true, true, expected)
            }

            @Test
            fun `Bottom border`() {
                val expected = listOf(line_6_H2xH2xH2xW3, line_6_ExExExSxSxS)

                testWithTopAndBottom(0, true, true, expected)
            }

            @Test
            fun `Top border`() {
                val expected = listOf(line_6_SxSxSxH2xH2xH2, line_6_W3xExExE)

                testWithTopAndBottom(1, true, true, expected)
            }
        }

        @Nested
        inner class BricksAcrossTheBorderTest {

            @Test
            fun `Left border`() {
                val expected = listOf(line_4_ExExH3xH3, line_4_H3xH3xExE, line_4_ExExExE, line_4_ExExW2)

                testWithLeftAndRight(1, false, true, expected)
            }

            @Test
            fun `Right border`() {
                val expected = listOf(line_4_H3xH3xH3xW3, line_4_ExExExH3, line_4_ExExExE, line_4_W3xE)

                testWithLeftAndRight(0, true, false, expected)
            }

            @Test
            fun `Bottom border`() {
                val expected = listOf(line_6_H3xH3xH3xW3, line_6_ExExExH3xH3xH3)

                testWithTopAndBottom(0, true, false, expected)
            }

            @Test
            fun `Top border`() {
                val expected = listOf(line_6_ExExExExExE, line_6_W3xExExE)

                testWithTopAndBottom(1, false, true, expected)
            }
        }

        private fun testWithLeftAndRight(tileX: Int, isLeft: Boolean, isRight: Boolean, expected: List<List<Brick?>>) =
            test(
                MapSize2d(4, 4),
                Borders(true, isLeft, isRight, true, tileX),
                expected,
            )

        private fun testWithTopAndBottom(tileY: Int, isTop: Boolean, isBottom: Boolean, expected: List<List<Brick?>>) =
            test(
                MapSize2d(6, 2),
                Borders(isBottom, true, true, isTop, 0, tileY),
                expected,
            )

        private fun test(tileX: Int, tileY: Int, expected: List<List<Brick?>>) {
            val size = MapSize2d(6, 4)
            val borders = Borders(true, tileX, tileY)

            test(size, borders, expected)
        }

        private fun test(size: MapSize2d, borders: Borders, expected: List<List<Brick?>>) =
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

            private fun testDefault(tileX: Int, tileY: Int) = test(tileX, tileY, listOf(
                line_6_W3xSxH2xH3,
                        line_6_H3xW3xExE,
                        line_6_ExH3xW3xE,
                        line_6_ExExH3xW3,
                        line_6_SxExExH2xW2,
                        line_6_W2xExExSxS,
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

            /*

            @Test
            fun `Bottom border`() {
                val expected = listOf(line_6_H3xH3xH3xW3, line_6_ExExExH3xH3xH3)

                testWithTopAndBottom(0, true, false, expected)
            }

            @Test
            fun `Top border`() {
                val expected = listOf(line_6_ExExExExExE, line_6_W3xExExE)

                testWithTopAndBottom(1, false, true, expected)
            }
            */

            private fun testAcrossLeftBorder(tileY: Int) {
                val expected = listOf(
                    line_5_H3xW3xS,
                    line_5_ExH3xW3,
                    line_5_ExExH3xW2,
                    line_5_ExExExH3xS,
                    line_5_ExExExExH2,
                    line_5_W3xExE,
                )

                testWithLeftAndRight(1, tileY, false, true, expected)
            }

            private fun testAcrossRightBorder(tileY: Int) {
                val expected = listOf(
                    line_5_W3xSxH2,
                    line_5_H3xW3xE,
                    line_5_ExH3xW3,
                    line_5_ExExH3xW3,
                    line_5_SxExExH2xW3,
                    line_5_W2xExExS,
                )

                testWithLeftAndRight(0, tileY, true, false, expected)
            }
        }

        private fun testWithLeftAndRight(tileX: Int, tileY: Int, isLeft: Boolean, isRight: Boolean, expected: List<List<Brick?>>) =
            test(
                MapSize2d(5, 6),
                Borders(true, isLeft, isRight, true, tileX, tileY),
                expected,
            )

        private fun testWithTopAndBottom(tileY: Int, isTop: Boolean, isBottom: Boolean, expected: List<List<Brick?>>) =
            test(
                MapSize2d(6, 5),
                Borders(isBottom, true, true, isTop, 0, tileY),
                expected,
            )


        private fun test(tileX: Int, tileY: Int, expected: List<List<Brick?>>) {
            val size = MapSize2d.square(6)
            val borders = Borders(true, tileX, tileY)

            test(size, borders, expected)
        }

        private fun test(size: MapSize2d, borders: Borders, expected: List<List<Brick?>>) =
            test(size, BrickPattern.Herringbone, 3, borders, expected)
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

        private fun testWithPartialBricks(tileX: Int, isLeft: Boolean, isRight: Boolean, expected: List<List<Brick?>>) {
            val size = MapSize2d(3, 2)
            val borders = Borders(true, isLeft, isRight, true, tileX)

            test(size, borders, expected)
        }

        private fun test(size: MapSize2d, borders: Borders, expected: List<List<Brick?>>) =
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

        private fun testWithPartialBricks(tileX: Int, isBorder: Boolean, expected: List<List<Brick?>>) =
            test(MapSize2d(3, 2), tileX, isBorder, expected)

        private fun test(size: MapSize2d, tileX: Int, isBorder: Boolean, expected: List<List<Brick?>>) =
            test(size, BrickPattern.Stack, 2, Borders(isBorder, tileX), expected)
    }

    private fun test(
        size: MapSize2d,
        pattern: BrickPattern,
        length: Int,
        borders: Borders,
        expected: List<List<Brick?>>,
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