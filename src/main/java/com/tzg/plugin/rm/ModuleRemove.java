package com.tzg.plugin.rm;

import com.tzg.plugin.support.helper.PluginHelper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;

import java.io.File;
import java.io.IOException;

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

            removeFiles( PluginHelper.getFiles( module ) );

        } catch ( Exception e ) {
            e.printStackTrace();
        }

    }

    private void removeFiles( String[] files ) throws IOException {

        for ( String file : files ) {

            FileUtils.forceDelete( new File( file ) );
            getLog().info( "delete file $file successfully.".replace( "$file", file ) );

        }

    }

}
