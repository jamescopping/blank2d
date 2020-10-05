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

import java.util.List;

/**
 * An engine system that processes a component family.
 */
public abstract class IteratingSystem extends EngineSystem {

    /** The family of components this system processes. */
    private final EntityFamily family;

    /** View to the currently present entities which match the family. */
    private List<Entity> entityList;

    /**
     * Creates a new instance
     *
     * @param family
     *            the family of entity components this system processes
     * @throws NullPointerException
     *             in case the specified entity family is null
     */
    public IteratingSystem(EntityFamily family) {
        if (family == null) {
            throw new NullPointerException("entity family must not be null");
        }
        this.family = family;
    }

    @Override
    public void addedToEngine(Engine e) {
        entityList = e.getEntities(family);
    }

    @Override
    public void removedFromEngine(Engine e) {
        entityList = null;
    }

    @Override
    public void update() {
        for (Entity entity : entityList) {
            processEntity(entity);
        }
    }

    @Override
    public void fixedUpdate() {
        for (Entity entity : entityList) {
            fixedProcessEntity(entity);
        }
    }

    /**
     * Returns the list of entities this system processes.
     *
     * @return the list of entities.
     */
    public final List<Entity> getEntityList() {
        return entityList;
    }

    /**
     * Processes one entity of the family.
     *
     * @param entity
     *            the entity to process
     */
    protected void processEntity(Entity entity) {}

    protected void fixedProcessEntity(Entity entity) {}

}