package com.tzg.plugin.dependency.goal;

import com.tzg.plugin.dependency.support.DependencySupport;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.IOException;

/**
 * @goal dep-rm
 */
public class DependencyRemove extends AbstractMojo {

    private final static String PROMPT = "Enter dependency's artifactId which you want to remove.";

    /**
     * @component
     * @required
     */
    private Prompter prompter;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        String component = System.getProperty( "component" );

        try {

            // 组件名不能为null，否则一直循环要求输入，直至进程被终止
            while ( StringUtils.isBlank( component ) ) {
                component = prompter.prompt( PROMPT );
            }

            // 读取xml，根据输入的component进行查找，如果找到，则删除相关的组件，并写入pom.xml文件
            String pomPath = DependencySupport.getRootPath() + "/pom.xml";

            SAXReader reader   = new SAXReader();
            Document  document = reader.read( pomPath );

            Element dependencies = DependencySupport.getDependenciesElement( document );
            Element dependency   = DependencySupport.getDependencyElement( component, dependencies );

            if ( dependency != null ) {
                dependencies.remove( dependency );
            }

            DependencySupport.pomWriter( pomPath, document );

        } catch ( DocumentException e ) {
            e.printStackTrace();
        } catch ( IOException e ) {
            e.printStackTrace();
        } catch ( PrompterException e ) {
            e.printStackTrace();
        }

    }

}
