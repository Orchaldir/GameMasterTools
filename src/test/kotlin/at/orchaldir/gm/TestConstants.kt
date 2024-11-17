package at.orchaldir.gm

import at.orchaldir.gm.core.model.calendar.Calendar
import at.orchaldir.gm.core.model.calendar.CalendarId
import at.orchaldir.gm.core.model.calendar.MonthDefinition
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.core.model.time.Day
import at.orchaldir.gm.core.model.world.building.BuildingId

val CHARACTER_ID_0 = CharacterId(0)
val CHARACTER_ID_1 = CharacterId(1)
val CHARACTER_ID_2 = CharacterId(2)
val BUILDING_ID_0 = BuildingId(0)
val BUILDING_ID_1 = BuildingId(1)
val BUSINESS_ID_0 = BusinessId(0)
val JOB_ID_0 = JobId(0)
val LANGUAGE_ID_0 = LanguageId(0)

val CALENDAR0 = Calendar(CalendarId(0), months = listOf(MonthDefinition("a")))
val DAY0 = Day(100)
val DAY1 = Day(200)
val DAY2 = Day(300)