package com.tzg.plugin.help;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @goal help
 */
public class Help extends AbstractMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {

        getLog().info( "" );
        getLog().info( "tzg:gen" );
        getLog().info( "==> Code generator. Attention! Table must exist in MySQL's `test` database locally." );
        getLog().info( "" );

        getLog().info( "" );
        getLog().info( "tzg:help" );
        getLog().info( "==> Display `help` information about tzg's plugin tool" );
        getLog().info( "" );

        getLog().info( "" );
        getLog().info( "tzg:rm" );
        getLog().info( "==> Input module name and remove module files." );

        getLog().info( "" );
        getLog().info( "tzg:component-gen" );
        getLog().info( "==> Input component name. System will append component dependency or properties in pom.xml and evn_${profile}.properties" );

    }

}
