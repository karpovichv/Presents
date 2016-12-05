package net.tee7even.presents.provider;

import net.tee7even.presents.Chest;

import java.util.List;

/**
 * @author Tee7even
 */
public interface DataProvider {
    List<Chest> loadChests();

    void saveChest(double x, double y, double z, String levelName, int facing);

    void removeChest(double x, double y, double z, String levelName, int facing);

    void save();
}
