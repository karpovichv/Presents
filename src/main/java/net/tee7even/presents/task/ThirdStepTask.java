package net.tee7even.presents.task;

import cn.nukkit.scheduler.Task;
import net.tee7even.presents.Chest;

/**
 * @author Tee7even
 */
public class ThirdStepTask extends Task {
    private Chest chest;

    public ThirdStepTask(Chest chest) {
        this.chest = chest;
    }

    public void onRun(int currentTick) {
        chest.close();
    }
}
