package blank2d.framework.ecs.signal;


import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The base class for all kinds of signals. Signals transmit events to
 * registered listeners.
 *
 * @param <T>
 *            the type of object that is added as parameter to a dispatched
 *            event
 */
public class Signal<T> {

    /** The registered listeners. */
    private final List<SignalListener<T>> signalListeners = new CopyOnWriteArrayList<>();

    /**
     * Adds the specified listener to this signal.
     *
     * @param signalListener
     *            the listener to be added
     */
    public void addSignalListener(SignalListener<T> signalListener) {
        assert !signalListeners.contains(signalListener) : "signal listener alreaedy added";
        signalListeners.add(signalListener);
    }

    /**
     * Removes the specified listener from this signal.
     *
     * @param signalListener
     *            the listener to be removed
     */
    public void removeSignalListener(SignalListener<T> signalListener) {
        signalListeners.remove(signalListener);
    }

    /**
     * Dispatches the event to all registered listeners.
     *
     * @param object
     *            the object that comes along with the dispatched event
     */
    public void dispatch(T object) {
        for (SignalListener<T> signalListener : signalListeners) {
            signalListener.receive(object);
        }
    }

}