package com.tzg.plugin.dependency.goal;

import com.tzg.plugin.dependency.support.DependencySupport;
import com.tzg.plugin.dependency.support.DubboSupport;
import com.tzg.plugin.dependency.support.MongoDBSupport;
import com.tzg.plugin.dependency.support.RedisSupport;
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
import java.util.ArrayList;
import java.util.List;

/**
 * @goal dep-gen
 */
public class DependencyGen extends AbstractMojo {

    /**
     * @component
     * @required
     */
    private Prompter prompter;

    @Override
    @SuppressWarnings( "unchecked" )
    public void execute() throws MojoExecutionException, MojoFailureException {

        String prompt = DependencySupport.getPrompt( "GENERATE" );

        try {

            String  component    = null;
            boolean isCandidated = false;   // 针对组件的候选版本，比如1.0.2-RC
            String  index        = DependencySupport.getIndex( prompter, prompt );
            switch ( index ) {
                case "1":
                    component = "component-browser-starter";
                    break;
                case "2":
                    component = "component-mongodb";
                    // DependencySupport.appendProperties( DependencySupport.getPropertiesPath(), "mongoDB", MongoDBSupport.getMongoDBMap(), MongoDBSupport.getMongoDBDeclaration() );
                    MongoDBSupport.genMongoDBModule();
                    break;
                case "3":
                    component = "component-redis";
                    // DependencySupport.appendProperties( DependencySupport.getPropertiesPath(), "redis", RedisSupport.getRedisMap(), RedisSupport.getRedisDeclaration() );
                    RedisSupport.genRedisDBModule();
                    break;
                case "4":
                    component = "web-auth";
                    break;
                case "5":
                    component = "web-auth";
                    isCandidated = true;
                    break;
                case "6":
                    component = "component-dubbo";
                    // DependencySupport.appendProperties( DependencySupport.getPropertiesPath(), "dubbo", DubboSupport.getDubboMap(), DubboSupport.getDubboDeclaration() );
                    DubboSupport.genDubboModule();
                    break;
                case "7":
                    component = "component-druid-statistics";
                    break;

            }

            // 读取xml，根据输入的component进行查找，如果查找不到，则生成相关的组件，并写入pom.xml文件
            String pomPath = DependencySupport.getRootPath() + "/pom.xml";

            SAXReader reader   = new SAXReader();
            Document  document = reader.read( pomPath );

            Element         dependencies   = DependencySupport.getDependenciesElement( document );
            List< Element > dependencyList = new ArrayList<>();

            if ( dependencies != null ) {
                dependencyList = DependencySupport.getDependencyElement( component, dependencies );
            }

            if ( dependencyList.size() == 0 ) {

                if ( dependencies == null ) {
                    Element rootElement = document.getRootElement();
                    dependencies = rootElement.addElement( "dependencies" );
                }

                // create <dependency> node
                Element dependencyElement = dependencies.addElement( "dependency" );
                dependencyElement.addElement( "groupId" ).addText( "com.tzg" );
                dependencyElement.addElement( "artifactId" ).addText( component );

                if ( component.contains( "web-" ) ) {
                    if ( isCandidated ) {
                        dependencyElement.addElement( "version" ).addText( "${project.parent.version}-RC" );
                    }
                    dependencyElement.addElement( "type" ).addText( "war" );

                    Element warClassifierDependency = dependencies.addElement( "dependency" );
                    warClassifierDependency.addElement( "groupId" ).addText( "com.tzg" );
                    warClassifierDependency.addElement( "artifactId" ).addText( component );
                    if ( isCandidated )
                        warClassifierDependency.addElement( "version" ).addText( "${project.parent.version}-RC" );
                    warClassifierDependency.addElement( "classifier" ).addText( "classes" );
                }
            }

            DependencySupport.pomWriter( pomPath, document );

        } catch ( DocumentException e ) {
            e.printStackTrace();
        } catch ( PrompterException e ) {
            e.printStackTrace();
        } catch ( IOException e ) {
            e.printStackTrace();
        } catch ( Exception e ) {
            e.printStackTrace();
        }

    }

}
