package io.github.zpiboo.mpkspeedrun.parkourmaps;

public class Map {
    private String name;

    private TriggerZone start;
    private TriggerZone finish;

    public Map(String name, TriggerZone start, TriggerZone finish) {
        this.name = name;

        this.start = start;
        this.finish = finish;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public TriggerZone getStart() {
        return start;
    }
    public void setStart(TriggerZone start) {
        this.start = start;
    }

    public TriggerZone getFinish() {
        return finish;
    }
    public void setFinish(TriggerZone finish) {
        this.finish = finish;
    }
}
