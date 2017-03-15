package ninja.hudy.infosmog.name;

public enum DefaultName {
    UNDEFINED_NAME("UNDEFINED");
    
    private String name;

    DefaultName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
