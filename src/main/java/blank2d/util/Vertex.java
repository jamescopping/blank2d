package blank2d.util;

import java.util.Objects;

public class Vertex<T> {
    String label;
    private T object;
    public Vertex(String label, T object){
        this.object = object;
        this.label = label;
    }
    public Vertex(String label){
        this.label = label;
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "label='" + label + '\'' +
                ", object=" + object +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex<?> vertex = (Vertex<?>) o;
        return Objects.equals(label, vertex.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label);
    }

    public T getObject() { return object; }
    public void setObject(T object){ this.object = object; }
}
