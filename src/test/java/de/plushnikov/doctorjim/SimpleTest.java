package de.plushnikov.doctorjim;

import de.plushnikov.doctorjim.javaparser.ParseException;
import org.apache.commons.io.IOUtils;
import org.junit.Before;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Simple testsuit for Importbeautifier tests
 *
 * @author Plushnikov Michail
 */
public class SimpleTest {
    private ImportProcessor mProcessor;

    @Before
    public void setUp() {
        mProcessor = new ImportProcessor();
    }

    @org.junit.Test
    public void testOne() throws IOException, ParseException {
        testBeautifikation("Temp");
    }

    @org.junit.Test
    public void testTwo() throws IOException, ParseException {
        testBeautifikation("Temp2");
    }

    @org.junit.Test
    public void testThree() throws IOException, ParseException {
        testBeautifikation("package-info");
    }

    @org.junit.Test
    public void testSample1() throws Exception {
        testBeautifikation("Sample01");
    }

    @org.junit.Test
    public void testSample2() throws Exception {
        testBeautifikation("Sample02");
    }

    @org.junit.Test
    public void testSample3() throws Exception {
        testBeautifikation("Sample03");
    }

    @org.junit.Test
    public void testSample4() throws Exception {
        testBeautifikation("Sample04");
    }

    @org.junit.Test
    public void testSample5() throws Exception {
        testBeautifikation("Sample05");
    }

    @org.junit.Test
    public void testSample6() throws Exception {
        testBeautifikation("Sample06");
    }

    @org.junit.Test
    public void testSample7() throws Exception {
        testBeautifikation("Sample07");
    }

    @org.junit.Test
    public void testSample8() throws Exception {
        testBeautifikation("Sample08");
    }

    @org.junit.Test
    public void testSample9() throws Exception {
        testBeautifikation("Sample09");
    }

    @org.junit.Test
    public void testSample10() throws Exception {
        testBeautifikation("Sample10");
    }

    @org.junit.Test
    public void testSample11() throws Exception {
        testBeautifikation("Sample11");
    }

    @org.junit.Test
    public void testSample12() throws Exception {
        testBeautifikation("Sample12");
    }

    @org.junit.Test
    public void testSample13() throws Exception {
        testBeautifikation("Sample13");
    }

    @org.junit.Test
    public void testSample14() throws Exception {
        testBeautifikation("Sample14");
    }

    @org.junit.Test
    public void testSample15() throws Exception {
        testBeautifikation("Sample15");
    }

    @org.junit.Test
    public void testSample16() throws Exception {
        testBeautifikation("Sample16");
    }

    @org.junit.Test
    public void testSample17() throws Exception {
        mProcessor.setStrict(false);
        testBeautifikation("Sample17");
    }

    @org.junit.Test
    public void testSample18() throws Exception {
        mProcessor.setStrict(false);
        testBeautifikation("Sample18");
    }

    @org.junit.Test
    public void testSample20() throws Exception {
        testBeautifikation("Sample20");
    }

    @org.junit.Test
    public void testSample21() throws Exception {
        testBeautifikation("Sample21");
    }

    @org.junit.Test
    public void testSample22() throws Exception {
        testBeautifikation("Sample22");
    }

    @org.junit.Test
    public void testSample23() throws Exception {
        testBeautifikation("Sample23");
    }

    @org.junit.Test
    public void testSample24() throws Exception {
        testBeautifikation("Sample24");
    }

    @org.junit.Test
    public void testSample25() throws Exception {
        testBeautifikation("Sample25");
    }

    @org.junit.Test
    public void testSample26() throws Exception {
        testBeautifikation("Sample26");
    }

    @org.junit.Test
    public void testSample27() throws Exception {
        testBeautifikation("Sample27");
    }

    @org.junit.Test
    public void testSample28() throws Exception {
        testBeautifikation("Sample28");
    }

    @org.junit.Test
    public void testSample29() throws Exception {
        testBeautifikation("Sample29");
    }

    @org.junit.Test
    public void testSample30() throws Exception {
        testBeautifikation("Sample30");
    }

    @org.junit.Test
    public void testSample31() throws Exception {
        testBeautifikation("Sample31");
    }

    @org.junit.Test
    public void testSample32() throws Exception {
        testBeautifikation("Sample32");
    }

    @org.junit.Test
    public void testSample33() throws Exception {
        testBeautifikation("Sample33");
    }

    @org.junit.Test
    public void testSample34() throws Exception {
        testBeautifikation("Sample34");
    }

    @org.junit.Test
    public void testSample35() throws Exception {
        testBeautifikation("Sample35");
    }

    @org.junit.Test
    public void testSample36() throws Exception {
        testBeautifikation("Sample36");
    }

    @org.junit.Test
    public void testSample37() throws Exception {
        testBeautifikation("Sample37");
    }


    private void testBeautifikation(String pFilename)
            throws IOException, ParseException {
        String lInput = IOUtils.toString(this.getClass().getResourceAsStream(pFilename + ".java_input"));
        String lExpectedOutput = IOUtils.toString(this.getClass().getResourceAsStream(pFilename + ".java_output"));

        String lOutput = mProcessor.organizeImports(lInput);

        assertEquals(normalized(lExpectedOutput), normalized(lOutput));
    }

    private String normalized(String pString) {
        return pString.trim().replaceAll("\r\n", System.getProperty("line.separator"));
    }
}
