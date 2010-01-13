package de.plushnikov.doctorjim;

import de.plushnikov.doctorjim.javaparser.JavaParser;
import de.plushnikov.doctorjim.javaparser.ParseException;
import org.apache.commons.lang.StringUtils;

import java.io.StringReader;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: plushnim
 * Date: 03.08.2009
 * Time: 17:00:34
 * To change this template use File | Settings | File Templates.
 */
public class ImportProcessor {
    private static final String NEW__LINE = "\r\n";
    private static final String DEFAULT_PACKAGE = "";
    private static final String JAVA_LANG_PACKAGE = "java.lang";

    /**
     * Import parser behaivor for .* imports
     */
    private boolean mStrict;
    private static final String STAR_IMPORT = ".*";

    private void initializeJavaLang(Collection<String> pCache) {
        // add default java lang import
        pCache.add("java.lang.*");
        pCache.add("java.lang.Object");
        pCache.add("java.lang.Short");
        pCache.add("java.lang.Integer");
        pCache.add("java.lang.Long");
        pCache.add("java.lang.Float");
        pCache.add("java.lang.Double");
        pCache.add("java.lang.String");
        pCache.add("java.lang.System");
        pCache.add("java.lang.Character");
        pCache.add("java.lang.Boolean");
        pCache.add("java.lang.Byte");
        pCache.add("java.lang.Number");
        pCache.add("java.lang.Exeption");
        pCache.add("java.lang.StringBuilder");
        pCache.add("java.lang.StringBuffer");
    }

    /**
     * Default constructor
     */
    public ImportProcessor() {
        mStrict = true;
    }

    /**
     * @return
     */
    public boolean isStrict() {
        return mStrict;
    }

    /**
     * @param pStrict
     */
    public void setStrict(boolean pStrict) {
        mStrict = pStrict;
    }

    /**
     * @param pInput
     * @return
     * @throws ParseException
     */
    public String organizeImports(String pInput) throws ParseException {
        //pInput = StringUtils.stripToEmpty(pInput);
        // create Parser and initialize with input string
        JavaParser lParser = new JavaParser(new StringReader(pInput));
        lParser.setTabSize(1);
        // parse the string and collect all informations
        lParser.CompilationUnit();

        // Main package
        final ElementPosition lPackage = lParser.getPackage();
        final String lMainPackage = null == lPackage ? "" : lPackage.getValue();

        // All Imports, which are already defined
        final Collection<ElementPosition> lImports = lParser.getImports();
        final Collection<String> lOriginalImports = new HashSet<String>(lImports.size());
        // Collect imports
        final Collection<String> lStarImports = new HashSet<String>(lImports.size());
        for (ElementPosition lImport : lImports) {
            final String lImportValue = lImport.getValue();
            lOriginalImports.add(lImportValue);
            if (lImportValue.endsWith(STAR_IMPORT)) {
                lStarImports.add(lImportValue);
            }
        }

        // add some of basic java.lang types
        initializeJavaLang(lOriginalImports);

        // if strict and there are star imports, break and return immedeadly
        if (isStrict() && !lStarImports.isEmpty()) {
            return pInput;
        }

        // extract head section of the file (everything before first import or just before package end)
        String lHeadSection = extractHeadSection(pInput, lPackage, lImports);
        // extract import section of the file (everything between beginn of first import and end of last import)
        final String lImportsSection = extractImportsSection(pInput, lImports);

        // check for safe import section
        final boolean lImportsAreSafe = verifyInputSection(lImportsSection);

        // prepare place for all javaparser imports
        final Collection<String> lGeneratedImports = new HashSet<String>();

        // add all local defines Types, because they are already 'imported' 
        final Collection<String> lLocalTypes = lParser.getLocalTypes();
        if (!StringUtils.isBlank(lMainPackage)) {
            for (String localType : lLocalTypes) {
                lGeneratedImports.add(lMainPackage + '.' + localType);
            }
        } else {
            lGeneratedImports.addAll(lLocalTypes);
        }

        String lBody = extractBodySection(pInput, lPackage, lImports);

        final Collection<String> lTypes = lParser.getTypes();
        for (String lType : lTypes) {
            //System.out.println(lType);
            final String[] lParts = lType.split("\\.");
            StringBuilder lImportPart = new StringBuilder();
            for (String lPart : lParts) {
                lImportPart.append(lPart);
                if (lPart.matches("[A-Z].*")) {
                    break;
                }
                lImportPart.append('.');
            }

            final String lImport2Replace = lImportPart.toString();
            if (lImport2Replace.contains(".") && !isConflict(lImport2Replace, lOriginalImports, lGeneratedImports)) {
                lGeneratedImports.add(lImport2Replace);

                final String lImport2ReplaceWith = lImport2Replace.substring(lImport2Replace.lastIndexOf('.') + 1);

                final String lReplaceSource = "([^\\w\\p{L}\\.\"])" + lImport2Replace.replaceAll("\\.", "\\\\s*\\.\\\\s*") + "([^\\p{L}\"])";
                final String lReplaceTarget = "$1" + lImport2ReplaceWith + "$2";
                lBody = lBody.replaceAll(lReplaceSource, lReplaceTarget);
                lHeadSection = lHeadSection.replaceAll(lReplaceSource, lReplaceTarget);
            }
        }


        // prepare resultbuffer
        final StringBuilder lBuffer = new StringBuilder(pInput.length());

        // add original head
        if (lHeadSection.length() > 0) {
            lBuffer.append(lHeadSection);
            lBuffer.append(NEW__LINE);
            lBuffer.append(NEW__LINE);
        }

        final Set<String> lAllImports = new TreeSet<String>();
        lAllImports.addAll(lGeneratedImports);

        if (!lImportsAreSafe) {
            if (lImportsSection.length() > 0) {
                lBuffer.append(lImportsSection);
                lBuffer.append(NEW__LINE);
                lBuffer.append(NEW__LINE);
            }
            // remove original imports
            lGeneratedImports.removeAll(lOriginalImports);
        } else {
            // add orignal imports
            lAllImports.addAll(lOriginalImports);
        }

        // append new imports
        final String lGeneratedImportsSection = generateImportSection(lAllImports, lMainPackage, lStarImports);
        if (lGeneratedImportsSection.length() > 0) {
            lBuffer.append(lGeneratedImportsSection);
            lBuffer.append(NEW__LINE);
        }

        // append body of class
        lBuffer.append(lBody);

        return lBuffer.toString();
    }

    protected String extractHeadSection(String pInput, ElementPosition pPackage, Collection<ElementPosition> pImports) {
        ElementPosition lFirstImport = null;
        if (!pImports.isEmpty()) {
            lFirstImport = Collections.min(pImports);
        }
        int lInputPosition = 0;
        if (null != pPackage || null != lFirstImport) {
            final int lColumn = null == lFirstImport ? pPackage.getEndColumn() : lFirstImport.getStartColumn() - 1;
            final int lLine = null == lFirstImport ? pPackage.getEndLine() : lFirstImport.getStartLine();

            lInputPosition = locatePosition(pInput, lLine, lColumn + 1);
        }
        return StringUtils.stripToEmpty(pInput.substring(0, lInputPosition));
    }

    protected String extractImportsSection(String pInput, Collection<ElementPosition> pImports) {
        String result = "";
        if (!pImports.isEmpty()) {
            final ElementPosition lFirstImport = Collections.min(pImports);
            final ElementPosition lLastImport = Collections.max(pImports);

            int lStart = locatePosition(pInput, lFirstImport.getStartLine(), lFirstImport.getStartColumn());
            int lEnd = locatePosition(pInput, lLastImport.getEndLine(), lLastImport.getEndColumn());

            result = StringUtils.stripToEmpty(pInput.substring(lStart, lEnd + 1));
        }
        return result;
    }

    protected String extractBodySection(String pInput, ElementPosition pPackage, Collection<ElementPosition> pImports) {
        // determine last element, after which class body declaration starts
        ElementPosition lClassBodyStartsAfterObject = pPackage;
        if (!pImports.isEmpty()) {
            lClassBodyStartsAfterObject = Collections.max(pImports);
        }
        // claculate start position of class body
        int lClassBodyStartPosition = 0;
        if (null != lClassBodyStartsAfterObject) {
            lClassBodyStartPosition = locatePosition(pInput,
                    lClassBodyStartsAfterObject.getEndLine(), lClassBodyStartsAfterObject.getEndColumn() + 1);
        }

        return StringUtils.stripToEmpty(pInput.substring(lClassBodyStartPosition));
    }

    protected boolean verifyInputSection(String pImportSection) {
        return !pImportSection.contains("//") && !pImportSection.contains("/*") && !pImportSection.contains("*/");
    }

    protected boolean isConflict(String type, Collection<String> importList,
                                 Collection<String> replacedSet) {
        return !JAVA_LANG_PACKAGE.equals(extractPackage(type)) &&
                (isConflict(type, replacedSet) || isConflict(type, importList));
    }

    protected boolean isConflict(String pType, Collection<String> pTestSet) {
        if (pTestSet.contains(pType)) {
            return false;
        }

        for (String importType : pTestSet) {
            if (!importType.endsWith(STAR_IMPORT)) {
                if (!pType.equals(importType)) {
                    String lClassName = importType;
                    final int lPosition = importType.lastIndexOf('.');
                    if (lPosition > 0) {
                        lClassName = importType.substring(lPosition);
                    }
                    if (pType.endsWith(lClassName) && !importType.startsWith("static ")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected String generateImportSection(Set<String> pAllImports, String pMainPackage, Collection<String> pStarImports) {
        StringBuilder lBuffer = new StringBuilder();

        for (String lImport : pAllImports) {
            String lImportPackage = extractPackage(lImport);
            // make sure the import is not redundant, because :
            //  - it is java.lang import (automatically imported)
            //  - it is part of the current package
            //  - there is * import from the same package already
            if (!JAVA_LANG_PACKAGE.equals(lImportPackage) &&
                    !pMainPackage.equals(lImportPackage) &&
                    (lImport.endsWith(STAR_IMPORT) || !pStarImports.contains(lImportPackage + STAR_IMPORT))) {
                lBuffer.append("import ").append(lImport).append(';').append(NEW__LINE);
            }
        }

        return lBuffer.toString();
    }


    /**
     * @param pInput
     * @param pLine
     * @param pColumn
     * @return
     */
    protected int locatePosition(String pInput, int pLine, int pColumn) {
        int result = pColumn;
        if (pLine > 0) {
            result += StringUtils.ordinalIndexOf(pInput, "\n", pLine - 1);
        }
        return result;
    }

    protected String extractPackage(String pImportType) {
        final int index = pImportType.lastIndexOf('.');
        String typePackage = "";
        if (-1 < index) {
            typePackage = pImportType.substring(0, index);
        }
        return typePackage;
    }
}
