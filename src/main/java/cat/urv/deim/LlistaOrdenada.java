package cat.urv.deim;

import cat.urv.deim.exceptions.ElementNoTrobat;
import cat.urv.deim.exceptions.PosicioForaRang;

/**
 * Es guarda l'element que està al mig, per tal de no haver de recorrer
 * tota la llista cada cop que busquem algo
 */
public class LlistaOrdenada<E extends Comparable<E>> extends LlistaGenerica<E> {

    private NodeLlista<E> mig;

    public LlistaOrdenada() {
        super();
    }

    //Metode per insertar un element a la llista. No importa la posicio on s'afegeix l'element
    public void inserir(E e) {
        NodeLlista<E> nouNode = new NodeLlista<E>(e);

        if (esBuida()) {
            fantasma.seguent = nouNode;
            mig = nouNode;
        } else {
            NodeLlista<E> actual = fantasma.seguent;
            boolean comp = e.compareTo(mig.element) < 0;

            while (actual.seguent != null && e.compareTo(actual.seguent.element) > 0) {
                actual = actual.seguent;
            }

            nouNode.seguent = actual.seguent;
            nouNode.anterior = actual;
            if (actual.seguent != null) {
                actual.seguent.anterior = nouNode;
            }
            actual.seguent = nouNode;

            if (numElem > 5) {
                mig = comp ? mig.anterior : mig.seguent;
            }
        }

        numElem++;
    }



    //Metode per a esborrar un element de la llista
    public void esborrar(E e) throws ElementNoTrobat {

        NodeLlista<E> n = fantasma.seguent;

        while (n != null) {
            if (e.equals(n.element)){
                if (n.seguent == null) n.anterior.seguent = null;
                else {
                    n.anterior.seguent = n.seguent;
                    n.seguent.anterior = n.anterior;
                }
                numElem--;
            }
            n = n.seguent;
        }

        throw new ElementNoTrobat();

    }

    //Metode per a consultar un element de la llista per posicio
    //La primera dada esta a la posicio 0
    public E consultar(int pos) throws PosicioForaRang {

        if (pos < 0 || pos >= numElements())
            throw new PosicioForaRang();

        NodeLlista<E> actual;
        if (pos <= numElem / 2) { // principi
            actual = fantasma.seguent;
            for (int i = 0; i < pos; i++) {
                actual = actual.seguent;
            }
        } else { // final
            actual = mig;
            for (int i = numElem - 1; i > pos; i--) {
                actual = actual.anterior;
            }
        }

        return actual.element;
    }

    //Metode per a comprovar en quina posicio esta un element a la llista
    //La primera dada esta a la posicio 0
    public int buscar(E e) throws ElementNoTrobat {

        NodeLlista<E> n = fantasma.seguent;
        int cont = 0;

        while (n != null) {
            if (e.equals(n.element)) return cont;
            n = n.seguent;
            cont++;
        }

        throw new ElementNoTrobat();

    }



}
