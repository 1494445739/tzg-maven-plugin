package com.tzg.plugin.dependency.support;

import com.tzg.plugin.module.support.ModuleSupport;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DubboSupport {

    public final static int DUBBO_COMMENT_LENGTH = 9;

    public static Map< String, String > getDubboMap() {

        Map< String, String > map = new HashMap<>();
        map.put( "dubbo.application.provider", "foo-provider" );
        map.put( "dubbo.application.consumer", "foo-consumer" );
        map.put( "dubbo.registry.address", "zookeeper://localhost:2181" );
        map.put( "dubbo.protocol.name", "dubbo" );
        map.put( "dubbo.protocol.port", "20880" );

        return map;

    }

    public static String getDubboDeclaration() {

        StringBuilder sb = new StringBuilder();
        sb.append( "\n## ---------------------------------------------------------------------------------------- ##" );
        sb.append( "\n##                                                                                          ##" );
        sb.append( "\n## Dubbo Config:                                                                            ##" );
        sb.append( "\n##                                                                                          ##" );
        sb.append( "\n## dubbo.application.provider : 提供者应用名称                                                ##" );
        sb.append( "\n## dubbo.application.consumer : 消费者应用名称                                                ##" );
        sb.append( "\n##                                                                                          ##" );
        sb.append( "\n## ---------------------------------------------------------------------------------------- ##" );
        sb.append( "\n" );
        return sb.toString();
    }

    public static void genDubboModule() throws IOException {

        String[] templates = getTemplates();
        String[] files     = getFiles();

        for ( int i = 0; i < files.length; i++ ) {
            DependencySupport.mergeTemplate( files[ i ], templates[ i ] );
        }

    }

    public static String[] getTemplates() {
        String vmPath = "dependency/dubbo/";

        return new String[]{
                vmPath + "service.vm",
                vmPath + "serviceImpl.vm",
                vmPath + "consumer.vm",
                vmPath + "provider-dubbo.vm",
                vmPath + "consumer-dubbo.vm"
        };
    }

    public static String[] getFiles() {
        String serviceFile     = "FooService.java";
        String serviceImplFile = "FooServiceImpl.java";
        String consumerFile    = "Consumer.java";
        String providerXmlFile = "dubbo-foo-provider.xml";
        String consumerXmlFile = "x-dubbo-foo-consumer.xml";

        String javaDir   = getDubboModulePath();
        String springDir = getDubboSpringPath();

        return new String[]{
                javaDir + "/provider/" + serviceFile,
                javaDir + "/provider/" + serviceImplFile,
                javaDir + "/consumer/" + consumerFile,
                springDir + providerXmlFile,
                springDir + consumerXmlFile
        };
    }

    public static String getDubboModulePath() {
        return DependencySupport.getRootPath() + "/src/main/java/com/tzg/web/project/dubbo/".replaceAll( "project", ModuleSupport.getCurrentProjectName() );
    }

    public static String getDubboSpringPath() {
        return DependencySupport.getRootPath() + "/src/main/resources/spring/";
    }

    public static void removeXml() throws IOException {

        String providerXml = getDubboSpringPath() + "dubbo-foo-provider.xml";
        String consumerXml = getDubboSpringPath() + "x-dubbo-foo-consumer.xml";

        List< String > list = new ArrayList<>();
        list.add( providerXml );
        list.add( consumerXml );

        for ( String fileName : list ) {
            File file = new File( fileName );
            if ( file.exists() ) {
                FileUtils.forceDelete( file );
            }
        }

        File file = new File( getDubboSpringPath() );
        if ( file.exists() && file.isDirectory() && file.listFiles().length == 0 ) {
            FileUtils.forceDelete( file );
        }

    }

}
