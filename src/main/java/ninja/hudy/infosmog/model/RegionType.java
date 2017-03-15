package ninja.hudy.infosmog.model;

public enum RegionType {
    VOIVODESHIP(Values.VOIVODESHIP),
    COUNTY(Values.COUNTY),
    CITY(Values.CITY);

    private String value;

    RegionType(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }

    public static class Values {
        public static final String VOIVODESHIP = "VOIVODESHIP";
        public static final String COUNTY = "COUNTY";
        public static final String CITY = "CITY";
    }
}
