package com.tzg.plugin.module.directive;

import com.tzg.plugin.module.support.ModuleSupport;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class MapperSQLDirective extends Directive {

    @Override
    public String getName() {
        return "genSQL";
    }

    @Override
    public int getType() {
        return LINE;
    }

    @Override
    public boolean render( InternalContextAdapter context, Writer writer, Node node )
            throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {

        StringBuilder fragment = new StringBuilder();

        fragment.append( "#set( $property = $stringHelper.camel( $columnMetadata.columnName.toLowerCase() ) )" );
        fragment.append( "#set( $column   = $columnMetadata.columnName.toUpperCase() )" ).append( "\t\t\t" );
        fragment.append( "<if test=\"$property != null and $property != ''\">and $column = #{$property}</if>" ).append( "\n" );

        Map< String, Object > map = ModuleSupport.getMapperFragment( node, context, fragment.toString() );

        String content = ( String ) map.get( "content" );

        StringBuilder sb = new StringBuilder();
        sb.append( "<sql id=\"where\">" ).append( "\n\t" );
        sb.append( "    <where>" ).append( "\n" );
        sb.append( content ).append( "\t" );
        sb.append( "    </where>" ).append( "\n\t" );
        sb.append( "</sql>" ).append( "\n" );

        map.put( "content", sb.toString() );

        writer.write( ModuleSupport.renderTemplate( map ) );

        return true;

    }

}
