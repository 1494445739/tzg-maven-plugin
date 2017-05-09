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

public class MapperUpdateDirective extends Directive {

    @Override
    public String getName() {
        return "genUpdate";
    }

    @Override
    public int getType() {
        return LINE;
    }

    @Override
    public boolean render( InternalContextAdapter context, Writer writer, Node node )
            throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {

        StringBuilder fragment = new StringBuilder();

        fragment.append( "#set( $property = $stringHelper.camel( $columnMetadata.columnName.toLowerCase() ) ) " );
        fragment.append( "#set( $column   = $columnMetadata.columnName.toUpperCase() )" );
        fragment.append( "\t\t\t" );
        fragment.append( "<if test=\"$property != null\">$column = #{$property},</if>" );
        fragment.append( "\n" );

        SimpleNode tableNode = ( SimpleNode ) node.jjtGetChild( 3 );
        String     table     = ( String ) tableNode.value( context );

        Map< String, Object > map = ModuleSupport.getMapperFragment( node, context, fragment.toString() );
        map.put( "table", table );

        String content = ( String ) map.get( "content" );

        StringBuilder sb = new StringBuilder();
        sb.append( "<update id=\"update\" parameterType=\"$modelQualifiedName\">" ).append( "\n\t" );
        sb.append( "    UPDATE $table" ).append( "\n\n\t" );
        sb.append( "    <set>" ).append( "\n" );
        sb.append( content ).append( "\t" );
        sb.append( "    </set>" ).append( "\n\n\t" );
        sb.append( "    WHERE id = #{id}" ).append( "\n\t" );
        sb.append( "</update>" ).append( "\n" );

        map.put( "content", sb.toString() );

        writer.write( ModuleSupport.renderTemplate( map ) );

        return true;

    }

}
