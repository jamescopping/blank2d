/*
  Copyright (c) 2016 Roman Divotkey, Univ. of Applied Sciences Upper Austria.
  All rights reserved.

  This file is subject to the terms and conditions defined in file
  'LICENSE', which is part of this source code package.

  THIS CODE IS PROVIDED AS EDUCATIONAL MATERIAL AND NOT INTENDED TO ADDRESS
  ALL REAL WORLD PROBLEMS AND ISSUES IN DETAIL.

  Code has been modified for this project
 */
package blank2d.framework.ecs;

import java.util.*;

/**
 * The component container class for entities.
 */
public final class Entity {

    /** The list of components this entity is composed of. */
    private final List<Component> componentList = new ArrayList<>();

    /** Provides fast access to components based on its type. */
    private final Map<Class<?>, Component> cache = new HashMap<>();

    /** Unique id for this entity */
    private String id;

    /** Entity tag */
    private Tag tag = Tag.UNTAGGED;

    /** Entity layer */
    private Layer layer = Layer.DEFAULT;

    /** Indicates if this entity has been activated. */
    private boolean activated;

    /** Indicates if this entity has been activated. */
    private boolean toBeDestroyed;

    /** A reference to the engine this entity has been added to. */
    private Engine engine;

    /**
     * Sets the references to the engine this entity belongs to. This method is
     * called by the engine when this entity is added.
     *
     * @param e
     *            the engine this entity belongs to
     */
    void setEngine(Engine e) {
        engine = e;
    }

    /**
     * Return the engine this entity has been attached to.
     *
     * @return the engine of this entity or {@code null} if this entity has not
     *         been added to an engine yet
     */
    public Engine getEngine() {
        return engine;
    }

    /**
     * Returns {@code true} if this entity has a component of the specified
     * type. A type can either be an interface of a class.
     *
     * <pre>
     * Entity bar = getEntityFromSomeWhere();
     *
     * if (bar.hasComponent(Foo.clazz)) {
     *     System.out.println(&quot;Entity bar has component of type Foo&quot;);
     * }
     * </pre>
     *
     * @param tClass
     *            the class or interface the requested component must implement
     * @return {@code true} if the requested component is part of this entity
     */
    public boolean hasComponent(Class<?> tClass) {
        if (cache.containsKey(tClass)) return true;
        for (Component component : componentList) {
            if (tClass.isInstance(component)) return true;
        }
        return false;
    }

    /**
     * Retrieves the component of this entity of the specified type. A type can
     * either be an interface or a class.
     *
     * <p>
     * If more than one component implements the specified type, the first
     * component that matches the requirements will be returned.
     * </p>
     *
     * <p>
     * This method returns throws an {@code IllegalArgumentException} in case no
     * component can be found that matches the requested type. This is a
     * deliberate design decision to avoid repetitive checks against
     * {@code null} references. In almost all cases one can assume that the
     * requested component must be part of the used entity. If this fails, this
     * is most likely an error condition that should be indicated as fast as
     * possible (<i>fail-fast</i>). In cases where it is not certain that an
     * entity has contains a certain type of component the {@link #hasComponent}
     * can be used test this.
     * </p>
     *
     * <p>
     * <strong>Example</strong>
     * </p>
     *
     * <pre>
     * public interface CollisionHandler {
     *     public void handleCollision();
     * }
     *
     * public class Foo extends Component implements CollisionHandler {
     *
     *     public void handleCollision() {
     *         //...
     *     }
     *
     *     public void doSomething() {
     *         //...
     *     }
     *
     *     //...
     * }
     *
     * Entity bar = getEntityFromSomeWhere();
     * bar.addComponent(new Foo());
     * //..
     *
     * // Invoke collision handling without knowing about Foo
     * bar.getComponent(CollisionHandler.class).handleCollision();
     * //..
     *
     * // Retrieve component Foo
     * Foo foo = bar.getComponent(Foo.class);
     * foo.handleCollision();
     * foo.doSomething();
     * </pre>
     *
     * @param <T>
     *            type parameter used to avoid casts, irrelevant when calling
     *            this method
     * @param tClass
     *            the class or interface the requested component must implement
     * @return the component that implements the specified interface of class
     * @throws IllegalArgumentException
     *             if no component with the specified type could be found
     */
    public <T> T getComponent(Class<T> tClass) throws IllegalArgumentException {
        Component cached = cache.get(tClass);
        if (cached != null) return tClass.cast(cached);

        for (Component component : componentList) {
            if (tClass.isInstance(component)) {
                cache.put(tClass, component);
                return tClass.cast(component);
            }
        }
        throw new IllegalArgumentException("component not found " + tClass.getName());
    }

    /**
     * Retrieves a list of all components of the specified type.
     *
     * <p>
     * <strong>Note: </strong> A new list object will be created each time this
     * method is called. Do not use this method too often.
     * </p>
     *
     * @param <T>
     *            type parameter used to avoid casts, irrelevant when calling
     *            this method
     * @param tClass
     *            the class or interface the requested components must implement
     * @return a list with components of the specified type (may be empty)
     */
    public <T> List<T> getAllComponents(Class<T> tClass) {
        ArrayList<T> result = new ArrayList<>();
        for (Component component : componentList) { if (tClass.isInstance(component)) result.add(tClass.cast(component)); }
        return result;
    }

    /**
     * Adds the specified component to this entity. Component cannot be added if
     * this entity has already been added to an engine and activated.
     *
     * @param component
     *            the component to be added
     */
    public void addComponent(Component component) {
        if (isActivated() || engine != null) throw new IllegalStateException("cannot add component to activated entity");
        if (component.getEntity() != null) throw new IllegalArgumentException("component already attached an entity");
        componentList.add(component);
        component.setEntity(this);
        if (isActivated() && !component.isActivated()) component.activateInternal();
    }

    /**
     * removes this entity from the engine.
     */
    public void destroy(){
        getEngine().removeEntity(this);
    }


    /**
     * Returns the activation state of this entity.
     *
     * @return {@code true} if this entity has been activated, {@code false}
     *          otherwise
     */
    boolean isActivated() {
        return activated;
    }

    /**
     * Activates this entity. This method is called by the engine this entity
     * has been added to. Activating an entity will activate its components in
     * exactly the order its components have been added.
     */
    void activate() {
        if (isActivated()) {
            throw new IllegalStateException("entity already activated");
        }

        // activate components
        for (Component component : componentList) {
            if (!component.isActivated()) {
                component.activateInternal();
            }
        }
        activated = true;
    }

    /**
     * Deactivates this entity. This method is called by the engine this entity
     * has been added to. Deactivating an entity will deactivate its component
     * in reverse order to ensure dependencies are resolved correctly.
     */
    void deactivate() {
        if (!isActivated()) throw new IllegalStateException("entity not activated");

        // deactivate components in reverse order
        for (int i = componentList.size() - 1; i >= 0; --i) {
            Component c = componentList.get(i);
            if (c.isActivated()) {
                c.deactivateInternal();
            }
        }
        activated = false;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Layer getLayer() {
        return layer;
    }

    public void setLayer(Layer layer) {
        this.layer = layer;
    }

    @Override
    public String toString() {
        return "Entity{" +
                "id='" + id + '\'' +
                ", tag=" + tag +
                ", layer=" + layer +
                ", activated=" + activated +
                ", componentList=" + componentList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return isActivated() == entity.isActivated() &&
                componentList.equals(entity.componentList) &&
                cache.equals(entity.cache) &&
                getId().equals(entity.getId()) &&
                getTag() == entity.getTag() &&
                getLayer() == entity.getLayer() &&
                getEngine().equals(entity.getEngine());
    }

    @Override
    public int hashCode() {
        return Objects.hash(componentList, cache, getId(), getTag(), getLayer(), isActivated(), getEngine());
    }
}

