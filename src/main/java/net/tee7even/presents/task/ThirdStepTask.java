package net.tee7even.presents.task;

import cn.nukkit.scheduler.Task;
import net.tee7even.presents.Chest;

/**
 * Created by Tee7even on 26.04.2016.
 */
public class ThirdStepTask extends Task
{
    Chest chest;

    public ThirdStepTask(Chest chest)
    {
        this.chest = chest;
    }

    public void onRun(int currentTick)
    {
        chest.close();
    }
}
