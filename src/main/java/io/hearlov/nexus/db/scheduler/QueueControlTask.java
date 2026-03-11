package io.hearlov.nexus.db.scheduler;

import cn.nukkit.scheduler.Task;
import io.hearlov.nexus.db.Main;
import io.hearlov.nexus.db.cache.NexusDB;

import java.util.Iterator;

public class QueueControlTask extends Task {

    @Override
    public void onRun(int currentTick) {
        Iterator<NexusDB> iterator = Main.getInstance().queryPoolThread.iterator();
        while(iterator.hasNext()) {
            NexusDB db = iterator.next();

            if(db.thread.isInterrupted()) {
                DBReturn ret;
                while((ret = db.mainqueue.poll()) != null) {
                    ret.callback().accept(ret.object());
                }
                iterator.remove();
            } else {
                DBReturn ret;
                while((ret = db.mainqueue.poll()) != null) {
                    ret.callback().accept(ret.object());
                }
            }
        }
    }

}
