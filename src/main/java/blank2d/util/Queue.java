package blank2d.util;

import java.util.Iterator;

public class Queue<T> implements Iterable<Node<T>> {

    private Node<T> head = null;
    private int size = 0 ;

    @Override
    public String toString() {
        return "Queue{" +
                "head=" + head.toString() +
                ", size=" + size +
                '}';
    }

    public int size(){
        return size;
    }

    public boolean isEmpty(){
        return size <= 0;
    }

    public void enqueue(T data){
        Node<T> node = new Node<>();
        node.setData(data);
        if(isEmpty()){
            head = node;
        }else{
            peekTail().setChild(node);
        }
        size++;
    }

    public T dequeue(){
        Node<T> first = null;
        if(!isEmpty()){
            first = head;
            if(!head.hasChild()){
                head = null;
            }else{
                head = head.getChild();
            }
            size--;
            return first.getData();
        }else{
            return null;
        }
    }

    public Node<T> peekHead(){
        return head;
    }

    public Node<T> peekTail(){
        Node<T> tail = head;
        while(tail.hasChild()){
            tail = tail.getChild();
        }
        return tail;
    }

    public void clear() {
        size = 0;
        head = null;
    }

    public boolean contains(T data){
        boolean contains = false;
        for(Node<T> tNode: this){
            if(data.equals(tNode.getData())){
                contains = true;
                break;
            }
        }
        return contains;
    }

    @Override
    public Iterator<Node<T>> iterator() {
        return new Iterator<Node<T>>() {

            private Node<T> head = Queue.this.head;

            @Override
            public boolean hasNext() {
                if (head == null) return false;
                return head.hasChild();
            }

            @Override
            public Node<T> next() {
                head = head.getChild();
                return head;
            }
        };
    }


}
