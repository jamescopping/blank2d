package blank2d.framework.gamestate;

import blank2d.framework.ecs.Engine;

public abstract class GameState {

    protected Engine gameEngine;

    public GameState() {
        this.gameEngine = new Engine();
    }

    public abstract void init();
    public abstract void cleanup();

    public abstract void pause();
    public abstract void resume();

    public abstract void handleEvents();
    public abstract void update();
    public abstract void fixedUpdate();
    public abstract void render(float interpolate);

    protected void setGameEngine(Engine engine){ this.gameEngine = engine; }
}

