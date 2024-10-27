package at.orchaldir.gm.core.model.world.building

enum class BuildingPurposeType {
    MultipleBusiness,
    SingleBusiness,
    SingleFamilyHouse,
    ApartmentHouse;

    fun isBusiness() = when (this) {
        MultipleBusiness -> true
        SingleBusiness -> true
        else -> false
    }
}