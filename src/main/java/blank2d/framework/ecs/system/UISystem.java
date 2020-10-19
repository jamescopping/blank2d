package blank2d.framework.ecs.system;

import blank2d.framework.ecs.EntityFamily;
import blank2d.framework.ecs.IteratingSystem;

public class UISystem extends IteratingSystem {
    /**
     * Creates a new instance
     *
     * @param family the family of entity components this system processes
     * @throws NullPointerException in case the specified entity family is null
     */
    public UISystem(EntityFamily family) {
        super(family);
    }


}
