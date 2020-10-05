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

import blank2d.Game;
import blank2d.framework.ecs.signal.entity.EntityRemovedListener;
import blank2d.framework.ecs.signal.entity.EntitySignal;
import blank2d.framework.ecs.system.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The engine is actual management class of this framework. If accomplishes
 * several tasks:
 *
 * <ul>
 * <li>Management of (active) entities.</li>
 * <li>Service locator for all kind of systems (services) within the
 * application.</li>
 * <li>Central update mechanism of attached systems.</li>
 * </ul>
 *
 * <p>
 * In general there will be only one instance of an engine within an application
 * at a time. It is fine to create, initialize and destroy an engine for each
 * individual state of the application. The update method of the engine should
 * be called once each cycle of the main loop, passing the actual delta time as
 * parameter.
 * </p>
 *
 * <p><strong>Example</strong></p>
 * <pre>
 * public class PlayState extends GameState {
 *
 *     private Engine engine;
 *
 *     public void enterState() {
 *         engine = new Engine();
 *         engine.addSystem(new RenderSystem());
 *         engine.addSystem(new CollisionSystem());
 *         engine.addSystem(new PhysicsSystem());
 *         //...
 *     }
 *
 *     public void exitState() {
 *         engine.dispose();
 *         engine = null;
 *     }
 *
 *     public void update(double dt) {
 *         engine.update(dt);
 *     }
 * }
 * </pre>
 *
 */
public final class Engine {

    /** The list of entities added to this engine. */
    private final List<Entity> entityList = new ArrayList<>();

    /** A map for all the different views of the entities. */
    private final Map<EntityFamily, List<Entity>> views = new HashMap<>();

    /** A map for all the different tags of the entities. */
    private final Map<Tag, List<Entity>> tags = new HashMap<>();

    /** A map for all the different layers of the entities. */
    private final Map<Layer, List<Entity>> layers = new HashMap<>();

    /** A map for all the different layers of the entities. */
    private final Map<String, Entity> entityIdCache = new HashMap<>();

    /** List of pending commands. */
    private final List<Command> commandList = new ArrayList<>();

    /** List of added systems. */
    private final List<EngineSystem> engineSystems = new ArrayList<>();

    /** Registered entity listeners. */
    private final List<IEntityListener> entityListeners = new CopyOnWriteArrayList<>();

    /** Registered entity listeners listening only for certain entity families. */
    private final Map<EntityFamily, List<IEntityListener>> filteredListeners = new HashMap<>();

    private final EntitySignal entitySignal = new EntitySignal();

    /** Pointer to the main instance of the game */
    private final Game game;

    /** Indicates if an update cycle is currently in progress. */
    private boolean updating;


    public Engine(Game game){
        this.game = game;
        entitySignal.addSignalListener(new EntityRemovedListener());
    }

    /**
     * Adds the specified entity listener to this engine.
     *
     * @param listener
     *            the entity listener to be added
     * @param family
     *            the family of entities this listeners is interested in
     */
    public void addEntityListener(IEntityListener listener, EntityFamily family){
        List<IEntityListener> entityListenerList = filteredListeners.get(family);
        if(entityListenerList == null){
            entityListenerList = new CopyOnWriteArrayList<>();
            filteredListeners.put(family, entityListenerList);
        }
        entityListenerList.add(listener);
    }

    /**
     * Removes the specified entity listeners from this engine.
     *
     * @param listener
     *            the entity listener to be removed
     * @param family
     *            the family of entities this listeners was interested in
     */
    public void removeEntityListener(IEntityListener listener, EntityFamily family){
        List<IEntityListener> entityListenerList = filteredListeners.get(family);
        if(entityListenerList != null) entityListenerList.remove(listener);
    }

    /**
     * Adds the specified entity listener to this engine.
     *
     * @param listener
     *            the entity listener to be added
     */
    public void addEntityListener(IEntityListener listener){
        if(entityListeners.contains(listener)) {System.out.println("listener already added " + listener); return;}
        entityListeners.add(listener);
    }

    /**
     * Removes the specified entity listeners from this engine.
     *
     * @param listener
     *            the entity listener to be removed
     */
    public void removeEntityListener(IEntityListener listener){
        entityListeners.remove(listener);
    }


    /**
     * Adds the specified entity to this engine. If the entity is added during
     * an update cycle, the entity will be added when the update cycle is
     * complete.
     *
     * @param entity
     *            the entity to be added
     */
    public void addEntity(Entity ...entity){
        for (Entity e: entity){
            if(updating){
                commandList.add(() -> addEntityInternal(e));
            } else {
                addEntityInternal(e);
            }
        }

    }

    /**
     * Removes all entities. If this method is invoked during an update cycle,
     * the command queued and executed when the update cycle is complete.
     */
    public void removeAll(){
        if(updating) {
            commandList.add(this::removeAllInternal);
        } else {
            removeAllInternal();
        }
    }

    /**
     * Removes the specified entity to this engine. If the entity is removed
     * during an update cycle, the entity will be removed when the update cycle
     * is complete.
     *
     * @param entity the entity to be removed
     */
    public void removeEntity(Entity entity){
        if(updating){
            commandList.add(() -> entitySignal.destroy(entity));
            commandList.add(() -> removeEntityInternal(entity));
        } else {
            entitySignal.destroy(entity);
            removeEntityInternal(entity);
        }
    }

    /**
     * The method that actually adds an entity.
     *
     * @param entity
     *            the entity to be added
     */
    private void addEntityInternal(Entity entity){
        if(entity.getEngine() != null) throw new IllegalArgumentException("entity already added to engine");
        if(entity.isActivated()) throw new IllegalArgumentException("entity has already been activated");
        if(entityList.contains(entity)) throw new IllegalArgumentException("entity already attached to engine");

        entityList.add(entity);
        entity.setEngine(this);
        entity.activate();

        addEntityToViews(entity);

        for(IEntityListener listener : entityListeners){
            listener.entityAdded(entity);
        }

        filteredListeners.forEach((key, value) -> {
            if(key.isMember(entity)) for(IEntityListener listener : value) { listener.entityAdded(entity);}
        });
    }

    /**
     * Adds the specified entity to the corresponding views.
     *
     * @param entity
     *            the entity to be added
     */
    private void addEntityToViews(Entity entity){
        for(EntityFamily family : views.keySet()){
            if(family.isMember(entity)) views.get(family).add(entity);
        }
    }

    /**
     * The method that actually removes the entity.
     *
     * @param entity
     *            the entity to be removed
     */
    private void removeEntityInternal(Entity entity){
        if(entity.getEngine() != this) return;


        assert entity.isActivated();
        assert entityList.contains(entity);

        for(IEntityListener listener: entityListeners){ listener.entityRemoved(entity); }
        filteredListeners.forEach((key, value) -> {
            if(key.isMember(entity)) for (IEntityListener listener : value) { listener.entityRemoved(entity);}
        });

        entity.deactivate();
        entity.setEngine(null);
        entityList.remove(entity);

        removeEntityFromViews(entity);
    }

    /**
     * The method that actually removes all entities.
     */
    private void removeAllInternal() {
        while (!entityList.isEmpty()) {
            Entity entity = entityList.get(0);
            entitySignal.destroy(entity);
            removeEntityInternal(entity);
        }
    }

    /**
     * Removes the specified entity from the corresponding views.
     *
     * @param entity
     *            the entity to be removed
     */
    private void removeEntityFromViews(Entity entity) {
        for (List<Entity> view : views.values()) {
            view.remove(entity);
        }
    }

    /**
     * Fixed Updates this engine and its attached systems. This method should be
     * called once each frame
     *
     */
    public void fixedUpdate() {
        updating = true;
        // fixedUpdate systems
        for (EngineSystem s : engineSystems) {
            if (s.isEnabled()) {
                s.fixedUpdate();
            }
        }

        // execute pending commands
        for (Command cmd : commandList) {
            cmd.execute();
        }
        commandList.clear();

        updating = false;
    }

    /**
     * Updates this engine and its attached systems. This method should be
     * called once each frame
     *
     */
    public void update() {

        updating = true;

        // update systems
        for (EngineSystem s : engineSystems) {
            if (s.isEnabled()) {
                s.update();
            }
        }

        // execute pending commands
        for (Command cmd : commandList) {
            cmd.execute();
        }
        commandList.clear();

        updating = false;
    }

    /**
     * Returns a list that will contain only entities which are members of the
     * specified family. It is safe to keep a reference of the returned list.
     * The list will be updated each update cycle and show reflect the current
     * state.
     *
     * <p>
     * The returned list cannot be modified. Any attempt to do so will result in
     * an {@code UnsupportedOperationException}.
     * </p>
     *
     * @param family
     *            the entity family this requested list should show
     * @return the list of entities
     */
    public List<Entity> getEntities(EntityFamily family) {
        List<Entity> view = views.get(family);
        if (view == null) {
            view = new ArrayList<>();
            views.put(family, view);
            initView(family, view);
        }
        return Collections.unmodifiableList(view);
    }

    /**
     * Initializes the specified view.
     *
     * @param family
     *            the entity family the new view should contain
     * @param view
     *            the view that should be initialized
     */
    private void initView(EntityFamily family, List<Entity> view) {
        assert view.isEmpty();
        for (Entity entity : entityList) {
            if (family.isMember(entity)) {
                view.add(entity);
            }
        }
    }

    /**
     * Returns a list of entities that have the given tag
     *
     * <p>
     * The returned list cannot be modified. Any attempt to do so will result in
     * an {@code UnsupportedOperationException}.
     * </p>
     *
     * @param tag tag used by the entities that will be returned
     * @return the list of entities
     */
    public List<Entity> getEntities(Tag tag) {
        List<Entity> tagList = tags.get(tag);
        if (tagList == null) {
            tagList = new ArrayList<>();
            tags.put(tag, tagList);
            initTagList(tag, tagList);
        }
        return Collections.unmodifiableList(tagList);
    }

    /**
     * Initializes the specified tag.
     *
     * @param tag
     *            the entity tag the new tagList should contain
     * @param tagList
     *            the tagList that should be initialized
     */
    private void initTagList(Tag tag, List<Entity> tagList) {
        assert tagList.isEmpty();
        for (Entity entity : entityList) {
            if (tag.equals(entity.getTag())) {
                tagList.add(entity);
            }
        }
    }

    /**
     * Returns a list of entities that have the given layer
     *
     * <p>
     * The returned list cannot be modified. Any attempt to do so will result in
     * an {@code UnsupportedOperationException}.
     * </p>
     *
     * @param layer tag used by the entities that will be returned
     * @return the list of entities
     */
    public List<Entity> getEntities(Layer layer) {
        List<Entity> layerList = layers.get(layer);
        if (layerList == null) {
            layerList = new ArrayList<>();
            layers.put(layer, layerList);
            initLayerList(layer, layerList);
        }
        return Collections.unmodifiableList(layerList);
    }

    /**
     * Initializes the specified layer.
     *
     * @param layer
     *            the entity layer the new tagList should contain
     * @param layerList
     *            the layerList that should be initialized
     */
    private void initLayerList(Layer layer, List<Entity> layerList) {
        assert layerList.isEmpty();
        for (Entity entity : entityList) {
            if (layer.equals(entity.getLayer())) {
                layerList.add(entity);
            }
        }
    }

    public Entity getEntity(String id){
        Entity entity = entityIdCache.get(id);
        if(entity == null){
            for (Entity e: entityList) {
                if(id.equals(e.getId())) {
                    entityIdCache.put(id, e);
                    return e;
                }
            }
        }
        return entity;
    }

    /**
     * Adds the specified system to this engine. Systems must not be added
     * during an update cycle. This method will call the {@code addedToEngine}
     * method of the specified system.
     *
     * @param system
     *            the system to be added
     * @throws IllegalStateException
     *             if an update cycle is currently in progress
     * @throws IllegalArgumentException
     *             if the specified system has already been added
     */
    public void addSystem(EngineSystem system) throws IllegalStateException, IllegalArgumentException {
        if (updating) throw new IllegalStateException("cannot add system while updating");
        if (engineSystems.contains(system)) throw new IllegalArgumentException("system already added");

        system.setEngine(this);
        engineSystems.add(system);
        system.addedToEngine(this);
    }

    /***
     * Removes the specified system from this engine. Systems must not be
     * removed during an update cycle. This method will call the
     * {@code removedFromEngine} method of the specified system.
     *
     * @param system
     *            the system to be added
     * @throws IllegalStateException
     *             if an update cycle is currently in progress
     * @throws IllegalArgumentException
     *             if the specified system in unknown
     */
    public void removeSystem(EngineSystem system) throws IllegalStateException, IllegalArgumentException {
        if (updating) throw new IllegalStateException("cannot remove system while updating");
        if (!engineSystems.contains(system)) throw new IllegalArgumentException("system is unknown");
        system.removedFromEngine(this);
        engineSystems.remove(system);
        system.setEngine(null);
    }

    /**
     * Returns {@code true} if there exists at least one system that matches the
     * specified interface of class.
     *
     * @param tClass
     *            the interface or class the requested system must implement
     * @return {@code true} if this engine as matching system, {@code false}
     *         otherwise
     */
    public boolean hasSystem(Class<?> tClass) {
        for (EngineSystem s : engineSystems) {
            if (tClass.isInstance(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves the first system that matches the specified interface of class.
     *
     * @param <T>
     *            type parameter used to avoid casts, irrelevant when calling
     *            this method
     * @param tClass
     *            the interface or class the requested system must implement
     * @return the system that matches the requirements
     * @throws IllegalArgumentException
     *             in case no suitable system could be found
     */
    public <T> T getSystem(Class<T> tClass) throws IllegalArgumentException {
        for (EngineSystem system : engineSystems) { if (tClass.isInstance(system)) return tClass.cast(system); }
        throw new IllegalArgumentException("system not found " + tClass.getName());
    }

    /**
     * Disposes this engine. All acquired resources will be released, all
     * entities will be deactivated and destroyed, all attached systems will be
     * detached and removed.
     *
     * <p>
     * Note: The attached systems will be removed in reverse order to resolve
     * dependencies without conflicts.
     * </p>
     *
     * @throws IllegalStateException
     *             in case this method is called during an update cycle
     */
    public void dispose() throws IllegalStateException {
        if (updating) throw new IllegalStateException("dispose not allowed during update");


        // dispose entities
        removeAll();
        entityList.clear();
        entitySignal.clear();
        views.clear();
        tags.clear();
        layers.clear();
        entityIdCache.clear();

        // dispose systems
        for (int i = engineSystems.size() - 1; i >= 0; --i) {
            EngineSystem system = engineSystems.get(i);
            system.setEnabled(false);
            system.removedFromEngine(this);
            system.setEngine(null);
        }
        engineSystems.clear();
    }

    /**
     * Returns the number of system registered at this engine.
     *
     * @return the number of systems
     */
    public int getNumOfSystems() {
        return engineSystems.size();
    }

    /**
     * Returns the system with the specified index.
     *
     * @param idx
     *            the index of the system to be returned
     * @return the requested system
     * @throws IndexOutOfBoundsException
     *             in case the specified index is {@code<} 0 or {@code>=} number
     *             of registered systems
     */
    public EngineSystem getSystem(int idx) throws IndexOutOfBoundsException {
        return engineSystems.get(idx);
    }

    /**
     * Returns the number of active entities within this engine.
     *
     * @return the number of entities
     */
    public int getNumOfEntities() {
        return entityList.size();
    }


    /**
     * Creates a default engine structure with all the base systems in the order of execution needed
     * @return Engine default engine structure
     */
    public static Engine defaultEngine(Game game){
        Engine defaultEngine = new Engine(game);

        //the order you add the systems are the order that is executed for the fixed updated and updated loops
        //all of fixed updated is done first, the all of update

        defaultEngine.addSystem(new ColliderSystem(EntityFamily.colliderEF));
        defaultEngine.addSystem(new ScriptSystem(EntityFamily.entityScriptEF));
        defaultEngine.addSystem(new PhysicsSystem(EntityFamily.rigidBodyEF));
        defaultEngine.addSystem(new CameraSystem(EntityFamily.cameraEF));
        defaultEngine.addSystem(new AnimationSystem(EntityFamily.animationEF));
        defaultEngine.addSystem(new RendererSystem(EntityFamily.spriteRendererEF));
        defaultEngine.addSystem(new SoundSystem());

        return defaultEngine;
    }


    public Game getGame() {
        return game;
    }

    @Override
    public String toString() {
        return "Engine{" +
                "entityList=" + entityList +
                ", views=" + views +
                ", commandList=" + commandList +
                ", engineSystems=" + engineSystems +
                ", entityListeners=" + entityListeners +
                ", filteredListeners=" + filteredListeners +
                ", updating=" + updating +
                '}';
    }

    /**
     * Interface for the command pattern.
     */
    private interface Command {
        void execute();
    }
}
