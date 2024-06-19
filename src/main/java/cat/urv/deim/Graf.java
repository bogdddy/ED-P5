package cat.urv.deim;

import cat.urv.deim.exceptions.VertexNoTrobat;
import cat.urv.deim.exceptions.ArestaNoTrobada;
import cat.urv.deim.exceptions.ElementNoTrobat;

public class Graf<K extends Comparable<K>, V extends Comparable<V>, E extends Comparable<E>> implements IGraf<K, V, E> {

    private HashMapIndirecte<K, Vertex<V, K, E>> llistaVertex;

    public Graf(int mida) {
        llistaVertex = new HashMapIndirecte<>(mida);
    }

    @Override
    public void inserirVertex(K key, V value) {
        Vertex<V, K, E> vertex = new Vertex<>(value);
        llistaVertex.inserir(key, vertex);
    }


    public void setValue(K key, V value) throws VertexNoTrobat{
        try {
            this.llistaVertex.consultar(key).setValue(value);
        } catch (ElementNoTrobat e) {
            throw new VertexNoTrobat();
        }
    }

    @Override
    public V consultarVertex(K key) throws VertexNoTrobat {
        try {
            return llistaVertex.consultar(key).getValue();
        } catch (ElementNoTrobat e) {
            throw new VertexNoTrobat();
        }
    }

    public boolean buscarVertex(K key) {
        try {
            return llistaVertex.buscar(key);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void esborrarVertex(K key) throws VertexNoTrobat {

        try {
            Vertex<V, K, E> vertex = llistaVertex.consultar(key);

            // Borrar arestes asociades
            for (Aresta<K, E> aresta : vertex.getAdjacencies()) {
                K desti = aresta.getDesti().equals(key) ? aresta.getOrigen() : aresta.getDesti();
                Vertex<V, K, E> adjVertex = llistaVertex.consultar(desti);
                adjVertex.getAdjacencies().esborrar(aresta);
            }

            llistaVertex.esborrar(key);
        } catch (ElementNoTrobat e) {
            throw new VertexNoTrobat();
        }

    }

    @Override
    public boolean esBuida() {
        return llistaVertex.esBuida();
    }

    @Override
    public int numVertex() {
        return llistaVertex.numElements();
    }

    @Override
    public LlistaGenerica<K> obtenirVertexIDs() {
        return llistaVertex.obtenirClaus();
    }

    @Override
    public void inserirAresta(K v1, K v2, E pes) throws VertexNoTrobat {

        try {
            if (!existeixAresta(v1, v2)) {
                Aresta<K, E> aresta = new Aresta<>(v1, v2, pes);
                Vertex<V, K, E> vertexV1 = llistaVertex.consultar(v1);
                vertexV1.getAdjacencies().inserir(aresta);
                Vertex<V, K, E> vertexV2 = llistaVertex.consultar(v2);
                vertexV2.getAdjacencies().inserir(aresta);
            }

        } catch (ElementNoTrobat e) {
            throw new VertexNoTrobat();
        }

    }

    @Override
    public void inserirAresta(K v1, K v2) throws VertexNoTrobat {
        inserirAresta(v1, v2, null);
    }

    @Override
    public boolean existeixAresta(K v1, K v2) throws VertexNoTrobat {

        try {

            Vertex<V, K, E> vertex = llistaVertex.consultar(v1);
            for (Aresta<K, E> aresta : vertex.getAdjacencies()) {
                if ((aresta.getOrigen().equals(v1) && aresta.getDesti().equals(v2)) ||
                    (aresta.getOrigen().equals(v2) && aresta.getDesti().equals(v1))) {
                    return true;
                }
            }

        } catch (ElementNoTrobat e) {
            throw new VertexNoTrobat();
        }

        return false;
    }

    @Override
    public E consultarAresta(K v1, K v2) throws VertexNoTrobat, ArestaNoTrobada {

        try {
            Vertex<V, K, E> vertex = llistaVertex.consultar(v1);
            for (Aresta<K, E> aresta : vertex.getAdjacencies()) {
                if ((aresta.getOrigen().equals(v1) && aresta.getDesti().equals(v2)) ||
                    (aresta.getOrigen().equals(v2) && aresta.getDesti().equals(v1))) {
                    return aresta.getPes();
                }
            }
        } catch (ElementNoTrobat e) {
            throw new VertexNoTrobat();
        }

        throw new ArestaNoTrobada();
    }

    @Override
    public void esborrarAresta(K v1, K v2) throws VertexNoTrobat, ArestaNoTrobada {

        try {
            Vertex<V, K, E> vertex = llistaVertex.consultar(v1);
            for (Aresta<K, E> aresta : vertex.getAdjacencies()) {
                if ((aresta.getOrigen().equals(v1) && aresta.getDesti().equals(v2)) ||
                    (aresta.getOrigen().equals(v2) && aresta.getDesti().equals(v1))) {
                    vertex.getAdjacencies().esborrar(aresta);
                    Vertex<V, K, E> adjVertex = llistaVertex.consultar(v2);
                    adjVertex.getAdjacencies().esborrar(aresta);
                    return;
                }
            }

        } catch (ElementNoTrobat e) {
            throw new VertexNoTrobat();
        }
        throw new ArestaNoTrobada();
    }

    @Override
    public int numArestes() {

        int nArestes = 0;
        for (K key : llistaVertex.obtenirClaus()) {
            try {
                Vertex<V, K, E> vertex = llistaVertex.consultar(key);
                nArestes += vertex.getAdjacencies().numElements();
            } catch (ElementNoTrobat e) {
                // No s'hauria d'entrar mai aquí
            }
        }
        return nArestes / 2; // Eliminem duplicats
    }

    @Override
    public boolean vertexAillat(K v1) throws VertexNoTrobat {

        try {
            Vertex<V, K, E> vertex = llistaVertex.consultar(v1);
            return vertex.getAdjacencies().esBuida();
        } catch (ElementNoTrobat e) {
            throw new VertexNoTrobat();
        }

    }

    @Override
    public int numVeins(K v1) throws VertexNoTrobat {
        return obtenirVeins(v1).numElements();
    }

    @Override
    public LlistaGenerica<K> obtenirVeins(K v1) throws VertexNoTrobat {

        LlistaGenerica<K> veins = new LlistaNoOrdenada<>();

        try {
            Vertex<V, K, E> vertex = llistaVertex.consultar(v1);
            for (Aresta<K, E> aresta : vertex.getAdjacencies()) {
                if (aresta.getOrigen().equals(v1)) {
                    veins.inserir(aresta.getDesti());
                } else if (aresta.getDesti().equals(v1)) {
                    veins.inserir(aresta.getOrigen());
                }
            }
        } catch (ElementNoTrobat e) {
            throw new VertexNoTrobat();
        }

        return veins;
    }

    // Mètodes opcionals:

    @Override
    public ILlistaGenerica<K> obtenirNodesConnectats(K v1) throws VertexNoTrobat {
        ILlistaGenerica<K> nodesConnectats = new LlistaOrdenada<>();
        return nodesConnectats;
    }

    @Override
    public ILlistaGenerica<K> obtenirComponentConnexaMesGran() {
        return null;
    }


}
