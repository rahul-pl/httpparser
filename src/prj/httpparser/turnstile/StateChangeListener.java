package prj.httpparser.turnstile;

public interface StateChangeListener<STATE, EVENT>
{
    public void onChange(STATE oldState, EVENT cause, STATE newState);
}
