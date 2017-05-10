package com.tzg.plugin.dependency.support;

import java.util.HashMap;
import java.util.Map;

public final class MongoDBSupport {

    public static Map< String, String > getMongoDBMap() {

        Map< String, String > map = new HashMap<>();
        map.put( "mongoDB.replicaSet", "127.0.0.1" );
        map.put( "mongoDB.connectionsPerHost", "8" );
        map.put( "mongoDB.threadsAllowedToBlockForConnectionMultiplier", "4" );
        map.put( "mongoDB.connectTimeout", "1000" );
        map.put( "mongoDB.maxWaitTime", "1500" );
        map.put( "mongoDB.autoConnectRetry", "true" );
        map.put( "mongoDB.socketKeepAlive", "true" );
        map.put( "mongoDB.socketTimeout", "1500" );
        map.put( "mongoDB.slaveOk", "true" );
        map.put( "mongoDB.writeNumber", "1" );
        map.put( "mongoDB.writeTimeout", "0" );
        map.put( "mongoDB.writeFsync", "true" );
        map.put( "mongoDB.dbName", "test" );

        return map;
    }

    public static String getMongoDBDeclaration() {

        StringBuilder sb = new StringBuilder();
        sb.append( "\n## ---------------------------------------------------------------------------------------- ##" );
        sb.append( "\n##                                                                                          ##" );
        sb.append( "\n## MongoDB config                                                                           ##" );
        sb.append( "\n##                                                                                          ##" );
        sb.append( "\n## ---------------------------------------------------------------------------------------- ##" );

        return sb.toString();
    }

}
