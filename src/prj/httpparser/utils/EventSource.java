package prj.httpparser.utils;

import java.util.concurrent.CopyOnWriteArraySet;

public abstract class EventSource<LISTENER_TYPE>
{
    protected CopyOnWriteArraySet<LISTENER_TYPE> _listeners = new CopyOnWriteArraySet<LISTENER_TYPE>();

    public void addListener(LISTENER_TYPE listener)
    {
        _listeners.add(listener);
    }

    public void removeListener(LISTENER_TYPE listener)
    {
        _listeners.remove(listener);
    }
}
