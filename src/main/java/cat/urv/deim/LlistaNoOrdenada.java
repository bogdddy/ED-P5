package cat.urv.deim;

import cat.urv.deim.exceptions.ElementNoTrobat;
import cat.urv.deim.exceptions.PosicioForaRang;

public class LlistaNoOrdenada<E extends Comparable<E>> extends LlistaGenerica<E> {

    private NodeLlista<E> ultim;

    public LlistaNoOrdenada() {
        super();
        ultim = fantasma;

    }

    //Metode per insertar un element a la llista. No importa la posicio on s'afegeix l'element
    public void inserir(E e) {

        NodeLlista<E> nouNode = new NodeLlista<E>(e);

        nouNode.anterior = ultim;
        ultim.seguent = nouNode;
        ultim = ultim.seguent;

        numElem++;

    }

    //Metode per a esborrar un element de la llista
    public void esborrar(E e) throws ElementNoTrobat {

        NodeLlista<E> n = this.fantasma.seguent;

        int posicio = buscar(e);

        for (int i=0; i<posicio; i++)
            n = n.seguent;

        if (n.seguent == null) n.anterior.seguent = null;
        else {
            n.anterior.seguent = n.seguent;
            n.seguent.anterior = n.anterior;
        }

        numElem--;
    }

    //Metode per a consultar un element de la llista per posicio
    //La primera dada esta a la posicio 0
    public E consultar(int pos) throws PosicioForaRang {

        NodeLlista<E> n = fantasma.seguent;

        if (pos < 0 || pos >= numElements())
            throw new PosicioForaRang();

        for (int i=0; i<pos; i++)
            n = n.seguent;

        return n.element;
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
