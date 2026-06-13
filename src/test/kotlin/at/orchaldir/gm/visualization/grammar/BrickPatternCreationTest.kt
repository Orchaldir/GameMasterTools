package at.orchaldir.gm.visualization.grammar

import at.orchaldir.gm.core.model.util.part.MadeFromStone
import at.orchaldir.gm.core.model.visualization.BrickPattern
import at.orchaldir.gm.core.model.visualization.BrickPatternGrammar
import at.orchaldir.gm.core.model.visualization.RectangularShapeGrammar
import at.orchaldir.gm.core.model.visualization.RowsAndColumns
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.map.assertTilemap
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class BrickPatternCreationTest {

    private val brickGrammar = RectangularShapeGrammar(MadeFromStone())
    private val singleBrick = Brick(brickGrammar, MapSize2d(1, 1))
    private val brickW2 = Brick(brickGrammar, MapSize2d(2, 1))
    private val brickH2 = Brick(brickGrammar, MapSize2d(1, 2))
    private val line_3_SxW2 = listOf(singleBrick, brickW2, null)
    private val line_3_W2xS = listOf(brickW2, null, singleBrick)
    private val line_3_W2xW2 = listOf(brickW2, null, brickW2)
    private val line_3_ExW2 = listOf(null, brickW2, null)
    private val line_4_SxW2xS = listOf(singleBrick, brickW2, null, singleBrick)
    private val line_4_W2xW2 = listOf(brickW2, null, brickW2, null)
    private val line_5_W2xH2xH2 = listOf(brickW2, null, brickH2, brickH2)
    private val line_5_W2xH2xH2xS = line_5_W2xH2xH2 + singleBrick
    private val line_5_W2xH2xH2xW2 = line_5_W2xH2xH2 + brickW2
    private val line_5_W2xExE = listOf(brickW2, null, null, null)
    private val line_5_W2xExExS = line_5_W2xExE + singleBrick
    private val line_5_W2xExExW2 = line_5_W2xExE + brickW2
    private val line_5_H2xH2xW2xH2 = listOf(brickH2, brickH2, brickW2, null, brickH2)
    private val line_5_ExExW2xE = listOf(null, null, brickW2, null, null)
    private val line_5_ExW2xH2xH2 = listOf(null, brickH2, null, brickH2, brickH2)
    private val line_5_ExW2xExE = listOf(null, brickW2, null, null, null)
    private val line_5_ExH2xH2xW2 = listOf(null, brickH2, brickH2, brickW2, null)
    private val line_5_ExExExW2 = listOf(null, null, null, brickW2, null)
    private val line_5_H2xW2xH2xH2 = listOf(null, brickW2, null, brickH2, brickH2) // TODO
    private val line_6_W2xH2xH2xW2 = listOf(brickW2, null, brickH2, brickH2, brickW2, null)
    private val line_6_W2xExExW2 = listOf(brickW2, null, null, null, brickW2, null)
    private val line_6_H2xH2xW2xH2xH2 = listOf(brickH2, brickH2, brickW2, null, brickH2, brickH2)
    private val line_6_SxSxW2xSxS = listOf(singleBrick, singleBrick, brickW2, null, singleBrick, singleBrick)
    private val line_6_ExExW2xExE = listOf(null, null, brickW2, null, null, null)

    @Nested
    inner class CreateBasketWeavePatternTest {

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
        }

        @Test
        fun `Partial bricks on the left border TODO`() {
            val expected = line_5_ExW2xH2xH2 + line_5_ExW2xExE + line_5_H2xW2xH2xH2 + line_5_ExW2xExE

            testWithLeftAndRight(1, true, true, expected)
        }

        @Test
        fun `Partial bricks on the right border`() {
            val expected = line_5_W2xH2xH2xS + line_5_W2xExExS + line_5_H2xH2xW2xH2 + line_5_ExExW2xE

            testWithLeftAndRight(0, true, true, expected)
        }

        @Test
        fun `Partial bricks on the bottom border`() {
            val expected = line_6_W2xH2xH2xW2 + line_6_W2xExExW2 + line_6_SxSxW2xSxS

            testWithTopAndBottom(0, true, true, expected)
        }

        @Test
        fun `Bricks across the left border`() {
            val expected = line_5_ExH2xH2xW2 + line_5_ExExExW2 + line_5_H2xW2xH2xH2 + line_5_ExW2xExE

            testWithLeftAndRight(1, false, true, expected)
        }

        @Test
        fun `Bricks across the right border`() {
            val expected = line_5_W2xH2xH2xW2 + line_5_W2xExExW2 + line_5_H2xH2xW2xH2 + line_5_ExExW2xE

            testWithLeftAndRight(0, true, false, expected)
        }

        @Test
        fun `Bricks across the bottom border`() {
            val expected = line_6_W2xH2xH2xW2 + line_6_W2xExExW2 + line_6_H2xH2xW2xH2xH2

            testWithTopAndBottom(0, true, false, expected)
        }

        private fun testPatterA(tileX: Int, tileY: Int) = test(tileX, tileY,
            line_6_W2xH2xH2xW2 + line_6_W2xExExW2 + line_6_H2xH2xW2xH2xH2 + line_6_ExExW2xExE,
        )

        private fun testPatterB(tileX: Int, tileY: Int) = test(tileX, tileY,
             line_6_H2xH2xW2xH2xH2 + line_6_ExExW2xExE + line_6_W2xH2xH2xW2 + line_6_W2xExExW2,
        )

        private fun test(tileX: Int, tileY: Int, expected: List<Brick?>) {
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

        private fun testWithLeftAndRight(tileX: Int, isLeft: Boolean, isRight: Boolean, expected: List<Brick?>) =
            test(
                MapSize2d(5, 4),
                Borders(true, isLeft, isRight, true, tileX),
                expected,
            )

        private fun testWithTopAndBottom(tileX: Int, isTop: Boolean, isBottom: Boolean, expected: List<Brick?>) =
            test(
                MapSize2d(6, 3),
                Borders(isBottom, true, true, isTop, tileX),
                expected,
            )

        private fun test(size: MapSize2d, borders: Borders, expected: List<Brick?>) {
            val pattern = BrickPatternGrammar(
                brickGrammar,
                RowsAndColumns(size),
                BrickPattern.BasketWeave,
                2,
            )

            val result = createBrickPattern(pattern, borders)

            assertTilemap(result, size, expected)
        }
    }

    @Nested
    inner class CreateGridPatternTest {

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
    inner class CreateRunningPatternTest {

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
            testWithPartialBricks(0, true, true, line_3_W2xS + line_3_SxW2)
        }

        @Test
        fun `Even lines with partial bricks on the left border`() {
            testWithPartialBricks(1, true, true, line_3_SxW2 + line_3_W2xS)
        }

        @Test
        fun `Even lines with bricks across the right border`() {
            testWithPartialBricks(0, true, false, line_3_W2xW2 + line_3_SxW2)
        }

        @Test
        fun `Even lines with bricks across the left border`() {
            testWithPartialBricks(1, false, true, line_3_ExW2 + line_3_W2xS)
        }

        private fun testWithoutPartialBricks(tileX: Int) {
            val size = MapSize2d(4, 2)
            val pattern = BrickPatternGrammar(
                brickGrammar,
                RowsAndColumns(size),
                BrickPattern.Running,
                2,
            )
            val borders = Borders(true, tileX)

            val result = createBrickPattern(pattern, borders)

            assertTilemap(result, size, line_4_W2xW2 + line_4_SxW2xS)
        }

        private fun testWithPartialBricks(tileX: Int, isLeft: Boolean, isRight: Boolean, expected: List<Brick?>) {
            val size = MapSize2d(3, 2)
            val pattern = BrickPatternGrammar(
                brickGrammar,
                RowsAndColumns(size),
                BrickPattern.Running,
                2,
            )
            val borders = Borders(true, isLeft, isRight, true, tileX)

            val result = createBrickPattern(pattern, borders)

            assertTilemap(result, size, expected)
        }
    }

    @Nested
    inner class CreateStackPatternTest {

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
            testWithPartialBricks(0, true, line_3_W2xS + line_3_W2xS)
        }

        @Test
        fun `Partial bricks on the left border`() {
            testWithPartialBricks(1, true, line_3_SxW2 + line_3_SxW2)
        }

        @Test
        fun `Bricks across the right border`() {
            testWithPartialBricks(0, false, line_3_W2xW2 + line_3_W2xW2)
        }

        @Test
        fun `Bricks across the left border`() {
            testWithPartialBricks(1, false, line_3_ExW2 + line_3_ExW2)
        }

        private fun testWithoutPartialBricks(tileX: Int) {
            val size = MapSize2d(4, 2)
            val pattern = BrickPatternGrammar(
                brickGrammar,
                RowsAndColumns(size),
                BrickPattern.Stack,
                2,
            )

            val result = createBrickPattern(pattern, Borders(true, tileX))

            assertTilemap(result, size, line_4_W2xW2 + line_4_W2xW2)
        }

        private fun testWithPartialBricks(tileX: Int, isBorder: Boolean, expected: List<Brick?>) {
            val size = MapSize2d(3, 2)
            val pattern = BrickPatternGrammar(
                brickGrammar,
                RowsAndColumns(size),
                BrickPattern.Stack,
                2,
            )

            val result = createBrickPattern(pattern, Borders(isBorder, tileX))

            assertTilemap(result, size, expected)
        }
    }

}