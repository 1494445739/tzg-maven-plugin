package com.tzg.plugin.module.directive;

import com.tzg.plugin.module.support.ModuleSupport;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class MapperInsertDirective extends Directive {

    @Override
    public String getName() {
        return "genInsert";
    }

    @Override
    public int getType() {
        return LINE;
    }

    @Override
    public boolean render( InternalContextAdapter context, Writer writer, Node node )
            throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {

        StringBuilder fragment = new StringBuilder();

        fragment.append( "#if( !$columnMetadata.columnName.equalsIgnoreCase(\"ID\") )" );
        fragment.append( "  #set( $cols = $cols + $columnMetadata.columnName.toUpperCase() + \", \" )" );
        fragment.append( "  #set( $prop = $stringHelper.camel( $columnMetadata.columnName.toLowerCase() ) )" );
        fragment.append( "  #if( $prop == \"cdt\" || $prop == \"udt\")" );
        fragment.append( "      #set( $props = $props + \"now()\" + \", \" )" );
        fragment.append( "  #else" );
        fragment.append( "      #set( $props = $props + \"#{\" + $prop + \"}, \" )" );
        fragment.append( "  #end" );
        fragment.append( "  #set( $colsLen = $cols.length() - 2 )" );
        fragment.append( "  #set( $propsLen = $props.length() - 2 )" );
        fragment.append( "#end" );

        SimpleNode tableNode = ( SimpleNode ) node.jjtGetChild( 3 );
        String     table     = ( String ) tableNode.value( context );

        Map< String, Object > map = ModuleSupport.getMapperFragment( node, context, fragment.toString() );
        map.put( "table", table );

        String content = ( String ) map.get( "content" );

        StringBuilder sb = new StringBuilder();
        sb.append( "#set( $cols = \"\" )" );
        sb.append( "#set( $props = \"\" )" );
        sb.append( "<insert id=\"insert\" parameterType=\"$modelQualifiedName\" keyProperty=\"id\" useGeneratedKeys=\"true\">" ).append( "\n\t" );
        sb.append( content );
        sb.append( "    INSERT INTO $table ( $cols.substring( 0, $colsLen ) ) VALUES ( $props.substring( 0, $propsLen ) )" ).append( "\n\t" );
        sb.append( "</insert>" ).append( "\n" );

        map.put( "content", sb.toString() );

        writer.write( ModuleSupport.renderTemplate( map ) );

        return true;

    }

}
