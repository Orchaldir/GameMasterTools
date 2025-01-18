package at.orchaldir.gm.core.model.item.text.book.typography

enum class TypographyLayout {
    Top,
    TopAndBottom,
    Center,
    Bottom;

    companion object {
        fun getTitleEntries() = entries.filter { it != TopAndBottom }
    }
}