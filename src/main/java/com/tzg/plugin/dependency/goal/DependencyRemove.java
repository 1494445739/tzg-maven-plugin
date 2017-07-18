package com.tzg.plugin.dependency.goal;

import com.tzg.plugin.dependency.support.*;
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
import java.util.List;

/**
 * @goal dep-rm
 */
public class DependencyRemove extends AbstractMojo {

    /**
     * @component
     * @required
     */
    private Prompter prompter;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        String prompt = DependencySupport.getPrompt( "REMOVE" );

        try {

            String component = null;
            String index     = DependencySupport.getIndex( prompter, prompt );
            switch ( index ) {
                case "1":
                    component = "component-browser-starter";
                    break;
                case "2":
                    component = "component-mongodb";
                    //                    DependencySupport.clearProperties( DependencySupport.getPropertiesPath(), "mongoDB", MongoDBSupport.MONGODB_COMMENT_LENGTH );
                    DependencySupport.removeModule( MongoDBSupport.getMongoDBModulePath(), "mongoDB" );
                    break;
                case "3":
                    component = "component-redis";
                    //                    DependencySupport.clearProperties( DependencySupport.getPropertiesPath(), "redis", RedisSupport.REDIS_COMMENT_LENGTH );
                    DependencySupport.removeModule( RedisSupport.getRedisModulePath(), "redis" );
                    break;
                case "4":
                case "5":
                    component = "web-auth";
                    break;
                case "6":
                    component = "component-dubbo";
                    //                    DependencySupport.clearProperties( DependencySupport.getPropertiesPath(), "dubbo", DubboSupport.DUBBO_COMMENT_LENGTH );
                    DependencySupport.removeModule( DubboSupport.getDubboModulePath(), "dubbo" );
                    DubboSupport.removeXml();
                    break;
                case "7":
                    component = "component-druid-statistics";
                    break;
                case "8":
                    component = "component-logback-dynamic-config";
                    break;
                case "9":
                    component = "component-batch";
                    BatchSupport.removeXml();
                    break;
            }

            // 读取xml，根据输入的component进行查找，如果找到，则删除相关的组件，并写入pom.xml文件
            String pomPath = DependencySupport.getRootPath() + "/pom.xml";

            SAXReader reader   = new SAXReader();
            Document  document = reader.read( pomPath );

            Element dependencies = DependencySupport.getDependenciesElement( document );
            if ( dependencies != null ) {
                List< Element > dependencyList = DependencySupport.getDependencyElement( component, dependencies );

                if ( dependencyList.size() != 0 ) {

                    for ( Element dependency : dependencyList ) {
                        dependencies.remove( dependency );
                    }

                } else {
                    System.out.println( "====> Dependency component doesn't exist in pom.xml".replaceAll( "component", component ) );
                }

                // 如果dependencies标签为空, 则删除之
                if ( dependencies.isTextOnly() ) {
                    dependencies.getParent().remove( dependencies );
                }

                DependencySupport.pomWriter( pomPath, document );
                System.out.println( "====> Remove component dependency successfully".replaceAll( "component", component ) );

            } else {
                System.out.println( "====> No dependencies Element exist." );
            }

        } catch ( DocumentException e ) {
            e.printStackTrace();
        } catch ( IOException e ) {
            e.printStackTrace();
        } catch ( PrompterException e ) {
            e.printStackTrace();
        }

    }

}
