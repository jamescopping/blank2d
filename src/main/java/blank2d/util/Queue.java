package blank2d.util;

import java.util.Iterator;

public class Queue<T> implements Iterable<T> {

    private Node<T> head = null;
    private Node<T> tail = null;
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
            tail = head;
        }else{
            tail.setChild(node);
            tail = tail.getChild();
        }
        size++;
    }

    public T dequeue(){
        Node<T> first = null;
        if(!isEmpty()){
            first = head;
            if(!head.hasChild()){
                head = null;
                tail = null;
            }else{
                head = head.getChild();
            }
            size--;
            return first.getData();
        }else{
            return null;
        }
    }

    public T peekHead(){
        return head.getData();
    }

    public T peekTail(){
        return tail.getData();
    }

    public void clear() {
        size = 0;
        head = null;
    }

    public void loop(boolean setLoop){
        if(setLoop){
            tail.setChild(head);
        }else{
            tail.setChild(null);
        }
    }

    public boolean contains(T data){
        boolean contains = false;
        for(T tData: this){
            if(data.equals(tData)){
                contains = true;
                break;
            }
        }
        return contains;
    }


    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            private Node<T> head = Queue.this.head;

            @Override
            public boolean hasNext() {
                if (head == null) return false;
                return head.hasChild();
            }

            @Override
            public T next() {
                head = head.getChild();
                return head.getData();
            }
        };
    }
}
