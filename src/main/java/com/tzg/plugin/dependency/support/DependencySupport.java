package com.tzg.plugin.dependency.support;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DependencySupport {

    private final static String ROOT_PATH = System.getProperty( "user.dir" );

    public static String getRootPath() {
        return ROOT_PATH;
    }

    /**
     * pom.xml的写入
     *
     * @param pomPath pom.xml的文件路径
     * @param doc     dom4j的文档对象
     */
    public static void pomWriter( String pomPath, Document doc ) throws IOException {
        FileOutputStream   fos    = new FileOutputStream( pomPath );
        OutputStreamWriter osw    = new OutputStreamWriter( fos, "utf-8" );
        OutputFormat       format = OutputFormat.createPrettyPrint();
        format.setEncoding( "utf-8" );
        format.setIndent( true );
        format.setIndent( "    " );
        format.setNewLineAfterDeclaration( false ); // 解决生成xml后第二行空行
        XMLWriter writer = new XMLWriter( osw, format );
        writer.write( doc );
        writer.close();
    }

    /**
     * 从pom.xml中寻找指定artifactId的dependency元素
     *
     * @param component    组件的artifactId字符串
     * @param dependencies dependencies dom4j element
     * @return dependency element
     */
    @SuppressWarnings( "unchecked" )
    public static Element getDependencyElement( String component, Element dependencies )
            throws DocumentException {

        List< Element > dependencyElementList = dependencies.elements();
        Element         dependencyElement     = null;
        for ( Element dependency : dependencyElementList ) {
            Element artifactId = dependency.element( "artifactId" );
            String  artifact   = artifactId.getTextTrim();
            if ( artifact.equalsIgnoreCase( component ) ) {
                dependencyElement = dependency;
                break;
            }
        }

        return dependencyElement;
    }

    /**
     * 根据pom.xml的路径返回dependencies元素
     *
     * @param document dom4j的pom.xml的document抽象
     */
    public static Element getDependenciesElement( Document document ) throws DocumentException {
        Element rootElement = document.getRootElement();
        return rootElement.element( "dependencies" );
    }

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

}
