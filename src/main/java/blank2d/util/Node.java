package blank2d.util;

public class Node<T> {

    private T data = null;
    private Node<T> child = null;
    private Node<T> parent = null;

    public Node(){}
    public Node(T data){
        setData(data);
    }

    public Node<T> getChild() {
        return child;
    }

    public void setChild(Node<T> child) {
        this.child = child;
    }

    public boolean hasChild(){
        return child != null;
    }
    public boolean hasParent(){
        return parent != null;
    }

    public Node<T> getParent() {
        return parent;
    }
    public void setParent(Node<T> parent) {
        this.parent = parent;
    }
    public T getData() {
        return data;
    }
    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Node{" +
                "data=" + data.toString() +
                '}';
    }
}
