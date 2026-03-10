package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.part.editItemPart
import at.orchaldir.gm.app.html.util.part.parseItemPart
import at.orchaldir.gm.app.html.util.part.showItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.BLADE_MATERIALS
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Size
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showAxeHead(
    call: ApplicationCall,
    state: State,
    head: AxeHead,
) {
    showDetails("Axe Head") {
        field("Type", head.getType())

        when (head) {
            is SingleBitAxeHead -> showAxeBlade(call, state, head.blade)
            is DoubleBitAxeHead -> showAxeBlade(call, state, head.blade)
        }
    }
}

private fun HtmlBlockTag.showAxeBlade(
    call: ApplicationCall,
    state: State,
    blade: AxeBlade,
) {
    showDetails("Axe Blade") {
        field("Type", blade.getType())

        when (blade) {
            is BroadAxeBlade -> {
                field("Shape", blade.shape)
                field("Size", blade.size)
                field("Length", blade.length)
                showItemPart(call, state, blade.blade)
            }

            is DaggerAxeBlade -> {
                field("Size", blade.size)
                showItemPart(call, state, blade.blade)
            }

            is SymmetricAxeBlade -> {
                field("Shape", blade.shape)
                field("Size", blade.size)
                showItemPart(call, state, blade.blade)
            }
        }
    }
}

// edit

fun HtmlBlockTag.editAxeHead(
    state: State,
    head: AxeHead,
    param: String,
) {
    showDetails("Axe Head", true) {
        selectValue("Type", param, AxeHeadType.entries, head.getType())

        when (head) {
            is SingleBitAxeHead -> editAxeBlade(state, head.blade, param)
            is DoubleBitAxeHead -> editAxeBlade(state, head.blade, param)
        }
    }
}

private fun HtmlBlockTag.editAxeBlade(
    state: State,
    blade: AxeBlade,
    param: String,
) {
    showDetails("Axe Blade", true) {
        val param = combine(param, BLADE)
        selectValue("Type", param, AxeBladeType.entries, blade.getType())

        when (blade) {
            is BroadAxeBlade -> {
                selectValue(
                    "Shape",
                    combine(param, SHAPE),
                    BroadAxeShape.entries,
                    blade.shape,
                )
                selectValue(
                    "Size",
                    combine(param, SIZE),
                    Size.entries,
                    blade.size,
                )
                selectValue(
                    "Length",
                    combine(param, LENGTH),
                    Size.entries,
                    blade.length,
                )
                editItemPart(state, blade.blade, param, allowedTypes = BLADE_MATERIALS)
            }

            is DaggerAxeBlade -> {
                selectValue(
                    "Size",
                    combine(param, SIZE),
                    Size.entries,
                    blade.size,
                )
                editItemPart(state, blade.blade, param, allowedTypes = BLADE_MATERIALS)
            }

            is SymmetricAxeBlade -> {
                selectValue(
                    "Shape",
                    combine(param, SHAPE),
                    SymmetricAxeShape.entries,
                    blade.shape,
                )
                selectValue(
                    "Size",
                    combine(param, SIZE),
                    Size.entries,
                    blade.size,
                )
                editItemPart(state, blade.blade, param, allowedTypes = BLADE_MATERIALS)
            }
        }
    }
}


// parse

fun parseAxeHead(
    state: State,
    parameters: Parameters,
    param: String = AXE,
) = when (parse(parameters, param, AxeHeadType.SingleBit)) {
    AxeHeadType.SingleBit -> SingleBitAxeHead(
        parseAxeBlade(state, parameters, param),
    )

    AxeHeadType.DoubleBit -> DoubleBitAxeHead(
        parseAxeBlade(state, parameters, param),
    )
}

private fun parseAxeBlade(
    state: State,
    parameters: Parameters,
    param: String,
): AxeBlade {
    val param = combine(param, BLADE)

    return when (parse(parameters, param, AxeBladeType.Symmetric)) {
        AxeBladeType.Broad -> BroadAxeBlade(
            parse(parameters, combine(param, SHAPE), BroadAxeShape.Curved),
            parse(parameters, combine(param, SIZE), Size.Medium),
            parse(parameters, combine(param, LENGTH), Size.Medium),
            parseItemPart(state, parameters, param, BLADE_MATERIALS),
        )

        AxeBladeType.Dagger -> DaggerAxeBlade(
            parse(parameters, combine(param, SIZE), Size.Medium),
            parseItemPart(state, parameters, param, BLADE_MATERIALS),
        )

        AxeBladeType.Symmetric -> SymmetricAxeBlade(
            parse(parameters, combine(param, SHAPE), SymmetricAxeShape.HalfCircle),
            parse(parameters, combine(param, SIZE), Size.Medium),
            parseItemPart(state, parameters, param, BLADE_MATERIALS),
        )
    }
}
