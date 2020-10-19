package blank2d.framework.gamestate;

import blank2d.Game;
import blank2d.framework.ecs.Engine;
import blank2d.framework.input.InputManager;
import blank2d.framework.screen.Screen;

public abstract class GameState {

    protected Engine gameEngine;
    protected GameStateMachine gameStateMachine;
    protected Game game;

    protected InputManager input = InputManager.getInstance();
    protected Screen screen = Screen.getInstance();

    public void init(){
        game = gameStateMachine.getGame();
        gameEngine = new Engine(game);
    }

    public void init(Engine engine){
        game = gameStateMachine.getGame();
        gameEngine = engine;
    }

    public abstract void cleanup();

    public abstract void pause();
    public abstract void resume();

    public abstract void fixedUpdate();
    public abstract void handleEvents();
    public abstract void update();
    public abstract void render(float interpolate);

    protected void setGameEngine(Engine engine){ this.gameEngine = engine; }
    protected void setGameStateMachine(GameStateMachine gameStateMachine) {
        this.gameStateMachine = gameStateMachine;
    }
}

