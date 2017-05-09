package com.tzg.plugin.dependency.support;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public final class MongoDBSupport {

    public static void execute() throws IOException {

        // 在.properties文件中写入mongodb的配置
        Map< String, String > map = DependencySupport.getMongoDBMap();

        String envProp = System.getProperty( "env" );
        envProp = org.springframework.util.StringUtils.isEmpty( envProp ) ? "dev" : envProp;

        Properties src      = new Properties();
        String     propFile = DependencySupport.getRootPath() + "/src/main/resources/properties/env_$env.properties".replace( "$env", envProp );
        src.load( new FileInputStream( propFile ) );

        // 从.properties文件中读取key，并放入到Set中
        Set< String >    set         = new HashSet<>();
        Enumeration< ? > enumeration = src.propertyNames();
        while ( enumeration.hasMoreElements() ) {
            set.add( ( String ) enumeration.nextElement() );
        }

        Properties tar = new Properties();
        for ( String key : map.keySet() ) {
            boolean isExist = false;
            for ( String k : set ) {
                if ( key.equalsIgnoreCase( k ) ) {
                    isExist = true;
                    break;
                }
            }

            if ( !isExist ) {
                tar.setProperty( key, map.get( key ) );
            }
        }

        if ( tar.size() != 0 ) {

            FileWriter writer = new FileWriter( propFile, true );

            StringBuilder sb = new StringBuilder();
            sb.append( "\n## ---------------------------------------------------------------------------------------- ##" );
            sb.append( "\n##                                                                                          ##" );
            sb.append( "\n## MongoDB config                                                                           ##" );
            sb.append( "\n##                                                                                          ##" );
            sb.append( "\n## ---------------------------------------------------------------------------------------- ##" );
            tar.store( writer, sb.toString() );

            writer.close();

            System.out.println( " Init MongoDB component. finish append properties file. the file path is: " + propFile );

        } else {
            System.out.println( " MongoDB config already exist in .properties file. " );
        }

    }
}
