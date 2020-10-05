package blank2d.framework.gamestate;

import blank2d.Game;
import blank2d.framework.ecs.Engine;

public abstract class GameState {

    protected Engine gameEngine;
    protected GameStateMachine gameStateMachine;
    protected Game game;

    public void init(){
        game = gameStateMachine.getGame();
        gameEngine = new Engine(game);
    }

    public abstract void cleanup();

    public abstract void pause();
    public abstract void resume();

    public abstract void handleEvents();
    public abstract void update();
    public abstract void fixedUpdate();
    public abstract void render(float interpolate);

    protected void setGameEngine(Engine engine){ this.gameEngine = engine; }

    public void setGameStateMachine(GameStateMachine gameStateMachine) {
        this.gameStateMachine = gameStateMachine;
    }

    public GameStateMachine getGameStateMachine() {
        return gameStateMachine;
    }

    public Engine getGameEngine() {
        return gameEngine;
    }

    public Game getGame() {
        return game;
    }
}

