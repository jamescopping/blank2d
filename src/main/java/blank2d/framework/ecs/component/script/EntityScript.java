package blank2d.framework.ecs.component.script;

import blank2d.framework.ecs.Component;

public abstract class EntityScript extends Component implements IEntityScript {

    protected EntityScript(){
        awake();
    }

    @Override
    protected final void activate() {
        start();
    }

}
