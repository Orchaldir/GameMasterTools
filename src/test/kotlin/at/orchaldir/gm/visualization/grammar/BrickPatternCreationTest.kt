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
    private val brick1 = Brick(brickGrammar, MapSize2d(1, 1))
    private val brick2 = Brick(brickGrammar, MapSize2d(2, 1))
    private val line_3_1x2 = listOf(brick1, brick2, null)
    private val line_3_2x1 = listOf(brick2, null, brick1)
    private val line_3_2x2 = listOf(brick2, null, brick2)
    private val line_3_Ex2 = listOf(null, brick2, null)
    private val line_4_1x2x1 = listOf(brick1, brick2, null, brick1)
    private val line_4_2x2 = listOf(brick2, null, brick2, null)

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

            assertTilemap(result, size, brick1)
        }

    }

    @Nested
    inner class CreateStackPatternTest {

        @Test
        fun `A stack pattern without partial bricks`() {
            testWithoutPartialBricks(0)
        }

        @Test
        fun `Another tile with a stack pattern without partial bricks`() {
            testWithoutPartialBricks(1)
        }

        @Test
        fun `A stack pattern with partial bricks on the right border`() {
            testWithPartialBricks(0, true, line_3_2x1 + line_3_2x1)
        }

        @Test
        fun `A stack pattern with partial bricks on the left border`() {
            testWithPartialBricks(1, true, line_3_1x2 + line_3_1x2)
        }

        @Test
        fun `A stack pattern with bricks across the right border`() {
            testWithPartialBricks(0, false, line_3_2x2 + line_3_2x2)
        }

        @Test
        fun `A stack pattern with bricks across the left border`() {
            testWithPartialBricks(1, false, line_3_Ex2 + line_3_Ex2)
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

            assertTilemap(result, size, line_4_2x2 + line_4_2x2)
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

    @Nested
    inner class CreateRunningPatternTest {

        @Test
        fun `A running pattern's even lines with full bricks`() {
            testWithoutPartialBricks(0)
        }

        private fun testWithoutPartialBricks(tileX: Int) {
            val size = MapSize2d(4, 2)
            val pattern = BrickPatternGrammar(
                brickGrammar,
                RowsAndColumns(size),
                BrickPattern.Running,
                2,
            )

            val result = createBrickPattern(pattern, Borders(true, tileX))

            assertTilemap(result, size, line_4_2x2 + line_4_1x2x1)
        }
    }

}