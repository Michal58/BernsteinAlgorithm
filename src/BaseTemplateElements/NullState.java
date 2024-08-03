package BaseTemplateElements;

public class NullState implements AlgorithmState {
    private static NullState instance=new NullState();
    private NullState(){
    }
    private static NullState getInstance(){
        return instance;
    }
}
