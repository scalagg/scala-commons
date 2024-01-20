package gg.scala.commons.tablist;

import gg.scala.commons.tablist.manage.TablistManager;
import net.evilblock.cubed.util.bukkit.Tasks;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author Reximian9k (https://github.com/reximian9k)
 */
public class TablistUpdateTask {

    private static TablistUpdateTask INSTANCE;

    public static TablistUpdateTask getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TablistUpdateTask();
        }

        return INSTANCE;
    }

    private BukkitTask task;

    protected TablistUpdateTask() { }

    public void start() {
        if (task != null) {
            return;
        }

        task = Tasks.timer(0, 20, this::run);
    }

    private void run() {
        for (TablistHandle handle : TablistManager.get().getTablistHandles()) {
            try {
                handle.onTick();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
