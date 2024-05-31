package at.orchaldir.gm.prototypes.visualization.character

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.character.appearance.ExoticSkin
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.OneEye
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.renderer.LineOptions
import at.orchaldir.gm.visualization.RenderConfig
import at.orchaldir.gm.visualization.character.EyesConfig
import at.orchaldir.gm.visualization.character.HeadConfig
import at.orchaldir.gm.visualization.character.visualizeCharacter
import java.io.File

fun main() {
    val config = RenderConfig(
        Distance(0.2f), LineOptions(Color.Black.toRender(), Distance(0.005f)),
        HeadConfig(EyesConfig(0.1f, 0.2f, 0.3f), 0.4f)
    )
    val appearance = HeadOnly(Head(eyes = OneEye()), ExoticSkin(), Distance(0.2f))
    val svg = visualizeCharacter(config, appearance)

    File("eyes.svg").writeText(svg.export())
}