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

/**
 * The interface for signal listeners.
 *
 * @param <T>
 *            the type of object that is added as parameter to a dispatched
 *            event
 */
public interface ISignalListener<T> {

    /**
     * Invoked when an event dispatched.
     *
     * @param object
     *            the object that comes along with the event
     */
    public void receive(T object);

}