package com.tzg.plugin.dependency.goal;

import com.tzg.plugin.dependency.support.DependencySupport;
import com.tzg.plugin.dependency.support.MongoDBSupport;
import com.tzg.plugin.dependency.support.RedisSupport;
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
                    DependencySupport.clearProperties( DependencySupport.getPropertiesPath(), "mongoDB", MongoDBSupport.MONGODB_COMMENT_LENGTH );
                    DependencySupport.removeModule( MongoDBSupport.getMongoDBModulePath(), "mongoDB" );
                    break;
                case "3":
                    component = "component-redis";
                    DependencySupport.clearProperties( DependencySupport.getPropertiesPath(), "redis", RedisSupport.REDIS_COMMENT_LENGTH );
                    DependencySupport.removeModule( RedisSupport.getRedisModulePath(), "redis" );
                    break;
                case "4":
                    component = "web-auth";
                    break;
            }

            // 读取xml，根据输入的component进行查找，如果找到，则删除相关的组件，并写入pom.xml文件
            String pomPath = DependencySupport.getRootPath() + "/pom.xml";

            SAXReader reader   = new SAXReader();
            Document  document = reader.read( pomPath );

            Element         dependencies   = DependencySupport.getDependenciesElement( document );
            List< Element > dependencyList = DependencySupport.getDependencyElement( component, dependencies );

            if ( dependencyList.size() != 0 ) {

                for ( Element dependency : dependencyList ) {
                    dependencies.remove( dependency );
                }

                DependencySupport.pomWriter( pomPath, document );
                System.out.println( "====> Remove component dependency successfully".replaceAll( "component", component ) );

            } else {
                System.out.println( "====> Dependency component doesn't exist in pom.xml".replaceAll( "component", component ) );
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
