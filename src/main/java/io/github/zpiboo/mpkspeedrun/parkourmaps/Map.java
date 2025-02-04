package io.github.zpiboo.mpkspeedrun.parkourmaps;

import io.github.kurrycat.mpkmod.gui.infovars.InfoString;
import io.github.kurrycat.mpkmod.util.BoundingBox3D;

@InfoString.DataClass
public class Map {
    private String name;

    private TriggerZone start;
    private TriggerZone finish;

    @InfoString.Getter
    public BoundingBox3D getStartBox() { return start.getZone(); }
    @InfoString.Getter
    public BoundingBox3D getFinishBox() { return finish.getZone(); }

    public Map(String name, TriggerZone start, TriggerZone finish) {
        this.name = name;

        this.start = start;
        this.finish = finish;
    }

    @InfoString.Getter
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
