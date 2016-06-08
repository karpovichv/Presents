package net.tee7even.presents.provider;

import net.tee7even.presents.Chest;

import java.util.List;

/**
 * @author Tee7even
 */
public interface DataProvider
{
    public List<Chest> loadChests();

    public void saveChest(double x, double y, double z, String levelName, int facing);

    public void removeChest(double x, double y, double z, String levelName, int facing);

    public void close();
}
