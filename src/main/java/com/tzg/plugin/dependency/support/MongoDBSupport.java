package com.tzg.plugin.dependency.support;

import com.tzg.plugin.module.support.ModuleSupport;
import org.apache.commons.io.FileUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class MongoDBSupport {

    public static int MONGODB_COMMENT_LENGTH = 12;
    public static int MONGODB_KEY_INTERVAL   = 53;

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
        sb.append( "\n## mongoDB config:                                                                          ##" );
        sb.append( "\n##                                                                                          ##" );
        sb.append( "\n## connections-per-host: 每个主机答应的连接数(每个主机的连接池大小), 当连接池被用光时, 会被阻塞住   ##" );
        sb.append( "\n## max-wait-time:        被阻塞线程从连接池获取连接的最长等待时间(ms)                            ##" );
        sb.append( "\n## connect-timeout:      在建立(打开)套接字连接时的超时时间(ms)                                 ##" );
        sb.append( "\n## socket-timeout:       套接字超时时间; 该值会被传递给Socket.setSoTimeout(int)                ##" );
        sb.append( "\n## slave-ok:             指明是否答应驱动从次要节点或者slave节点读取数据                         ##" );
        sb.append( "\n##                                                                                          ##" );
        sb.append( "\n## ---------------------------------------------------------------------------------------- ##" );
        sb.append( "\n" );
        return sb.toString();
    }

    public static void genMongoDBModule() throws IOException {

        String[] templates = getTemplates();
        String[] files     = getFiles();

        for ( int i = 0; i < files.length; i++ ) {
            mergeTemplate( files[ i ], templates[ i ] );
        }

    }

    public static void removeMongoDBModule() throws IOException {
        File file = new File( getMongoDBModulePath() );
        if ( file.exists() ) {
            FileUtils.forceDelete( file );
        }
        System.out.println( "====> delete mongodb module '$path' successfully.".replace( "$path", file.getAbsolutePath() ) );
    }

    public static String[] getTemplates() {
        String vmJavaDir = "dependency/mongodb/";

        return new String[]{
                vmJavaDir + "model.vm",
                vmJavaDir + "controller.vm"
        };
    }

    public static String getMongoDBModulePath() {
        return DependencySupport.getRootPath() + "/src/main/java/com/tzg/web/project/mongodb/".replaceAll( "project", ModuleSupport.getCurrentProjectName() );
    }

    public static String[] getFiles() {
        String modelFile       = "Foo.java";
        String ctrlFile        = "FooController.java";

        String javaDir = getMongoDBModulePath();

        return new String[]{
                javaDir + modelFile,
                javaDir + ctrlFile
        };
    }

    public static void mergeTemplate( String filePath, String tplPath ) throws IOException {

        File file = new File( filePath );
        FileUtils.touch( file );

        Properties properties = new Properties();
        properties.setProperty( "resource.loader", "class" );
        properties.setProperty( "class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader" );

        VelocityEngine  engine  = new VelocityEngine( properties );
        VelocityContext context = new VelocityContext();
        context.put( "project", ModuleSupport.getCurrentProjectName() );

        Writer writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( file ), "UTF-8" ) );
        engine.mergeTemplate( tplPath, "UTF-8", context, writer );

        writer.flush();
        writer.close();

    }

}
