package blank2d.framework.ecs.system;


import blank2d.Game;
import blank2d.framework.ecs.Entity;
import blank2d.framework.ecs.EntityFamily;
import blank2d.framework.ecs.IteratingSystem;
import blank2d.framework.ecs.component.script.EntityScript;
import blank2d.framework.ecs.component.script.IEntityScript;

import java.util.List;

public class ScriptSystem extends IteratingSystem {

    /**
     * Creates a new instance
     *
     * @param family the family of entity components this system processes
     * @throws NullPointerException in case the specified entity family is null
     */
    public ScriptSystem(EntityFamily family) {
        super(family);
    }

    @Override
    protected void processEntity(Entity entity) {
        List<EntityScript> scripts = entity.getAllComponents(EntityScript.class);
        scripts.forEach(IEntityScript::update);
        scripts.forEach(IEntityScript::lateUpdate);
        if(getEngine().getGame().isDebugMode()) scripts.forEach(IEntityScript::debug);
    }

    @Override
    protected void fixedProcessEntity(Entity entity) {
       entity.getAllComponents(EntityScript.class).forEach(IEntityScript::fixedUpdate);
    }
}
