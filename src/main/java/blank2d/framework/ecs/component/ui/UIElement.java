package blank2d.framework.ecs.component.ui;

import blank2d.framework.ecs.Component;
import blank2d.util.math.Rect;

public class UIElement extends Component {

    UIContent content;
    Rect rect = new Rect();

    UIElement(){
        content = new UIContent<String>();
    }

}
