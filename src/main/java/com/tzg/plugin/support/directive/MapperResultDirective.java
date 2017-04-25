package com.tzg.plugin.support.directive;

import com.tzg.plugin.support.helper.PluginHelper;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class MapperResultDirective extends Directive {

    @Override
    public String getName() {
        return "genResultMap";
    }

    @Override
    public int getType() {
        return LINE;
    }

    @Override
    public boolean render( InternalContextAdapter context, Writer writer, Node node )
            throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {

        StringBuilder fragment = new StringBuilder();

        fragment.append( "    #if( !$columnMetadata.columnName.equalsIgnoreCase( \"ID\") )" );
        fragment.append( "      #set( $column   = $columnMetadata.columnName.toUpperCase() )" );
        fragment.append( "      #set( $property = $stringHelper.camel( $columnMetadata.columnName.toLowerCase() ) )" );
        fragment.append( "        <result column=\"$column\" property=\"$property\" />" ).append( "\n" );
        fragment.append( "    #end" );

        Map< String, Object > map = PluginHelper.getMapperFragment( node, context, fragment.toString() );

        String content = ( String ) map.get( "content" );

        StringBuilder sb = new StringBuilder();
        sb.append( "<resultMap id=\"result\" type=\"$modelQualifiedName\">" ).append( "\n\t" );
        sb.append( "    <id column=\"id\" property=\"id\" />" ).append( "\n" );
        sb.append( content ).append( "\t" );
        sb.append( "</resultMap>" ).append( "\n" );

        map.put( "content", sb.toString() );

        writer.write( PluginHelper.renderTemplate( map ) );

        return true;

    }

}
