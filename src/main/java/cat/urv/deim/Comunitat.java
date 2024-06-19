package cat.urv.deim;

import cat.urv.deim.exceptions.ArestaNoTrobada;
import cat.urv.deim.exceptions.ElementNoTrobat;
import cat.urv.deim.exceptions.VertexNoTrobat;

public class Comunitat implements Comparable<Comunitat> {

    private HashMapIndirecte<Integer, Integer> vertex;
    private int id;
    private int numNodes;
    private int Kc; // suma dels graus dels nodes de la comunitat
    private int Lc; // Arestes que interconecten al comunitat
    private double modularitat;  //qualitat de la comunitat

    public Comunitat(int mida, int id) {
        this.vertex = new HashMapIndirecte<>(mida);
        this.id = id;
        this.Lc = 0;
        this.Kc = 0;
        this.numNodes = 0;
        this.modularitat = 0;
    }

    /*
    * Mc (Modularitat comunitat) = (Lc/L) - (Kc/2L)^2
    *
    * Lc -> numero enlaços que interconecten comunitat
    * L  -> numero enllaços xarxa
    * Kc -> suma dels graus dels nodes de la comunitat
    *
    * La modularitat indica la qualitat de una comunitat al comparar la densitat
    * que tindria la xarxa si es tractes d'una xarxa aleatoria
    *
    * Mc = 0 -> la xarxa en si mateixa es una comunitat
    * Mc < 0 -> cada node de la xarxa es una comunitat
    * Mc > 0 -> es una bona particio de la xarxa
    */
    public void calcularModularitat(int L) {
        this.modularitat = ((double)Lc / (double)L) - Math.pow((double)Kc / (2 * (double)L), 2);
    }

    public LlistaGenerica<Integer> obtenirVertex() {
        return vertex.obtenirClaus();

    }

    public boolean buscarVertex(int id) {
        return vertex.buscar(id);
    }

    /**
     * Afegir vertex a la comunitat, actualitzant valors interns
     *
     * @param id vertex
     * @param comunitat id comunitat
     * @param veins Llista de vertex veins
     */
    public void afegirVertexComunitat(int id, int comunitat, LlistaGenerica<Integer> veins) throws ElementNoTrobat {
        if (veins.numElements() > 0) {
            for (int vei : veins) {
                if (vertex.buscar(vei))
                    afegiryLc(1);
            }
        }
        this.vertex.inserir(id, comunitat);
        numNodes++;
        afegirKc(veins.numElements());
    }

    public int getNumNodes() {
        return this.numNodes;
    }

    public int getKc() {
        return this.Kc;
    }

    public int getLc() {
        return this.Lc;
    }

    public void setKc(int K) {
        this.Kc = K;
    }

    public void setLc(int L) {
        this.Lc = L;
    }

    public double getModularitat() {
        return modularitat;
    }

    public void afegirKc(int K) {
        this.Kc += K;
    }

    public void afegiryLc(int L) {
        this.Lc += L;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public int compareTo(Comunitat c) {
        return Double.compare(getModularitat(), c.getModularitat());
    }
}
