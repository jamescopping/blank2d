package blank2d.util;

import java.util.*;

public class Graph<T> {

    private Map<Vertex<T>, List<Vertex<T>>> adjVertices;
    public Graph(){ adjVertices = new HashMap<>(); }

    @Override
    public String toString() {
        return "Graph{" +
                "adjVertices=" + adjVertices +
                '}';
    }

    public void addVertex(String label){
        adjVertices.putIfAbsent(new Vertex<>(label), new ArrayList<>());
    }

    public void addVertex(String label, T object){
        adjVertices.putIfAbsent(new Vertex<>(label, object), new ArrayList<>());
    }

    public void removeVertex(String label){
        Vertex<T> v = new Vertex<>(label);
        adjVertices.values().forEach(e -> e.remove(v));
        adjVertices.remove(new Vertex<T>(label));
    }

    public void addEdge(String label1, String label2){
        Vertex<T> v1 = new Vertex<>(label1);
        Vertex<T> v2 = new Vertex<>(label2);
        adjVertices.get(v1).add(v2);
        adjVertices.get(v2).add(v1);
    }

    public void removeEdge(String label1, String label2) {
        Vertex<T> v1 = new Vertex<>(label1);
        Vertex<T> v2 = new Vertex<>(label2);
        List<Vertex<T>> eV1 = adjVertices.get(v1);
        List<Vertex<T>> eV2 = adjVertices.get(v2);
        if (eV1 != null)
            eV1.remove(v2);
        if (eV2 != null)
            eV2.remove(v1);
    }

    public List<Vertex<T>> getAdjVertices(String label) {
        return adjVertices.get(new Vertex<T>(label));
    }
    public Map<Vertex<T>, List<Vertex<T>>> getAdjVertices() {
        return adjVertices;
    }
    public void setAdjVertices(Map<Vertex<T>, List<Vertex<T>>> adjVertices) {
        this.adjVertices = adjVertices;
    }

    public <K> Set<String> depthFirstTraversal(Graph<K> graph, String root) {
        Set<String> visited = new LinkedHashSet<>();
        Stack<String> stack = new Stack<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            String vertex = stack.pop();
            if (!visited.contains(vertex)) {
                visited.add(vertex);
                for (Vertex<K> v : graph.getAdjVertices(vertex)) {
                    stack.push(v.label);
                }
            }
        }
        return visited;
    }

    public <K> Set<String> breadthFirstTraversal(Graph<K> graph, String root) {
        Set<String> visited = new LinkedHashSet<>();
        Queue<String> queue = new Queue<>();
        queue.enqueue(root);
        visited.add(root);
        while (!queue.isEmpty()) {
            String vertex = queue.dequeue();
            for (Vertex<K> v : graph.getAdjVertices(vertex)) {
                if (!visited.contains(v.label)) {
                    visited.add(v.label);
                    queue.enqueue(v.label);
                }
            }
        }
        return visited;
    }
}
