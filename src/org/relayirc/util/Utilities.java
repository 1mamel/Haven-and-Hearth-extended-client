//-----------------------------------------------------------------------------
// $RCSfile: Utilities.java,v $
// $Revision: 1.1.2.2 $
// $Author: snoopdave $
// $Date: 2001/03/30 12:35:43 $
//-----------------------------------------------------------------------------


package org.relayirc.util;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Utility functions.
 *
 * @author David M. Johnson.
 */
@SuppressWarnings({"JavaDoc"})
public class Utilities {

    //--------------------------------------------------------------------------

    /**
     * Convert string to integer array.
     */
    public static String[] stringToStringArray(final String instr, final String delim) {
        String[] sa = null;

        try {
            // Tokenize string, build vector of tokens
            final StringTokenizer toker = new StringTokenizer(instr, delim);
            final Vector v = new Vector();
            while (toker.hasMoreTokens()) {
                final String s = toker.nextToken();
                v.addElement(s);
            }

            // Allocate and fill array of ints
            sa = new String[v.size()];
            for (int i = 0; i < v.size(); i++) {
                sa[i] = (String) v.elementAt(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sa;
    }

    //--------------------------------------------------------------------------

    /**
     * Convert string to integer array.
     */
    public static int[] stringToIntArray(final String instr, final String delim)
            throws NoSuchElementException, NumberFormatException {

        final int[] intArray;

        // Tokenize string, build vector of tokens
        final StringTokenizer toker = new StringTokenizer(instr, delim);
        final Vector ints = new Vector();
        while (toker.hasMoreTokens()) {
            final String sInt = toker.nextToken();
            final int nInt = Integer.parseInt(sInt);
            ints.addElement(nInt);
        }

        // Allocate and fill array of ints
        intArray = new int[ints.size()];
        for (int i = 0; i < ints.size(); i++) {
            intArray[i] = (Integer) ints.elementAt(i);
        }
        return intArray;
    }
    //-------------------------------------------------------------------

    /**
     * Convert integer array to a string.
     */
    public static String intArrayToString(final int[] intArray) {
        final StringBuilder ret = new StringBuilder();
        for (final int anIntArray : intArray) {
            if (ret.length() == 0)
                ret.append(',');
            ret.append(Integer.toString(anIntArray));
        }
        return ret.toString();
    }
}


