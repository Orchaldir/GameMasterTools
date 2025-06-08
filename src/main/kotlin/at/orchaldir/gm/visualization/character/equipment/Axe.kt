package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.item.equipment.style.AxeHead
import at.orchaldir.gm.core.model.item.equipment.style.DoubleBitAxeHead
import at.orchaldir.gm.core.model.item.equipment.style.SingleBitAxeHead
import at.orchaldir.gm.core.model.item.equipment.style.SpearShape
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.visualization.character.CharacterRenderState

fun visualizeAxeHead(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    shaftAabb: AABB,
    head: AxeHead,
) = when (head) {
    is SingleBitAxeHead -> visualizeSingleBitAxeHead(state, renderer, shaftAabb, head)
    is DoubleBitAxeHead -> visualizeDoubleBitAxeHead(state, renderer, shaftAabb, head)
}

fun visualizeSingleBitAxeHead(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    shaftAabb: AABB,
    head: SingleBitAxeHead,
) {

}

fun visualizeDoubleBitAxeHead(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    shaftAabb: AABB,
    head: DoubleBitAxeHead,
) {

}
