package com.llq.netty.utils.uuid;

// https://github.com/hibernate/hibernate-orm/blob/master/hibernate-core/src/main/java/org/hibernate/id/UUIDHexGenerator.java

import com.llq.netty.utils.Usual;

public class UUIDHexGenerator extends AbstractUUIDGenerator{

    private static final String sep = "-";

    public static String generate() {
        return format( getIP() ) + sep
                + format( getJVM() ) + sep
                + format( getHiTime() ) + sep
                + format( getLoTime() ) + sep
                + format( getCount() );

//        return
//                format( getJVM() ) + sep
//                + format( getHiTime() ) + sep
//                + format( getLoTime() ) + sep
//                + format( getIP() ) + sep
//                + format( getCount() );
    }

    public static long generateLong() {
        String id = Usual.getString(getHiTime())
                + Usual.getString(getLoTime()).replace(sep, "")
                + Usual.getString(getCount());
        return Usual.getLong(id);
    }

    protected static String format(int intValue) {
        String formatted = Integer.toHexString( intValue );
        StringBuilder buf = new StringBuilder( "00000000" );
        buf.replace( 8 - formatted.length(), 8, formatted );
        return buf.toString();
    }

    protected  static String format(short shortValue) {
        String formatted = Integer.toHexString( shortValue );
        StringBuilder buf = new StringBuilder( "0000" );
        buf.replace( 4 - formatted.length(), 4, formatted );
        return buf.toString();
    }


    public static void main(String[] args) {
        System.out.println(UUIDHexGenerator.generateLong());
    }
}
