package blank2d.util;

import java.util.Iterator;
import java.util.function.Consumer;

public class Stack<T> implements Iterable<T> {

    private Node<T> top = null;
    private int size = 0;

    @Override
    public String toString() {
        return "Stack{" +
                "top=" + top +
                ", size=" + size +
                '}';
    }

    public boolean isEmpty(){
        return size <= 0;
    }

    public int size(){
        return size;
    }

    public void push(T data){
        Node<T> node = new Node<>();
        node.setData(data);
        if(top != null){
            top.setParent(node);
        }
        node.setChild(top);
        top = node;
        size++;
    }

    public T pop(){
        T data = null;
        if(!isEmpty()) {
            Node<T> child = top.getChild();
            top.setChild(null);
            top.setParent(null);
            data = top.getData();

            top = child;
            if(child != null) {
                top.setParent(null);
            }
            size--;
        }else{
            System.err.println("The stack is empty, there is nothing to pop off!");
        }
        return data;
    }

    public T peek(){
        return top.getData();
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            private Node<T> top = Stack.this.top;

            @Override
            public boolean hasNext() {
                if (top == null) return false;
                return top.hasChild();
            }

            @Override
            public T next() {
                top = top.getChild();
                return top.getData();
            }
        };
    }

}
