package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.NameListId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.name.*

fun State.canDelete(nameList: NameListId) = getCultures(nameList).isEmpty()

fun State.getCultures(nameList: NameListId) = cultures.getAll().filter { l ->
    when (l.namingConvention) {
        is FamilyConvention -> l.namingConvention.givenNames.contains(nameList) || l.namingConvention.familyNames.contains(
            nameList
        )

        is GenonymConvention -> l.namingConvention.names.contains(nameList)
        is MatronymConvention -> l.namingConvention.names.contains(nameList)
        is MononymConvention -> l.namingConvention.names.contains(nameList)
        NoNamingConvention -> false
        is PatronymConvention -> l.namingConvention.names.contains(nameList)
    }
}
