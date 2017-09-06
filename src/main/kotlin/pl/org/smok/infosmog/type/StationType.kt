package pl.org.smok.infosmog.type

enum class StationType(val type: String) {
    GIOS("GIOS");

    override fun toString(): String {
        return type
    }
}
