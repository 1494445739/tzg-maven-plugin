package com.tzg.plugin.dependency.support;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BatchSupport {

    public static void genBatchConfig() throws IOException {

        String[] templates = getTemplates();
        String[] files     = getFiles();

        for ( int i = 0; i < files.length; i++ ) {
            DependencySupport.mergeTemplate( files[ i ], templates[ i ] );
        }

    }

    public static String[] getTemplates() {
        String batchVmPath = "dependency/batch/spring-batch-task.vm";
        String jobVmPath   = "dependency/batch/job.vm";
        return new String[]{
                batchVmPath,
                jobVmPath
        };
    }

    public static String[] getFiles() {
        String batchXmlPath = DependencySupport.getRootPath() + "/src/main/resources/spring/spring-batch-task.xml";
        String jobXmlPath   = DependencySupport.getRootPath() + "/src/main/resources/spring/job/job-foo.xml";

        return new String[]{
                batchXmlPath,
                jobXmlPath
        };
    }

    public static void removeXml() throws IOException {

        String batchXmlPath = DependencySupport.getRootPath() + "/src/main/resources/spring/spring-batch-task.xml";
        String jobXmlPath   = DependencySupport.getRootPath() + "/src/main/resources/spring/job/job-foo.xml";

        List< String > list = new ArrayList<>();
        list.add( batchXmlPath );
        list.add( jobXmlPath );

        for ( String fileName : list ) {
            File file = new File( fileName );
            if ( file.exists() ) {
                FileUtils.forceDelete( file );
            }
        }

        File springDir = new File( DependencySupport.getRootPath() + "/src/main/resources/spring" );
        File jobDir    = new File( DependencySupport.getRootPath() + "/src/main/resources/spring/job" );

        List< File > files = new ArrayList<>();
        files.add( jobDir );
        files.add( springDir );

        for ( File dir : files ) {
            if ( dir.exists() && dir.isDirectory() && dir.listFiles().length == 0 ) {
                FileUtils.forceDelete( dir );
            }
        }

    }

}
