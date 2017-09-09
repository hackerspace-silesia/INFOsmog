package ninja.hudy.infosmog.api.v1.to;

public class ThresholdTO {

    private Integer acceptable;
    private Integer informative;
    private Integer alarm;

    public Integer getAcceptable() {
        return acceptable;
    }

    public void setAcceptable(Integer acceptable) {
        this.acceptable = acceptable;
    }

    public Integer getInformative() {
        return informative;
    }

    public void setInformative(Integer informative) {
        this.informative = informative;
    }

    public Integer getAlarm() {
        return alarm;
    }

    public void setAlarm(Integer alarm) {
        this.alarm = alarm;
    }
}
