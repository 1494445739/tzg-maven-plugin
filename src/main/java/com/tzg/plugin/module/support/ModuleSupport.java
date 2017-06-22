package com.tzg.plugin.module.support;

import com.tzg.plugin.module.model.ColumnMetadata;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ModuleSupport {

    private final static String ROOT_PATH = System.getProperty( "user.dir" );

    private final static Logger logger = LoggerFactory.getLogger( ModuleSupport.class );

    /**
     * 根据user.dir返回当前目录的项目名缩写。比如：webapp-auth将返回auth。规则是以`-`为界。
     */
    public static String getCurrentProjectName() {

        String project = ROOT_PATH.substring( ROOT_PATH.lastIndexOf( System.getProperty( "file.separator" ) ) );

        if ( project.lastIndexOf( "-" ) > 0 ) {
            int startPos = project.indexOf( "-" ) + 1;
            int endPos   = startPos + project.substring( startPos ).indexOf( "-" );
            project = project.substring( startPos, endPos );
        }

        return project;

    }

    /**
     * 根据模块名返回要生成的文件路径集合
     *
     * @param module 模块名
     */
    public static String[] getFiles( String module ) {

        String modelFileName     = module + ".java";
        String ctrlFileName      = module + "Controller.java";
        String mapperFileName    = module + "Mapper.java";
        String servApiFileName   = module + "Service.java";
        String servImplFileName  = module + "ServiceImpl.java";
        String xmlMapperFileName = module.substring( 0, 1 ).toLowerCase() + module.substring( 1 ) + ".xml";

        String project      = getCurrentProjectName();
        String javaDir      = ROOT_PATH + "/src/main/java/com/tzg/web/" + project + "/" + module.toLowerCase();
        String xmlMapperDir = ROOT_PATH + "/src/main/resources/mybatis/" + project;

        return new String[]{
                javaDir + "/" + modelFileName,
                javaDir + "/" + mapperFileName,
                javaDir + "/" + ctrlFileName,
                javaDir + "/" + servApiFileName,
                javaDir + "/" + servImplFileName,
                xmlMapperDir + "/" + xmlMapperFileName
        };

    }

    public static String getModulePath( String module ) {
        return ROOT_PATH + "/src/main/java/com/tzg/web/" + getCurrentProjectName() + "/" + module.toLowerCase();
    }

    public static String getMapperPath( String module ) {
        return ROOT_PATH + "/src/main/resources/mybatis/" + getCurrentProjectName() + "/" + module.substring( 0, 1 ).toLowerCase() + module.substring( 1 ) + ".xml";
    }

    public static String getMapperDirPath() {
        return ROOT_PATH + "/src/main/resources/mybatis/" + getCurrentProjectName();
    }

    /**
     * 返回velocity模板路径集合。
     */
    public static String[] getTemplates() {

        String vmJavaDir      = "gen/java/";
        String vmXmlMapperDir = "gen/mybatis/";

        return new String[]{
                vmJavaDir + "model.vm",
                vmJavaDir + "mapper.vm",
                vmJavaDir + "controller.vm",
                vmJavaDir + "service.vm",
                vmJavaDir + "serviceImpl.vm",
                vmXmlMapperDir + "mybatis.vm"
        };

    }

    /**
     * 从数据库获取指定表结构。
     *
     * @param table 数据库表名
     */
    public static List< ColumnMetadata > getTableMetadata( String table ) throws SQLException, ClassNotFoundException {

        List< ColumnMetadata > columnMetadataList = new ArrayList<>();

        String driver   = "com.mysql.jdbc.Driver";
        String username = System.getProperty( "username" );
        String password = System.getProperty( "password" );
        username = StringUtils.isBlank( username ) ? "root" : username;
        password = StringUtils.isBlank( password ) ? "root" : password; // 默认密码`root`

        String ip = System.getProperty( "ip" );
        String db = System.getProperty( "db" );
        String url = "jdbc:mysql://$ip:3306/$db?useUnicode=true&characterEncoding=UTF-8"
                .replace( "$ip", StringUtils.isBlank( ip ) ? "localhost" : ip )
                .replace( "$db", StringUtils.isBlank( db ) ? "test" : db );

        logger.info( "====>Url: " + url );
        logger.info( "====>User: " + username );
        logger.info( "====>Password: " + password );

        Class.forName( driver );
        Connection conn = DriverManager.getConnection( url, username, password );
        ResultSet  rs   = conn.getMetaData().getColumns( null, "%", table.toUpperCase(), "%" );

        while ( rs.next() ) {
            ColumnMetadata columnMetadata = new ColumnMetadata();
            columnMetadata.setColumnName( rs.getString( "COLUMN_NAME" ) );
            columnMetadata.setTypeName( rs.getString( "TYPE_NAME" ) );
            columnMetadata.setRemarks( rs.getString( "REMARKS" ) );

            columnMetadataList.add( columnMetadata );
        }

        rs.close();
        conn.close();

        return columnMetadataList;
    }

    public static Map< String, Object > getMapperFragment( Node node, InternalContextAdapter context, String fragment ) {

        SimpleNode modelQualifiedNameNode = ( SimpleNode ) node.jjtGetChild( 2 );
        String     modelQualifiedName     = ( String ) modelQualifiedNameNode.value( context );

        Map< String, Object > map = getPropMap( node, context );
        map.put( "modelQualifiedName", modelQualifiedName );

        StringBuilder sb = new StringBuilder();

        if ( !StringUtils.isBlank( fragment ) ) {
            sb.append( "#foreach( $columnMetadata in $columnMetadataList )" );
            sb.append( fragment );
            sb.append( "#end" );
            map.put( "content", sb.toString() );
        }

        return map;

    }

    public static Map< String, Object > getPropMap( Node node, InternalContextAdapter context ) {

        SimpleNode propsNode          = ( SimpleNode ) node.jjtGetChild( 0 );
        Object     columnMetadataList = propsNode.value( context );

        SimpleNode stringHelperNode = ( SimpleNode ) node.jjtGetChild( 1 );
        Object     stringHelper     = stringHelperNode.value( context );

        Map< String, Object > map = new HashMap<>();
        map.put( "columnMetadataList", columnMetadataList );
        map.put( "stringHelper", stringHelper );

        return map;

    }

    /**
     * 获取生成model文件中的片段代码。比如property或者method。
     *
     * @param node
     * @param context
     * @param fragment 核心片段代码
     */
    public static Map< String, Object > getModelFragment( Node node, InternalContextAdapter context, String fragment ) {

        Map< String, Object > map = getPropMap( node, context );

        StringBuilder sb = new StringBuilder();

        sb.append( "#set( $i   = 0 ) " );
        sb.append( "#foreach( $columnMetadata in $!{columnMetadataList} )" );

        sb.append( "    #set( $i = $i + 1 ) " );
        sb.append( "    #set( $dbType = $columnMetadata.typeName )" );

        sb.append( "    #if( $dbType == 'VARCHAR' )" );
        sb.append( "        #set( $javaType = 'String' )" );
        sb.append( "    #elseif( $dbType == 'INT' || $dbType == 'INT UNSIGNED' )" );
        sb.append( "        #set( $javaType = 'Integer' )" );
        sb.append( "    #elseif( $dbType == 'DATE' || $dbType == 'DATETIME' || $dbType == 'TIMESTAMP')" );
        sb.append( "        #set( $javaType = 'java.util.Date' )" );
        sb.append( "    #elseif( $dbType == 'DECIMAL' )" );
        sb.append( "        #set( $javaType = 'java.math.BigDecimal' )" );
        sb.append( "    #end" );

        sb.append( fragment ).append( "\n" );

        sb.append( "#end" );

        map.put( "content", sb.toString() );

        return map;

    }

    public static String getDirectiveFilterStr( String content ) {
        return content.replaceAll( "[ ]*(#if|#else|#elseif|#end|#set|#foreach)", "$1" ).replaceAll( "[ ]*(private|public|@Override|return|<resultMap|<sql|<update|<insert)", "\t$1" );
    }

    public static String renderTemplate( Map< String, Object > map ) throws UnsupportedEncodingException {

        VelocityEngine velocityEngine = new VelocityEngine();

        String          content = ( String ) map.get( "content" );
        VelocityContext context = new VelocityContext( map );

        StringWriter writer = new StringWriter();

        velocityEngine.evaluate( context, writer, "", getDirectiveFilterStr( content ) );   // 过滤velocity指令前的空格

        return writer.toString();

    }

}
