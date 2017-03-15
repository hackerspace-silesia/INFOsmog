package ninja.hudy.infosmog.type;

public enum StationType {
    GIOS("GIOS");

    private String type;

    StationType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return type;
    }
}
