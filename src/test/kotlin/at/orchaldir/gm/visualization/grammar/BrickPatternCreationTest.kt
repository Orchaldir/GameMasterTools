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

            val result = createGridPattern(pattern)

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
        fun `A stack pattern without partial bricks in another tile`() {
            testWithoutPartialBricks(1)
        }

        @Test
        fun `A stack pattern with partial bricks on the right border`() {
            val size = MapSize2d(3, 2)
            val pattern = BrickPatternGrammar(
                brickGrammar,
                RowsAndColumns(size),
                BrickPattern.Grid,
                2,
            )

            val result = createStackPattern(pattern, Borders(true))


            assertTilemap(result, size, listOf(brick2, null, brick1, brick2, null, brick1))
        }

        @Test
        fun `A stack pattern with partial bricks on the left border`() {
            val size = MapSize2d(3, 2)
            val pattern = BrickPatternGrammar(
                brickGrammar,
                RowsAndColumns(size),
                BrickPattern.Grid,
                2,
            )

            val result = createStackPattern(pattern, Borders(true, 1))


            assertTilemap(result, size, listOf(brick1, brick2, null, brick1, brick2, null))
        }

        private fun testWithoutPartialBricks(tileX: Int) {
            val size = MapSize2d(4, 2)
            val pattern = BrickPatternGrammar(
                brickGrammar,
                RowsAndColumns(size),
                BrickPattern.Grid,
                2,
            )

            val result = createStackPattern(pattern, Borders(true, tileX))


            assertTilemap(result, size, listOf(brick2, null, brick2, null, brick2, null, brick2, null))
        }
    }

}