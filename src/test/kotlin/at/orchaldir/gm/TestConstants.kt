package at.orchaldir.gm

import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.PersonalityTraitId
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.culture.fashion.FashionId
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.model.font.FontId
import at.orchaldir.gm.core.model.holiday.HolidayId
import at.orchaldir.gm.core.model.item.equipment.EquipmentId
import at.orchaldir.gm.core.model.item.periodical.PeriodicalId
import at.orchaldir.gm.core.model.item.text.TextId
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.core.model.magic.SpellId
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.name.NameListId
import at.orchaldir.gm.core.model.organization.OrganizationId
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.race.appearance.RaceAppearanceId
import at.orchaldir.gm.core.model.religion.DomainId
import at.orchaldir.gm.core.model.religion.GodId
import at.orchaldir.gm.core.model.religion.PantheonId
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.calendar.CalendarId
import at.orchaldir.gm.core.model.time.calendar.ComplexMonths
import at.orchaldir.gm.core.model.time.calendar.Month
import at.orchaldir.gm.core.model.time.date.Day
import at.orchaldir.gm.core.model.time.date.Year
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyleId
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.model.world.moon.MoonId
import at.orchaldir.gm.core.model.world.plane.PlaneId
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.street.StreetTemplateId
import at.orchaldir.gm.core.model.world.terrain.MountainId
import at.orchaldir.gm.core.model.world.terrain.RiverId
import at.orchaldir.gm.core.model.world.town.TownId

val ARCHITECTURAL_ID0 = ArchitecturalStyleId(0)
val ARCHITECTURAL_ID1 = ArchitecturalStyleId(1)
val BUILDING_ID_0 = BuildingId(0)
val BUILDING_ID_1 = BuildingId(1)
val BUSINESS_ID_0 = BusinessId(0)
val UNKNOWN_BUSINESS_ID = BusinessId(99)
val CALENDAR_ID_0 = CalendarId(0)
val CALENDAR_ID_1 = CalendarId(1)
val UNKNOWN_CALENDAR_ID = CalendarId(99)
val CHARACTER_ID_0 = CharacterId(0)
val CHARACTER_ID_1 = CharacterId(1)
val CHARACTER_ID_2 = CharacterId(2)
val UNKNOWN_CHARACTER_ID = CharacterId(99)
val CULTURE_ID_0 = CultureId(0)
val CULTURE_ID_1 = CultureId(1)
val DOMAIN_ID_0 = DomainId(0)
val UNKNOWN_DOMAIN_ID = DomainId(99)
val EQUIPMENT_ID_0 = EquipmentId(0)
val EQUIPMENT_ID_1 = EquipmentId(1)
val FASHION_ID_0 = FashionId(0)
val FONT_ID_0 = FontId(0)
val GOD_ID_0 = GodId(0)
val UNKNOWN_GOD_ID = GodId(99)
val HOLIDAY_ID_0 = HolidayId(0)
val UNKNOWN_HOLIDAY_ID = HolidayId(99)
val JOB_ID_0 = JobId(0)
val UNKNOWN_JOB_ID = JobId(99)
val LANGUAGE_ID_0 = LanguageId(0)
val LANGUAGE_ID_1 = LanguageId(1)
val UNKNOWN_LANGUAGE_ID = LanguageId(99)
val MATERIAL_ID_0 = MaterialId(0)
val MATERIAL_ID_1 = MaterialId(1)
val MOON_ID_0 = MoonId(0)
val MOUNTAIN_ID_0 = MountainId(0)
val MOUNTAIN_ID_1 = MountainId(1)
val NAME_LIST_ID0 = NameListId(0)
val ORGANIZATION_ID_0 = OrganizationId(0)
val PANTHEON_ID_0 = PantheonId(0)
val UNKNOWN_PANTHEON_ID = PantheonId(99)
val PERIODICAL_ID_0 = PeriodicalId(0)
val PERIODICAL_ID_1 = PeriodicalId(1)
val PERSONALITY_ID_0 = PersonalityTraitId(0)
val UNKNOWN_PERSONALITY_ID = PersonalityTraitId(99)
val PLANE_ID_0 = PlaneId(0)
val PLANE_ID_1 = PlaneId(1)
val UNKNOWN_PLANE_ID = PlaneId(99)
val RACE_ID_0 = RaceId(0)
val RACE_ID_1 = RaceId(1)
val RACE_ID_2 = RaceId(2)
val RACE_APPEARANCE_ID_0 = RaceAppearanceId(0)
val RIVER_ID_0 = RiverId(0)
val SPELL_ID_0 = SpellId(0)
val SPELL_ID_1 = SpellId(1)
val STREET_ID_0 = StreetId(0)
val STREET_ID_1 = StreetId(1)
val STREET_TYPE_ID_0 = StreetTemplateId(0)
val STREET_TYPE_ID_1 = StreetTemplateId(1)
val TEXT_ID_0 = TextId(0)
val TEXT_ID_1 = TextId(1)
val TEXT_ID_2 = TextId(2)
val TOWN_ID_0 = TownId(0)
val TOWN_ID_1 = TownId(1)

val CALENDAR0 = Calendar(CALENDAR_ID_0, months = ComplexMonths(listOf(Month("a"))))
val YEAR0 = Year(-11)
val YEAR1 = Year(-10)
val DAY0 = Day(-300)
val DAY1 = Day(-200)
val DAY2 = Day(-100)
val FUTURE_DAY_0 = Day(100)
val FUTURE_YEAR_0 = Year(10)