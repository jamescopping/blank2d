package blank2d.framework.gamestate;

import blank2d.util.Stack;

public class GameStateMachine {

    private final Stack<GameState> stateStack = new Stack<>();
    private GameState newGameState;

    private char stateTransitionFlag = 0;
    private boolean isRemoving = false;
    private boolean isAdding = false;
    private boolean isReplacing = false;
    private boolean running = false;

    @Override
    public String toString() {
        return "GameStateMachine{" +
                "stateStack=" + stateStack +
                ", newGameState=" + newGameState +
                ", stateTransitionFlag=" + stateTransitionFlag +
                ", isRemoving=" + isRemoving +
                ", isAdding=" + isAdding +
                ", isReplacing=" + isReplacing +
                '}';
    }

    public void pushGameState(GameState newGameState){
        pushGameState(newGameState, true);
    }

    public void pushGameState(GameState newGameState, boolean isReplacing){
        isAdding = true;
        this.isReplacing = isReplacing;
        this.newGameState = newGameState;
    }

    public void popGameState(){
        isRemoving = true;
    }

    public boolean processGameStateChange(){
        if(!isRunning()) return false;
        if(isRemoving && !stateStack.isEmpty()){
            stateStack.peek().cleanup();
            stateStack.pop();
            isRemoving = false;
            if(!stateStack.isEmpty()){
                stateStack.peek().resume();
            }
        }

        if(isAdding){
            if(!stateStack.isEmpty()){
                if(isReplacing){
                    stateStack.peek().cleanup();
                    stateStack.pop();
                }else{
                    stateStack.peek().pause();
                }
            }
            stateStack.push(newGameState);
            stateStack.peek().init();
            isAdding = false;
        }

        if(stateStack.isEmpty()) {
            stop();
            return false;
        }else{
            return true;
        }

    }

    public GameState getActiveGameState(){
        if(!stateStack.isEmpty()){
            return stateStack.peek();
        }else{
            System.err.println("The stateStack is empty, oops!"); //TODO replace with an error state screen, just so its shows something
            return null;
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void stop(){
        this.running = false;
    }

    public void start(){
        this.running = true;
    }
}
