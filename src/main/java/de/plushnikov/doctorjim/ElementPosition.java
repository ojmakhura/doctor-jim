package de.plushnikov.doctorjim;

import de.plushnikov.doctorjim.javaparser.Token;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Plushnikov Michail
 */
public final class ElementPosition implements Comparable<ElementPosition> {
    private String mValue;

    private int mStartColumn;
    private int mStartLine;
    private int mEndColumn;
    private int mEndLine;

    public ElementPosition(String pWert, Token pStart, Token pEnd) {
        mValue = pWert;
        mStartLine = pStart.beginLine;
        mStartColumn = pStart.beginColumn;

        mEndLine = pEnd.endLine;
        mEndColumn = pEnd.endColumn;
    }

    public String getValue() {
        return mValue;
    }

    public int getStartColumn() {
        return mStartColumn;
    }

    public int getStartLine() {
        return mStartLine;
    }

    public int getEndColumn() {
        return mEndColumn;
    }

    public int getEndLine() {
        return mEndLine;
    }

    /**
     * Compares Position of two JavaObjects
     *
     * @param pOther another ElementPosition
     * @return
     */
    public int compareTo(ElementPosition pOther) {
        if (mEndLine == pOther.mEndLine) {
            if (mEndColumn == pOther.mEndColumn) {
                return 0;
            } else {
                if (mEndColumn > pOther.mEndColumn) {
                    return 1;
                } else {
                    return -1;
                }
            }
        } else {
            if (mEndLine > pOther.mEndLine) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}
