package cat.urv.deim;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestComunitats {

    @Test
    public void GraphmlSobre() {
        DeteccioComunitats comunity = new DeteccioComunitats(6, "sobre.graphml");
        assertEquals(5, comunity.graf.numVertex());
        assertEquals(7, comunity.graf.numArestes());
    }

    @Test
    public void GraphmlKarate() {
        DeteccioComunitats comunity = new DeteccioComunitats(50, "karate.graphml");
        assertEquals(34, comunity.graf.numVertex());
        assertEquals(78, comunity.graf.numArestes());
    }

    @Test
    public void GraphmlPersones1() {
        DeteccioComunitats comunity = new DeteccioComunitats(120, "persones_amistats1.graphml");
        assertEquals(100, comunity.graf.numVertex());
        assertEquals(100, comunity.graf.numArestes());
    }

    @Test
    public void GraphmlPersones2() {
        DeteccioComunitats comunity = new DeteccioComunitats(120, "persones_amistats2.graphml");
        assertEquals(100, comunity.graf.numVertex());
        assertEquals(300, comunity.graf.numArestes());
    }

    @Test
    public void GraphmlPersones3() {
        DeteccioComunitats comunity = new DeteccioComunitats(120, "persones_amistats3.graphml");
        assertEquals(100, comunity.graf.numVertex());
        assertEquals(49, comunity.graf.numArestes());
    }

    @Test
    public void GraphmlAerolineas() {
        DeteccioComunitats comunity = new DeteccioComunitats(250, "airlines.graphml");
        assertEquals(235, comunity.graf.numVertex());
        assertEquals(1297, comunity.graf.numArestes());
    }



}

