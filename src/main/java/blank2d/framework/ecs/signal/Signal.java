/*
  Copyright (c) 2016 Roman Divotkey, Univ. of Applied Sciences Upper Austria.
  All rights reserved.

  This file is subject to the terms and conditions defined in file
  'LICENSE', which is part of this source code package.

  THIS CODE IS PROVIDED AS EDUCATIONAL MATERIAL AND NOT INTENDED TO ADDRESS
  ALL REAL WORLD PROBLEMS AND ISSUES IN DETAIL.

  Code has been modified for this project
 */

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
    private final List<ISignalListener<T>> signalListeners = new CopyOnWriteArrayList<>();

    /**
     * Adds the specified listener to this signal.
     *
     * @param signalListener
     *            the listener to be added
     */
    public void addSignalListener(ISignalListener<T> signalListener) {
        assert !signalListeners.contains(signalListener) : "signal listener already added";
        signalListeners.add(signalListener);
    }

    /**
     * Removes the specified listener from this signal.
     *
     * @param signalListener
     *            the listener to be removed
     */
    public void removeSignalListener(ISignalListener<T> signalListener) {
        signalListeners.remove(signalListener);
    }

    /**
     * clears the signal listeners
     * called when the engine is disposed
     */
    public void clear(){
        signalListeners.clear();
    }

    /**
     * Dispatches the event to all registered listeners.
     *
     * @param object
     *            the object that comes along with the dispatched event
     */
    public void dispatch(T object) {
        for (ISignalListener<T> signalListener : signalListeners) {
            signalListener.receive(object);
        }
    }

    public void dispatch(T object, Class<? extends ISignalListener<T>> tClass) {
        for (ISignalListener<T> signalListener : signalListeners) {
            if(tClass.isInstance(signalListener)) signalListener.receive(object);
        }
    }

}