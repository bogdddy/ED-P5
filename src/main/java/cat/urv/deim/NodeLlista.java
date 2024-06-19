package cat.urv.deim;

public class NodeLlista <E extends Comparable<E>> {

    E element;
    NodeLlista<E> seguent;
    NodeLlista<E> anterior;

    public NodeLlista (E elem) {
        element = elem;
        seguent = null;
        anterior = null;
    }

}
