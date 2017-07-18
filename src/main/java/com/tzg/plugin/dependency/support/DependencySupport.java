package com.tzg.plugin.dependency.support;

import com.tzg.plugin.module.support.ModuleSupport;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public final class DependencySupport {

    private final static String ROOT_PATH = System.getProperty( "user.dir" );

    public static String getRootPath() {
        return ROOT_PATH;
    }

    public static String getPropertiesPath() {
        String envProp = System.getProperty( "env" );
        envProp = org.springframework.util.StringUtils.isEmpty( envProp ) ? "dev" : envProp;
        return DependencySupport.getRootPath() + "/src/main/resources/properties/env_$env.properties".replace( "$env", envProp );
    }

    /**
     * pom.xml的写入
     *
     * @param pomPath pom.xml的文件路径
     * @param doc     dom4j的文档对象
     */
    public static void pomWriter( String pomPath, Document doc ) throws IOException {
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
    }

    /**
     * 从pom.xml中寻找指定artifactId的dependency元素
     *
     * @param component    组件的artifactId字符串
     * @param dependencies dependencies dom4j element
     * @return dependency element
     */
    @SuppressWarnings( "unchecked" )
    public static List< Element > getDependencyElement( String component, Element dependencies )
            throws DocumentException {

        List< Element > result                = new ArrayList<>();
        List< Element > dependencyElementList = dependencies.elements();
        for ( Element dependency : dependencyElementList ) {
            Element artifactId = dependency.element( "artifactId" );
            String  artifact   = artifactId.getTextTrim();
            if ( artifact.equalsIgnoreCase( component ) ) {
                result.add( dependency );
            }
        }

        return result;
    }

    /**
     * 根据pom.xml的路径返回dependencies元素
     *
     * @param document dom4j的pom.xml的document抽象
     */
    public static Element getDependenciesElement( Document document ) throws DocumentException {
        Element rootElement = document.getRootElement();
        return rootElement.element( "dependencies" );
    }

    public static boolean isNumeric( String str ) {
        Pattern pattern = Pattern.compile( "[0-9]*" );
        return pattern.matcher( str ).matches();
    }

    public static String getPrompt( String keyword ) {

        StringBuilder sb = new StringBuilder();
        sb.append( "Dependency registered: \n" );
        sb.append( "1) component-browser-starter \n" );
        sb.append( "2) component-mongodb \n" );
        sb.append( "3) component-redis \n" );
        sb.append( "4) web-auth\n" );
        sb.append( "5) web-auth ( sso )\n" );
        sb.append( "6) component-dubbo\n" );
        sb.append( "7) component-druid-statistics\n" );
        sb.append( "8) component-logback-dynamic-config\n" );
        sb.append( "9) component-batch\n" );
        sb.append( "Please enter the number you want to keyword. Default is No.1)" );

        return sb.toString().replaceAll( "keyword", keyword );
    }

    public static String getIndex( Prompter prompter, String prompt ) throws PrompterException {
        StringBuilder errMsg = new StringBuilder();

        String input = prompter.prompt( prompt );
        String index = StringUtils.isBlank( input ) ? "1" : input;

        int i = 1; // 控制打印的索引
        while ( !DependencySupport.isNumeric( index ) || ( Integer.valueOf( index ) < 0 || Integer.valueOf( index ) > 9 ) ) {

            if ( i++ != 1 ) {
                errMsg.append( "\n" ); // 第一次输入的时候不打印空行，反之则打印空行
            }

            errMsg.append( "------------------------------------------------------\n" );
            errMsg.append( "|-----      Error! You type wrong character     -----|\n" );
            errMsg.append( "------------------------------------------------------\n" );

            {
                index = prompter.prompt( errMsg.append( prompt ).toString() );
                if ( StringUtils.isBlank( index ) ) index = "1";
            }

        }

        return index;
    }

    /**
     * @param pomPath 文件地址
     * @param key     组件的key值，比如component-mongodb的key就是mongoDB
     * @param rowSpan 删除注释的长度。例如mongdb。位置为从下往上
     * @return
     * @throws IOException
     */
    @SuppressWarnings( "resource" )
    public static boolean clearProperties( String pomPath, String key, int rowSpan )
            throws IOException {

        boolean isKey    = false;    // 是否存在删除的key
        int     keyIndex = 0;        // 存在key当前的索引

        File file = new File( pomPath );

        if ( file.exists() ) {

            BufferedReader reader = new BufferedReader( new InputStreamReader( new FileInputStream( file ), "utf-8" ) );

            List< String > content = new ArrayList<>();
            String         line;
            while ( ( line = reader.readLine() ) != null ) {
                if ( StringUtils.isBlank( line ) ) {
                    content.add( "\n" );
                } else if ( line.startsWith( "#" ) ) {
                    content.add( line + "\n" );
                } else if ( line.trim().startsWith( key ) ) {
                    if ( !isKey ) {
                        keyIndex = content.size() - 1;
                    }
                    isKey = true;
                } else {
                    content.add( line + "\n" );
                }
            }

            reader.close();

            StringBuffer sb = new StringBuffer();
            if ( keyIndex > 0 ) {
                // 需要移除的范围
                int endIndex = keyIndex;// 移除元素结束索引
                int begIndex = endIndex - rowSpan;// 移除元素开始索引
                begIndex = begIndex < 0 ? 0 : begIndex;

                for ( int i = 0; i < content.size(); i++ ) {
                    if ( i <= begIndex || i > endIndex ) {
                        sb.append( content.get( i ) );
                    }
                }
            }

            if ( isKey ) {
                BufferedWriter writer = new BufferedWriter( new FileWriter( file, false ) );
                writer.write( sb.toString() );
                writer.close();
                writer.close();
                return true;
            }

        }

        return false;

    }

    @SuppressWarnings( "resource" )
    public static boolean appendProperties( String filePath, String keyword, Map< String, String > map, String declaration )
            throws IOException {

        File file = new File( filePath );
        if ( file.exists() ) {
            InputStreamReader isr = new InputStreamReader( new FileInputStream( file ), "utf-8" );
            BufferedReader    br  = new BufferedReader( isr );

            StringBuffer content = new StringBuffer();
            String       line;

            while ( ( line = br.readLine() ) != null ) {

                if ( StringUtils.isBlank( line ) ) {
                    content.append( "\n" );
                } else if ( line.startsWith( "#" ) ) {
                    content.append( line + "\n" );
                } else if ( line.trim().startsWith( keyword ) ) {
                    return false;
                } else {
                    content.append( line + "\n" );
                }

            }

            content.append( comment2Unicode( declaration ) );
            // content.append( declaration );

            for ( String key : map.keySet() ) {
                content.append( format( key, key.length() ) + "= " + map.get( key ) + "\n" );
            }

            FileWriter     fw = new FileWriter( file, false );
            BufferedWriter bw = new BufferedWriter( fw );
            bw.write( content.toString() );
            bw.close();
            fw.close();
            br.close();

        }

        return true;
    }

    public static String comment2Unicode( String comment ) {

        StringBuffer sb = new StringBuffer();

        char[] chars = comment.toCharArray();

        for ( char charStr : chars ) {
            // 处理中文字符
            if ( Character.getType( charStr ) == Character.OTHER_LETTER ) {
                sb.append( "\\u" + Integer.toHexString( charStr ) );
            } else {
                sb.append( charStr );
            }
        }

        return sb.toString();

    }

    private static String format( String key, int keyLen ) {

        String blank = "";

        for ( int i = keyLen; i < MongoDBSupport.MONGODB_KEY_INTERVAL; i++ ) {
            blank = blank + " ";
        }

        return key + blank;

    }

    public static void mergeTemplate( String filePath, String tplPath ) throws IOException {

        File file = new File( filePath );
        FileUtils.touch( file );

        Properties properties = new Properties();
        properties.setProperty( "resource.loader", "class" );
        properties.setProperty( "class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader" );

        VelocityEngine  engine  = new VelocityEngine( properties );
        VelocityContext context = new VelocityContext();
        context.put( "project", ModuleSupport.getCurrentProjectName() );

        Writer writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( file ), "UTF-8" ) );
        engine.mergeTemplate( tplPath, "UTF-8", context, writer );

        writer.flush();
        writer.close();

    }

    public static void removeModule( String modulePath, String module ) throws IOException {
        File file = new File( modulePath );
        if ( file.exists() ) {
            FileUtils.forceDelete( file );
        }
        System.out.println( "====> delete module '$path' successfully.".replace( "$path", file.getAbsolutePath() ).replace( "module", module ) );
    }

}
