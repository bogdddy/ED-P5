package cat.urv.deim;

public class Vertex<V extends Comparable<V>, K, E extends Comparable<E>> implements Comparable<Vertex<V, K, E>> {

    private V value;
    private LlistaGenerica<Aresta<K, E>> adjacencies;

    public Vertex(V value) {
        this.value = value;
        this.adjacencies = new LlistaNoOrdenada<>();
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public LlistaGenerica<Aresta<K, E>> getAdjacencies() {
        return adjacencies;
    }

    @Override
    public int compareTo(Vertex<V, K, E> other) {
        return this.value.compareTo(other.value);
    }
}
