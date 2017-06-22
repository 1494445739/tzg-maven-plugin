package com.tzg.plugin.module.goal;

import com.tzg.plugin.module.support.ModuleSupport;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.components.interactivity.Prompter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @goal rm
 */
public class ModuleRemove extends AbstractMojo {

    private final static String PROMPT = "Enter project module which you want to remove. First character must be capitalized";

    /**
     * @component
     * @required
     */
    private Prompter prompter;

    /**
     * maven插件goal执行的入口方法
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        try {

            String module = System.getProperty( "module" );
            module = StringUtils.isBlank( module ) ? prompter.prompt( PROMPT ) : module;

            // 模块名不能为null
            while ( StringUtils.isBlank( module ) ) {
                module = prompter.prompt( PROMPT );
            }

            String         modulePath      = ModuleSupport.getModulePath();
            List< String > moduleFilesPath = new ArrayList<>();
            moduleFilesPath.add( modulePath + "/bean/" + module + ".java" );
            moduleFilesPath.add( modulePath + "/controller/" + module + "Controller.java" );
            moduleFilesPath.add( modulePath + "/mapper/" + module + "Mapper.java" );
            moduleFilesPath.add( modulePath + "/service/api/" + module + "Service.java" );
            moduleFilesPath.add( modulePath + "/service/impl/" + module + "ServiceImpl.java" );

            for ( String moduleFilePath : moduleFilesPath ) {
                removeFiles( moduleFilePath );   // 删除module
            }

            removeFiles( ModuleSupport.getMapperPath( module ) );   // 删除mapper.xml

            // 如果项目下目录为null，则一并删除空目录
            List< String > moduleDirPath = new ArrayList<>();
            moduleDirPath.add( modulePath + "/bean" );
            moduleDirPath.add( modulePath + "/controller" );
            moduleDirPath.add( modulePath + "/mapper" );
            moduleDirPath.add( modulePath + "/service/api" );
            moduleDirPath.add( modulePath + "/service/impl" );
            moduleDirPath.add( modulePath + "/service" );

            moduleDirPath.add( ModuleSupport.getMapperDirPath() );

            for ( String path : moduleDirPath ) {
                File dir = new File( path );
                if ( dir.list().length == 0 ) {
                    removeFiles( dir.getAbsolutePath() );
                }
            }


        } catch ( Exception e ) {
            e.printStackTrace();
        }

    }

    private void removeFiles( String file ) throws IOException {
        FileUtils.forceDelete( new File( file ) );
        getLog().info( "delete file $file successfully.".replace( "$file", file ) );
    }

}
