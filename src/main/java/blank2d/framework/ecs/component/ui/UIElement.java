package blank2d.framework.ecs.component.ui;

import blank2d.framework.ecs.Component;
import blank2d.util.math.Rect;

public class UIElement extends Component {

    UIContent uiContent;
    Rect rect = new Rect();

    UIElement(){
        uiContent = new UIContent<String>();
    }
}
