package at.orchaldir.gm.visualization.grammar

import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.visualization.AshlarGrammar
import at.orchaldir.gm.core.model.visualization.RectangularShapeGrammar
import at.orchaldir.gm.core.model.visualization.RowsAndColumns
import at.orchaldir.gm.utils.HashNumberGenerator
import at.orchaldir.gm.utils.RepeatableNumberGenerator
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.visualization.grammar.brick.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import kotlin.test.assertEquals

private const val WIDTH = 20
private const val HEIGHT = 10

class AshlarTest {

    private val brick = RectangularShapeGrammar(Color.Gray)

    @Test
    fun `Test BasketWeave`() {
        val grammar = AshlarGrammar(
            RowsAndColumns(WIDTH, HEIGHT),
            brick,
            4,
            3,
        )
        val numberGenerator: RepeatableNumberGenerator = HashNumberGenerator.fromTime()
        val result = createAshlarPattern(numberGenerator, grammar, Borders(true))

        assertEquals(WIDTH, result.size.width, "Invalid width")
        assertEquals(HEIGHT, result.size.height, "Invalid height")
        assertEquals(200, result.tiles.size, "Invalid number of tiles")

        result.tiles.none { it is EmptyTile }

        var index = 0
        val tiles = result.tiles.toMutableList()

        repeat(HEIGHT) { y ->
            repeat(WIDTH) { x ->
                when (val tile = tiles[index]) {
                    is BrickStart -> validateBrick(tile, x, y, index, tiles, result.size)
                    EmptyTile -> doNothing()
                    OccupiedTile -> fail { "Found an occupied tile not belonging to a brick!" }
                }

                index++
            }
        }
    }

    private fun validateBrick(
        tile: BrickStart,
        x: Int,
        y: Int,
        index: Int,
        tiles: MutableList<BrickTile>,
        brickSize: MapSize2d,
    ) {
        repeat(tile.size.height) { offsetY ->
            repeat(tile.size.width) { offsetX ->
                val subIndex = brickSize.toIndexRisky(x + offsetX, y + offsetY)

                if (subIndex != index) {
                    require(tiles[subIndex] is OccupiedTile) { "Subtiles of brick must be occupied!" }
                    tiles[subIndex] = EmptyTile
                }
            }
        }
    }
}