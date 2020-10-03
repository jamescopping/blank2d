package blank2d.framework.ecs.component.rendering;

import blank2d.framework.ecs.Component;
import blank2d.framework.ecs.component.physics2d.Transform;

public class Camera extends Component {

    Transform transform;

    @Override
    protected void activate() {
        transform = getComponent(Transform.class);
    }
}
