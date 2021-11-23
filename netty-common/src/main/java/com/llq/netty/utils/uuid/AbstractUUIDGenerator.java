package com.llq.netty.utils.uuid;

// https://github.com/hibernate/hibernate-orm/blob/master/hibernate-core/src/main/java/org/hibernate/id/AbstractUUIDGenerator.java

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

/**
 * The base class for identifier generators that use a UUID algorithm. This
 * class implements the algorithm, subclasses define the identifier
 * format.
 *
 * @see UUIDHexGenerator
 * @author Gavin King
 */
public abstract class AbstractUUIDGenerator {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractUUIDGenerator.class);

    private static final int IP;
    static {
        int ipadd;
        try {
            ipadd = BytesHelper.toInt( InetAddress.getLocalHost().getAddress() );
        }
        catch (Exception e) {
            ipadd = 0;
        }
        IP = ipadd;
    }

    private static short counter = (short) 0;
    private static final int JVM = (int) ( System.currentTimeMillis() >>> 8 );

    public  AbstractUUIDGenerator() {
    }

    /**
     * Unique across JVMs on this machine (unless they load this class
     * in the same quater second - very unlikely)
     */
    protected static int getJVM() {
        return JVM;
    }

    /**
     * Unique in a millisecond for this JVM instance (unless there
     * are > Short.MAX_VALUE instances created in a millisecond)
     */
    protected static short getCount() {
        synchronized(AbstractUUIDGenerator.class) {
            if ( counter < 0 ) {
                counter=0;
            }
            return counter++;
        }
    }

    /**
     * Unique in a local network
     */
    protected static int getIP() {
        return IP;
    }

    /**
     * Unique down to millisecond
     */
    protected static short getHiTime() {
        return (short) ( System.currentTimeMillis() >>> 32 );
    }

    protected static int getLoTime() {
        return (int) System.currentTimeMillis();
    }
}