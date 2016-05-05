package net.tee7even.presents.provider;

import net.tee7even.presents.Chest;

import java.util.List;

/**
 * Created by Tee7even on 05.05.2016.
 */
public interface DataProvider
{
    public List<Chest> loadChests();

    public void saveChest(double x, double y, double z, String levelName, int facing);

    public void removeChest(double x, double y, double z, String levelName, int facing);

    public void close();
}
