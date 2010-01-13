package de.plushnikov.doctorjim.javaparser;

import de.plushnikov.doctorjim.ElementPosition;

import java.util.Collection;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Plushnikov Michail
 */
public abstract class AbstractJavaParser {
    /**
     * Main package of the class
     */
    private ElementPosition mPackage;
    /**
     * All imports from the file
     */
    private Collection<ElementPosition> mImports = new HashSet<ElementPosition>();
    /**
     * All local defined types
     */
    private Collection<String> mLocalTypes = new HashSet<String>();
    /**
     * All type elements which should be processed
     */
    private Collection<String> mTypes = new TreeSet<String>();

    ///////////////////////////////////////////////////////////////////////////

    /**
     * Sets informations about the package declaration
     *
     * @param pPackage package name
     * @param pStart   start position Token
     * @param pEnd     end position Token
     */
    public void setPackage(String pPackage, Token pStart, Token pEnd) {
        mPackage = new ElementPosition(pPackage, pStart, pEnd);
    }

    /**
     * Adds new import entry
     *
     * @param pImport    name of the import
     * @param pStatic    true if this is an static import
     * @param pImportAll true if this is a star import
     * @param pStart     start position Token
     * @param pEnd       end position Token
     */
    protected void addImport(String pImport, boolean pStatic, boolean pImportAll, Token pStart, Token pEnd) {
        StringBuilder lImport = new StringBuilder(50);
        if (pStatic) {
            lImport.append("static ");
        }
        lImport.append(pImport);

        if (pImportAll) {
            lImport.append(".*");
        }

        mImports.add(new ElementPosition(lImport.toString(), pStart, pEnd));
    }

    /**
     * TODO docu
     *
     * @param pType
     */
    protected void addType(String pType) {
        mTypes.add(pType);
    }

    protected void addAnnotation(Token pToken) {
        addType(pToken.image);
    }

    protected void addThrows(Token pToken) {
        addType(pToken.image);
    }

    protected void addInternType(Token pToken) {
        final String lNameValue = pToken.image;
        final String[] lParts = lNameValue.split("\\.");
        if (lParts.length > 1) {
            StringBuilder lTypeValue = new StringBuilder(lNameValue.length());
            boolean lFound = false;
            for (String lPart : lParts) {
                lTypeValue.append(lPart);
                // uses java conventions to revise type
                if (lPart.matches("[A-Z].*")) {
                    lFound = true;
                    break;
                }
                lTypeValue.append('.');
            }

            if (lFound && lTypeValue.indexOf(".") > -1) {
                addType(lTypeValue.toString());
            }
        }
    }

    protected void addClassOrInterfaceName(Token pToken) {
        mLocalTypes.add(pToken.image);
    }

    protected void addEnumName(Token pToken) {
        mLocalTypes.add(pToken.image);
    }

    protected void addAnnotationName(Token pToken) {
        mLocalTypes.add(pToken.image);
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * Getter for the main package
     *
     * @return information about the main package
     */
    public ElementPosition getPackage() {
        return mPackage;
    }

    /**
     * Getter for all imports
     *
     * @return information about the imports
     */
    public Collection<ElementPosition> getImports() {
        return mImports;
    }

    /**
     * Getter for all local defined types
     *
     * @return collection of local defined types
     */
    public Collection<String> getLocalTypes() {
        return mLocalTypes;
    }

    /**
     * Getter for all Types
     *
     * @return information about all Types
     */
    public Collection<String> getTypes() {
        return mTypes;
    }
}
