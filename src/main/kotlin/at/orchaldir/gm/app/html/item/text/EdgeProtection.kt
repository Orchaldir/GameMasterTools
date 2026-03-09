package at.orchaldir.gm.app.html.item.text

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.math.*
import at.orchaldir.gm.app.html.util.part.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.BOOK_PROTECTION_MATERIALS
import at.orchaldir.gm.core.model.item.text.*
import at.orchaldir.gm.core.model.item.text.book.*
import at.orchaldir.gm.core.model.item.text.book.typography.Typography
import at.orchaldir.gm.core.model.item.text.scroll.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.unit.SiPrefix
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showEdgeProtection(
    call: ApplicationCall,
    state: State,
    protection: EdgeProtection,
) {
    showDetails("Edge Protection") {
        field("Type", protection.getType())

        when (protection) {
            NoEdgeProtection -> doNothing()
            is ProtectedCorners -> {
                field("Corner Shape", protection.shape)
                fieldFactor("Corner Size", protection.size)
                showItemPart(call, state, protection.main)
            }

            is ProtectedEdge -> {
                fieldFactor("Edge Width", protection.width)
                showItemPart(call, state, protection.main)
            }
        }
    }
}

// edit

fun HtmlBlockTag.editEdgeProtection(
    state: State,
    protection: EdgeProtection,
) {
    showDetails("Edge Protection", true) {
        selectValue("Type", EDGE, EdgeProtectionType.entries, protection.getType())

        when (protection) {
            NoEdgeProtection -> doNothing()
            is ProtectedCorners -> {
                selectValue("Corner Shape", combine(EDGE, SHAPE), CornerShape.entries, protection.shape)
                selectPercentage(
                    "Corner Size",
                    combine(EDGE, SIZE),
                    protection.size,
                    1,
                    50,
                    1,
                )
                editItemPart(state, protection.main, EDGE, allowedTypes = BOOK_PROTECTION_MATERIALS)
            }

            is ProtectedEdge -> {
                selectPercentage(
                    "Edge Width",
                    combine(EDGE, SIZE),
                    protection.width,
                    1,
                    20,
                    1,
                )
                editItemPart(state, protection.main, EDGE, allowedTypes = BOOK_PROTECTION_MATERIALS)
            }
        }
    }
}

// parse

fun parseEdgeProtection(parameters: Parameters) = when (parse(parameters, EDGE, EdgeProtectionType.None)) {
    EdgeProtectionType.None -> NoEdgeProtection
    EdgeProtectionType.Corners -> ProtectedCorners(
        parse(parameters, combine(EDGE, SHAPE), CornerShape.Triangle),
        parseFactor(parameters, combine(EDGE, SIZE), DEFAULT_PROTECTED_CORNER_SIZE),
        parseItemPart(parameters, EDGE, BOOK_PROTECTION_MATERIALS),
    )

    EdgeProtectionType.Edge -> ProtectedEdge(
        parseFactor(parameters, combine(EDGE, SIZE), DEFAULT_PROTECTED_EDGE_WIDTH),
        parseItemPart(parameters, EDGE, BOOK_PROTECTION_MATERIALS),
    )
}

