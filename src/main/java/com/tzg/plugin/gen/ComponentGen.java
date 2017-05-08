package com.tzg.plugin.gen;

import com.tzg.plugin.support.helper.PluginHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.util.List;

/**
 * @goal component-gen
 */
public class ComponentGen extends AbstractMojo {

    private final static String PROMPT = "Enter component's name. such as( component-browser-start ) ";

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

            String pomPath = PluginHelper.getRootPath() + "/pom.xml";

            // 读取xml，根据输入的component进行查找，如果查找不到，则生成相关的组件，并写入pom.xml文件
            try {
                SAXReader       reader                = new SAXReader();
                Document        doc                   = reader.read( pomPath );
                Element         rootElement           = doc.getRootElement();
                Element         dependenciesElement   = rootElement.element( "dependencies" );
                List< Element > dependencyElementList = dependenciesElement.elements();
                Boolean         isComponentExist      = false;
                for ( Element dependency : dependencyElementList ) {
                    Element artifactId = dependency.element( "artifactId" );
                    String  artifact   = artifactId.getTextTrim();
                    if ( artifact.equalsIgnoreCase( component ) ) {
                        isComponentExist = true;
                        break;
                    }
                }

                if ( !isComponentExist ) {
                    // create <dependency> node
                    Element dependencyElement = dependenciesElement.addElement( "dependency" );
                    dependencyElement.addElement( "groupId" ).addText( "com.tzg" );
                    dependencyElement.addElement( "artifactId" ).addText( component );
                }

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

            } catch ( DocumentException e ) {
                e.printStackTrace();
            } catch ( FileNotFoundException e ) {
                e.printStackTrace();
            } catch ( UnsupportedEncodingException e ) {
                e.printStackTrace();
            } catch ( IOException e ) {
                e.printStackTrace();
            }

        } catch ( Exception e ) {
            getLog().error( e.getMessage(), e );
        }

    }

}
