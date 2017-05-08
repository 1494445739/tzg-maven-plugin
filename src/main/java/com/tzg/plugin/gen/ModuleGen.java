package com.tzg.plugin.gen;

import com.tzg.plugin.support.model.ColumnMetadata;
import com.tzg.plugin.support.model.Parameter;
import com.tzg.plugin.support.helper.PluginHelper;
import com.tzg.tool.support.datatype.StringUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.codehaus.plexus.components.interactivity.Prompter;

import java.io.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

/**
 * @goal gen
 */
public class ModuleGen extends AbstractMojo {

    private final static String PROMPT = "Enter project module name. First character must be capitalized";

    /**
     * @component
     * @required
     */
    private Prompter prompter;

    /**
     * maven插件goal执行的入口方法
     */
    public void execute() throws MojoExecutionException, MojoFailureException {

        try {

            String table  = System.getProperty( "table" );
            String module = System.getProperty( "module" );
            module = StringUtils.isBlank( module ) ? prompter.prompt( PROMPT ) : module;

            // 模块名不能为null
            while ( StringUtils.isBlank( module ) ) {
                module = prompter.prompt( PROMPT );
            }

            // 如果表名为空，说明模块名跟表名相同
            if ( StringUtils.isBlank( table ) ) table = module;

            // 获取项目名
            String project = PluginHelper.getCurrentProjectName();
            getLog().info( "====>Project: " + project );

            // 获取数据库指定表结构
            List< ColumnMetadata > columnMetadataList = PluginHelper.getTableMetadata( table );
            getLog().info( "====>Table structure: " + columnMetadataList );

            // 生成模块文件集合
            String[]  templates = PluginHelper.getTemplates();
            String[]  files     = PluginHelper.getFiles( module );
            Parameter param     = new Parameter( project, module, table, columnMetadataList );

            for ( int i = 0; i < files.length; i++ ) {
                mergeTemplate( files[ i ], templates[ i ], param );
            }


        } catch ( Exception e ) {
            getLog().error( e.getMessage(), e );
        }

    }

    /**
     * 将合并后的vm模板内容输出到指定的生成文件中。
     *
     * @param filePath 指定的生成文件路径
     * @param tplPath  vm模板路径
     * @param param    parameter对象
     */
    private void mergeTemplate( String filePath, String tplPath, Parameter param )
            throws IOException, SQLException, ClassNotFoundException {

        File file = new File( filePath );
        FileUtils.touch( file );

        Properties properties = new Properties();
        properties.setProperty( "resource.loader", "class" );
        properties.setProperty( "class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader" );
        properties.setProperty( "userdirective", "com.tzg.plugin.support.directive.ModelPropDirective, com.tzg.plugin.support.directive.ModelMethodDirective, "
                + "com.tzg.plugin.support.directive.ModelToStringDirective, com.tzg.plugin.support.directive.MapperResultDirective, "
                + "com.tzg.plugin.support.directive.MapperSQLDirective, com.tzg.plugin.support.directive.MapperUpdateDirective, "
                + "com.tzg.plugin.support.directive.MapperSelectByIdDirective, com.tzg.plugin.support.directive.MapperInsertDirective, "
                + "com.tzg.plugin.support.directive.MapperSelectListDirective" );

        VelocityEngine  engine  = new VelocityEngine( properties );
        VelocityContext context = new VelocityContext();
        context.put( "project", param.getProject() );
        context.put( "module", param.getModule() );

        context.put( "table", param.getTable() );

        context.put( "columnMetadataList", param.getColumnMetadataList() );

        context.put( "stringHelper", new StringUtil() );

        Writer writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( file ), "UTF-8" ) );
        engine.mergeTemplate( tplPath, "UTF-8", context, writer );

        writer.flush();
        writer.close();

    }

}
