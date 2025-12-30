package at.orchaldir.gm.app.html.realm.population

import at.orchaldir.gm.app.html.util.fieldPopulationDensity
import at.orchaldir.gm.app.html.util.showAreaLookupDetails
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.population.HasPopulation
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.math.unit.HasArea
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

fun <ID : Id<ID>, ELEMENT> HtmlBlockTag.showAreaAndPopulation(
    call: ApplicationCall,
    state: State,
    element: ELEMENT,
) where
        ELEMENT : Element<ID>,
        ELEMENT : HasArea,
        ELEMENT : HasPopulation
{
    showAreaLookupDetails(state, element)
    showPopulationDetails(call, state, element)
    fieldPopulationDensity(state, element)
}