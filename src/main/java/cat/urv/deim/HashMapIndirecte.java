package cat.urv.deim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import cat.urv.deim.exceptions.ElementNoTrobat;

public class HashMapIndirecte<K extends Comparable<K> , V extends Comparable<V>> implements IHashMap<K, V> {

    private ArrayList<NodeHash<K,V>> taula;
    private int numElem;
    private int capacitat;
    private final double INDEXCARREGA = 0.75;

    public HashMapIndirecte(int mida) {

        capacitat = mida;
        numElem = 0;
        taula = new ArrayList<>(Collections.nCopies(mida, null));

    }

    private int calcularIndex(K clau) {
        return Math.abs(clau.hashCode()) % capacitat;
    }

    private void redimensionarTaula() {

        int novaCapacitat = capacitat * 2;
        ArrayList<NodeHash<K,V>> novaTaula = new ArrayList<>(Collections.nCopies(novaCapacitat, null));

        for (NodeHash<K,V> node : taula) {
            NodeHash<K,V> nodeActual = node;
            while (nodeActual != null) {

                K key = nodeActual.key;
                V value = nodeActual.value;
                int nouIndex = Math.abs(key.hashCode() % novaCapacitat);
                NodeHash<K,V> nouNode = novaTaula.get(nouIndex);

                if (nouNode == null)
                    novaTaula.set(nouIndex, new NodeHash<>(key, value));
                else {
                    while (nouNode.seguent != null)
                        nouNode = nouNode.seguent;
                    nouNode.seguent = new NodeHash<>(key, value);
                }

                nodeActual = nodeActual.seguent;
            }
        }

        taula = novaTaula;
        capacitat = novaCapacitat;
    }

    public void inserir(K key, V value) {

        int index = calcularIndex(key);
        NodeHash<K,V> nodeActual = taula.get(index);

        if(nodeActual == null)
            taula.set(index, new NodeHash<K,V> (key, value));

        else{

            while (nodeActual.seguent != null)
                nodeActual = nodeActual.seguent;

            nodeActual.seguent = new NodeHash<K,V> (key, value);

        }

        numElem++;

        if(factorCarrega() > INDEXCARREGA)
            redimensionarTaula();

    }

    @Override
    public V consultar(K key) throws ElementNoTrobat {

        int index = calcularIndex(key);
        NodeHash<K,V> nodeActual = taula.get(index);

        while (nodeActual != null) {

            if (nodeActual.key.equals(key))
                return nodeActual.value;

            else
                nodeActual = nodeActual.seguent;

        }

        throw new ElementNoTrobat();

    }

    @Override
    public void esborrar(K key) throws ElementNoTrobat {

        int index = calcularIndex(key);
        NodeHash<K, V> nodeActual = taula.get(index);
        NodeHash<K, V> nodeAnterior = null;

        while (nodeActual != null) {

            if (nodeActual.key.equals(key)) {

                if (nodeAnterior != null)
                    nodeAnterior.seguent = nodeActual.seguent;
                else
                    taula.set(index, nodeActual.seguent);
                    numElem--;
                    return;

            } else {
                nodeAnterior = nodeActual;
                nodeActual = nodeActual.seguent;
            }
        }

        throw new ElementNoTrobat();
    }

    @Override
    public boolean buscar(K key) {

        int index = calcularIndex(key);
        NodeHash<K,V> nodeActual = taula.get(index);

        while (nodeActual != null) {

            if (nodeActual.key.equals(key))
                return true;

            else
                nodeActual = nodeActual.seguent;

        }

        return false;
    }

    @Override
    public boolean esBuida() {
        return numElem == 0;
    }

    @Override
    public int numElements() {
        return numElem;
    }

    @Override
    public LlistaGenerica<K> obtenirClaus() {

        LlistaGenerica<K> claus = new LlistaNoOrdenada<>();

        for (NodeHash<K, V> node : taula) {
            while (node != null) {
                claus.inserir(node.key);
                node = node.seguent;
            }
        }

        return claus;
    }

    @Override
    public float factorCarrega() {
        return (float) numElem / capacitat;
    }

    @Override
    public int midaTaula() {
        return capacitat;
    }

    @Override
    public Iterator<V> iterator() {
        return new HashMapIndirecteIterator<>();
    }

    private class HashMapIndirecteIterator<T> implements Iterator<V> {

        private ArrayList<V> elements;
        private int index;

        public HashMapIndirecteIterator() {

            elements = new ArrayList<>();
            for (NodeHash<K, V> node : taula) {

                NodeHash<K, V> nodeActual = node;
                while (nodeActual != null) {
                    elements.add(nodeActual.value);
                    nodeActual = nodeActual.seguent;
                }

            }
            index = 0;
        }

        @Override
        public boolean hasNext() {
            return index < elements.size();
        }

        @Override
        public V next() {

            if (hasNext()) return elements.get(index++);

            else throw new IndexOutOfBoundsException();

        }

    }

}
