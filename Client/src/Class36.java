/* Class36 - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */

public class Class36
{
    public static RSString aRSString_639 = Class112.method1668(43, ")1p");
    public static boolean aBoolean640 = false;
    public static int anInt641;
    public static int anInt642;
    public static int anInt643;
    public static long aLong644 = 0L;
    public static int anInt645;
    
    public static synchronized long method438(int i) {
	if (i != 17161)
	    return -108L;
	anInt645++;
	long l = System.currentTimeMillis();
	if (l < Class61.aLong1143)
	    Class21.aLong476 += -l + Class61.aLong1143;
	Class61.aLong1143 = l;
	return l + Class21.aLong476;
    }
    
    public static void method439(byte i) {
	if (i < 8)
	    aLong644 = -105L;
	aRSString_639 = null;
    }
    
    static {
	anInt642 = 0;
    }
}
