package blank2d.framework.ecs.signal;


/**
 * The interface for signal listeners.
 *
 * @param <T>
 *            the type of object that is added as parameter to a dispatched
 *            event
 */
public interface SignalListener<T> {

    /**
     * Invoked when an event dispatched.
     *
     * @param object
     *            the object that comes along with the event
     */
    public void receive(T object);

}