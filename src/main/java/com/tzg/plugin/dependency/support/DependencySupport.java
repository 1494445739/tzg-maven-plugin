package com.tzg.plugin.dependency.support;

import org.apache.commons.lang3.StringUtils;
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
        sb.append( "3) web-auth\n" );
        sb.append( "Please enter the number you want to keyword. Default is No.1)" );

        return sb.toString().replaceAll( "keyword", keyword );
    }

    public static String getIndex( Prompter prompter, String prompt ) throws PrompterException {
        StringBuilder errMsg = new StringBuilder();

        String input = prompter.prompt( prompt );
        String index = StringUtils.isBlank( input ) ? "1" : input;

        int i = 1; // 控制打印的索引
        while ( !DependencySupport.isNumeric( index ) || ( Integer.valueOf( index ) < 0 || Integer.valueOf( index ) > 3 ) ) {

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
     * @param pomPath    文件地址
     * @param key        模糊匹配key的值
     * @param commentLen 删除注释的长度。例如mongdb。位置为从下往上
     * @return
     * @throws IOException
     */
    @SuppressWarnings( "resource" )
    public static boolean clearProperties( String pomPath, String key, int commentLen )
            throws IOException {
        File file = new File( pomPath );

        boolean hasKey      = false;// 是否存在删除的key
        int     hasKeyIndex = 0;// 存在key当前的索引

        if ( file.exists() ) {
            InputStreamReader isr = new InputStreamReader( new FileInputStream( file ), "utf-8" );
            BufferedReader    br  = new BufferedReader( isr );

            List< String > lineContent = new ArrayList<>();
            String         line;
            while ( ( line = br.readLine() ) != null ) {
                if ( StringUtils.isBlank( line ) ) {
                    lineContent.add( "\n" );
                } else if ( line.startsWith( "#" ) ) {
                    lineContent.add( line + "\n" );
                } else if ( line.trim().startsWith( key ) ) {
                    if ( !hasKey ) {
                        hasKeyIndex = lineContent.size() - 1;
                    }
                    hasKey = true;
                } else {
                    lineContent.add( line + "\n" );
                }
            }

            StringBuffer outStr = new StringBuffer();
            if ( hasKeyIndex > 0 ) {
                // 需要移除的范围
                int endIndex = hasKeyIndex;// 移除元素结束索引
                int begIndex = endIndex - commentLen;// 移除元素开始索引
                begIndex = begIndex < 0 ? 0 : begIndex;

                for ( int i = 0; i < lineContent.size(); i++ ) {
                    if ( i <= begIndex || i > endIndex ) {
                        outStr.append( lineContent.get( i ) );
                    }
                }
            }

            if ( hasKey ) {
                FileWriter     fw = new FileWriter( file, false );
                BufferedWriter bw = new BufferedWriter( fw );
                bw.write( outStr.toString() );
                bw.close();
                fw.close();
                return true;
            }
        }
        return false;

    }

    public static void appendProperties( Map< String, String > map, String declaration, String component )
            throws IOException {

        Properties src      = new Properties();
        String     propFile = DependencySupport.getPropertiesPath();
        src.load( new FileInputStream( propFile ) );

        // 从.properties文件中读取key，并放入到Set中
        Set< String >    set         = new HashSet<>();
        Enumeration< ? > enumeration = src.propertyNames();
        while ( enumeration.hasMoreElements() ) {
            set.add( ( String ) enumeration.nextElement() );
        }

        Properties tar = new Properties();
        for ( String key : map.keySet() ) {
            boolean isExist = false;
            for ( String k : set ) {
                if ( key.equalsIgnoreCase( k ) ) {
                    isExist = true;
                    break;
                }
            }

            if ( !isExist ) {
                tar.setProperty( key, map.get( key ) );
            }
        }

        if ( tar.size() != 0 ) {

            FileWriter writer = new FileWriter( propFile, true );
            tar.store( writer, declaration );
            writer.close();

            System.out.println( "====> Finish append component in properties file. Path is: ".replaceAll( "component", component ) + propFile );

        } else {
            System.out.println( "====> component config already exist in .properties file. ".replaceAll( "component", component ) );
        }

    }

}
