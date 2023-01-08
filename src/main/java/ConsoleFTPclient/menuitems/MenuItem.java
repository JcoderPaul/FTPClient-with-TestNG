package ConsoleFTPclient.menuitems;

import ConsoleFTPclient.services.BaseReadWriteWorker;

public interface MenuItem {
    public abstract void executeMenuItem(BaseReadWriteWorker baseCommand);
}
