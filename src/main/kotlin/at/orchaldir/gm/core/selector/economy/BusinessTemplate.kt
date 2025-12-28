package at.orchaldir.gm.core.selector.economy

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.business.BusinessTemplateId

fun State.canDeleteBusinessTemplate(template: BusinessTemplateId) = DeleteResult(template)
    .addElements(getBusinesses(template))
