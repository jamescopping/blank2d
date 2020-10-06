package blank2d.framework.ecs.component.script;

import blank2d.framework.ecs.Component;
import blank2d.framework.Screen;
import blank2d.framework.input.InputManager;

public abstract class EntityScript extends Component implements IEntityScript {

    public final Screen screen = Screen.getInstance();
    public final InputManager inputManager = InputManager.getInstance();

    protected EntityScript(){
        awake();
    }

    @Override
    protected final void activate() {
        start();
    }

}
