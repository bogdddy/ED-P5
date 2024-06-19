package cat.urv.deim;

public class Aresta<K , E extends Comparable<E>> implements Comparable<Aresta<K, E>> {

    private K origen;
    private K desti;
    private E pes;

    public Aresta(K o, K d, E p) {
        origen = o;
        desti = d;
        pes = p;
    }

    public K getOrigen() {
        return origen;
    }

    public void setOrigen(K o) {
        origen = o;
    }

    public K getDesti() {
        return desti;
    }

    public void setDesti(K d) {
        desti = d;
    }

    public E getPes() {
        return pes;
    }

    public void setPes(E pes) {
        this.pes = pes;
    }

    @Override
    public int compareTo(Aresta<K, E> otraArista) {
        return pes.compareTo(otraArista.getPes());
    }

}
