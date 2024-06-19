package cat.urv.deim;

import java.util.Iterator;

import cat.urv.deim.exceptions.ElementNoTrobat;

public abstract class LlistaGenerica<E extends Comparable<E>> implements ILlistaGenerica<E>, Iterable<E> {

    protected NodeLlista<E> fantasma;
    protected int numElem;

    public LlistaGenerica() {
        fantasma = new NodeLlista<E>(null);
        numElem=0;
    }

    //Metode per a comprovar si un element esta a la llista
    public boolean existeix(E e) {

        try {
            buscar(e);
            return true;
        }catch (ElementNoTrobat ex){
            return false;
        }

    }

    //Metode per a comprovar si la llista te elements
    public boolean esBuida() {
        return numElem == 0;
    }

    //Metode per a obtenir el nombre d'elements de la llista
    public int numElements() {
        return numElem;
    }

    // Método para obtener un iterador sobre los elementos de la lista
    public Iterator<E> iterator() {
        return new ListaGenericaIterator<E>(fantasma);
    }

    // Clase interna para implementar el iterador sobre la lista
    private class ListaGenericaIterator<T extends Comparable<T>> implements Iterator<T> {
        private NodeLlista<T> current;

        public ListaGenericaIterator(NodeLlista<T> inicio) {
            current = inicio;
        }

        public boolean hasNext() {
            return current != null && current.seguent != null;
        }

        public T next() {
            if (!hasNext()) throw new java.util.NoSuchElementException();
            current = current.seguent;
            return current.element;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
