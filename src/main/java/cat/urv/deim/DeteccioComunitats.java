package cat.urv.deim;

import java.io.File;
import java.text.DecimalFormat;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import cat.urv.deim.exceptions.ElementNoTrobat;
import cat.urv.deim.exceptions.VertexNoTrobat;

public class DeteccioComunitats {

    public Graf<Integer, Integer, Integer> graf;
    private Graf<Integer, Comunitat, Integer> comunitats;
    private int mida;
    private String fitxer;

    public DeteccioComunitats(int mida, String fitxer) {
        this.mida = mida;
        this.graf = new Graf<>(mida);
        this.fitxer = fitxer;
        this.comunitats = new Graf<>(mida);

        try {
            llegirGraphML(fitxer);
            comunitatsInicials();
            aplicarLouvain();
            exportGraphML();
        } catch (VertexNoTrobat | ElementNoTrobat e) {
            e.printStackTrace();
        }
    }

    private void llegirGraphML(String fitxer) {
        try {
            File inputFile = new File(fitxer);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            NodeList llistaNodes = doc.getElementsByTagName("node");
            for (int i = 0; i < llistaNodes.getLength(); i++) {
                Node nouNode = llistaNodes.item(i);
                int nodeId = Integer.parseInt(nouNode.getAttributes().getNamedItem("id").getNodeValue());
                this.graf.inserirVertex(nodeId, i);
            }

            NodeList llistaArestes = doc.getElementsByTagName("edge");
            for (int i = 0; i < llistaArestes.getLength(); i++) {
                Node edge = llistaArestes.item(i);
                int source = Integer.parseInt(edge.getAttributes().getNamedItem("source").getNodeValue());
                int target = Integer.parseInt(edge.getAttributes().getNamedItem("target").getNodeValue());
                this.graf.inserirAresta(source, target);
            }
        } catch (Exception e) {
            System.out.println("Error leyendo " + fitxer);
            e.printStackTrace();
        }
    }

    public void exportGraphML() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;

        try {
            builder = factory.newDocumentBuilder();
            Document graphExport = builder.newDocument();

            Element etiquetaRoot = graphExport.createElement("graphml");

            Element keyElement = graphExport.createElement("key");
            keyElement.setAttribute("id", "d0");
            keyElement.setAttribute("for", "node");
            keyElement.setAttribute("attr.name", "community");
            keyElement.setAttribute("attr.type", "string");
            etiquetaRoot.appendChild(keyElement);

            Element etiquetaGraph = graphExport.createElement("graph");
            etiquetaGraph.setAttribute("id", "G");
            etiquetaGraph.setAttribute("edgedefault", "undirected");
            etiquetaGraph.setAttribute("modularity", String.valueOf(sumaModularidades()));

            LlistaNoOrdenada<Integer> nodes = (LlistaNoOrdenada<Integer>) this.graf.obtenirVertexIDs();
            for (Integer id : nodes) {
                Element node = graphExport.createElement("node");
                node.setAttribute("id", "n" + id);

                Element data = graphExport.createElement("data");
                data.setAttribute("key", "d0");
                data.setTextContent(String.valueOf(getComunitatNode(id)));
                node.appendChild(data);

                etiquetaGraph.appendChild(node);
            }

            Graf<Integer, Integer, Integer> grafTemp = new Graf<>(mida);
            int i = 0;
            for (Integer id : nodes) {
                for (Integer veiID : this.graf.obtenirVeins(id)) {
                    grafTemp.inserirVertex(id, 0);
                    grafTemp.inserirVertex(veiID, 0);
                    if (!grafTemp.existeixAresta(id, veiID)) {
                        i++;
                        Element aresta = graphExport.createElement("edge");
                        aresta.setAttribute("id", String.valueOf(i));
                        aresta.setAttribute("source", "n" + id);
                        aresta.setAttribute("target", "n" + veiID);
                        etiquetaGraph.appendChild(aresta);
                    }
                    grafTemp.inserirAresta(id, veiID);
                }
            }

            etiquetaRoot.appendChild(etiquetaGraph);
            graphExport.appendChild(etiquetaRoot);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(graphExport);

            String mod = new DecimalFormat("#.###").format(this.sumaModularidades()).replace(".", "_");
            StreamResult resultat = new StreamResult("./" + this.fitxer.split("\\.")[0] + "-(" + mod + ").graphml");

            transformer.transform(source, resultat);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Sumar les modularitats de totes les comunitats
     */
    public double sumaModularidades() throws VertexNoTrobat {
        double suma = 0.0;
        for (Integer id : this.comunitats.obtenirVertexIDs()) {
            Comunitat comunitat = this.comunitats.consultarVertex(id);
            suma += comunitat.getModularitat();
        }
        return suma;
    }

    /*
     * Inicialització de les comunitats per l'algorisme de Louvain
     * crear una comunitat per cada node del graf i enllaçar-les
     */
    private void comunitatsInicials() throws ElementNoTrobat, VertexNoTrobat {

        // crear comunitats
        for (int node : graf.obtenirVertexIDs()) {
            int comunitatID = graf.consultarVertex(node);
            Comunitat comunitat = new Comunitat(mida, comunitatID);
            comunitat.afegirVertexComunitat(node, comunitatID, graf.obtenirVeins(node));
            comunitats.inserirVertex(comunitatID, comunitat);
        }

        // enllaçar comunitats
        for (int node : graf.obtenirVertexIDs()) {
            int comunitatNode = graf.consultarVertex(node);

            for (int vei : graf.obtenirVeins(node)) {
                int comunitatVei = graf.consultarVertex(vei);
                comunitats.inserirAresta(comunitatNode, comunitatVei);
            }
        }

    }

    /*
     * Obtindre la comunitat d'un node del graf
     */
    private Integer getComunitatNode(int id) {
        try {
            return this.graf.consultarVertex(id);
        } catch (Exception e) {
            return 0;
        }
    }

    /*
     * Algorisme de Louvain; aplicar fins que no es pugui millorar la modularitat
     */
    private void aplicarLouvain() throws VertexNoTrobat, ElementNoTrobat {

        boolean millora;
        do {
            millora = faseLouvain();

        } while (millora);
    }

    /*
     * Fase de Louvain
     * Iterar sobre totes les comunitats i mirar si es pot millorar la modularitat
     * al junta-la amb una altra comunitat
     */
    private boolean faseLouvain() throws VertexNoTrobat, ElementNoTrobat {

        LlistaNoOrdenada<Integer> comunitatsID = (LlistaNoOrdenada<Integer>) comunitats.obtenirVertexIDs();
        for (int comunitatID : comunitatsID) {

            Comunitat comunitatActual = comunitats.consultarVertex(comunitatID);
            int millorComunitatID = comunitatID;
            double millorModularitat = comunitatActual.getModularitat();

            for (Integer veiComunitatID : comunitats.obtenirVeins(comunitatID)) {

                if (veiComunitatID != comunitatID && comunitats.buscarVertex(veiComunitatID) && comunitats.buscarVertex(comunitatID) ) {
                double incrementModularitat = calcularIncrementModularitat(comunitatID, veiComunitatID);

                    // Calcular nueva modularidad al añadir a vecino y actualizar
                    double novaModularitat = millorModularitat + incrementModularitat;
                    if (novaModularitat > millorModularitat) {
                        millorModularitat = novaModularitat;
                        millorComunitatID = veiComunitatID;
                    }
                }
            }

            if (millorComunitatID != comunitatID && comunitats.buscarVertex(millorComunitatID) && comunitats.buscarVertex(comunitatID)) {
                juntarComunitats(comunitatID, millorComunitatID);
                return true;
            }
        }
        return false;
    }


    /**
     * Calcula el increment en la modularidat al fusionar dos comunitats
     *
     * @param comunitat1 Identificador de la primera comunidad.
     * @param comunitat2 Identificador de la segunda comunidad.
     * @return El incremento en la modularidad al fusionar las dos comunidades.
     */
    private double calcularIncrementModularitat(int comunitat1, int comunitat2) throws VertexNoTrobat, ElementNoTrobat {

        Comunitat comunitatActual = comunitats.consultarVertex(comunitat1);
        Comunitat novaComunitat = comunitats.consultarVertex(comunitat2);

        // Número d'enlaços d'una comunitat amb l'altre
        double I_AB = (double) 0;
        for (int node : comunitatActual.obtenirVertex()) {
            for (int vei : graf.obtenirVeins(node)) {
                if (novaComunitat.buscarVertex(vei)) {
                    I_AB++;
                }
            }
        }

        double L = (double) graf.numArestes(); // total enllaços graf
        double K_A = (double) comunitatActual.getKc() ; //enllaços de la comunitat A
        double K_B = (double) novaComunitat.getKc();  // enllaços de la comunitat B

        double deltaQ = (I_AB / L) - (K_A * K_B) / (2.0 * L * L);

        return deltaQ;
    }

    private void juntarComunitats(int comunitat1, int comunitat2) throws ElementNoTrobat, VertexNoTrobat {

        Comunitat comunitatActual = comunitats.consultarVertex(comunitat1);
        Comunitat novaComunitat = comunitats.consultarVertex(comunitat2);

        // afegir nodes a la nova comunitat
        for (int node : comunitatActual.obtenirVertex()) {
            novaComunitat.afegirVertexComunitat(node, comunitat2, graf.obtenirVeins(node));
        }
        // copiar arestes
        for (int vei : comunitats.obtenirVeins(comunitat1)) {
            if (vei != comunitat2 && comunitats.buscarVertex(vei) && comunitats.buscarVertex(comunitat2)) {
                comunitats.inserirAresta(comunitat2, vei);
            }
        }

        novaComunitat.calcularModularitat(graf.numArestes());

        // esborrar comunitat actual
        try {
            comunitats.esborrarVertex(comunitat1);
        } catch (Exception e) {
        }

        actualizarGraf();

    }

    /*
     * Actualitzar el graf amb les noves comunitats
     */
    private void actualizarGraf() throws VertexNoTrobat {

        for (Integer comunitatID : comunitats.obtenirVertexIDs()) {
            Comunitat comunitat = comunitats.consultarVertex(comunitatID);

            for (Integer node : comunitat.obtenirVertex()) {
                try {
                    graf.setValue(node, comunitatID);
                } catch (VertexNoTrobat e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
