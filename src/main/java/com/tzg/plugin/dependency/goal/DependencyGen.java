package com.tzg.plugin.dependency.goal;

import com.tzg.plugin.dependency.support.DependencySupport;
import com.tzg.plugin.dependency.support.MongoDBSupport;
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
 * @goal dep-gen
 */
public class DependencyGen extends AbstractMojo {

    private final static String PROMPT = "Enter component's name. such as( component-browser-starter ) ";

    /**
     * @component
     * @required
     */
    private Prompter prompter;

    @Override
    @SuppressWarnings( "unchecked" )
    public void execute() throws MojoExecutionException, MojoFailureException {

        String component = System.getProperty( "component" );

        try {

            // 组件名不能为null，否则一直循环要求输入，直至进程被终止
            while ( StringUtils.isBlank( component ) ) {
                component = prompter.prompt( PROMPT );
            }

            // 读取xml，根据输入的component进行查找，如果查找不到，则生成相关的组件，并写入pom.xml文件
            String pomPath = DependencySupport.getRootPath() + "/pom.xml";

            SAXReader reader   = new SAXReader();
            Document  document = reader.read( pomPath );

            Element dependencies = DependencySupport.getDependenciesElement( document );
            Element dependency   = DependencySupport.getDependencyElement( component, dependencies );

            if ( dependency == null ) {
                // create <dependency> node
                Element dependencyElement = dependencies.addElement( "dependency" );
                dependencyElement.addElement( "groupId" ).addText( "com.tzg" );
                dependencyElement.addElement( "artifactId" ).addText( component );
            }

            DependencySupport.pomWriter( pomPath, document );

            switch ( component ) {
                case "component-mongodb":
                    MongoDBSupport.execute();
                    break;
            }

        } catch ( DocumentException e ) {
            e.printStackTrace();
        } catch ( PrompterException e ) {
            e.printStackTrace();
        } catch ( IOException e ) {
            e.printStackTrace();
        }

    }

}
