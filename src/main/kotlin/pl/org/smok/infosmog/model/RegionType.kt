package pl.org.smok.infosmog.model

enum class RegionType private constructor(private val value: String) {
    VOIVODESHIP(Values.VOIVODESHIP),
    COUNTY(Values.COUNTY),
    CITY(Values.CITY);

    override fun toString(): String {
        return value
    }

    object Values {
        const val VOIVODESHIP = "VOIVODESHIP"
        const val COUNTY = "COUNTY"
        const val CITY = "CITY"
    }
}
