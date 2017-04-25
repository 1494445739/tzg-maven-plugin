package com.tzg.plugin.support.directive;

import com.tzg.plugin.support.helper.PluginHelper;
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

public class MapperSelectListDirective extends Directive {

    @Override
    public String getName() {
        return "genSelectList";
    }

    @Override
    public int getType() {
        return LINE;
    }

    @Override
    public boolean render( InternalContextAdapter context, Writer writer, Node node )
            throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {

        StringBuilder fragment = new StringBuilder();

        SimpleNode tableNode = ( SimpleNode ) node.jjtGetChild( 3 );
        String     table     = ( String ) tableNode.value( context );

        Map< String, Object > map = PluginHelper.getMapperFragment( node, context, fragment.toString() );
        map.put( "table", table );

        StringBuilder sb = new StringBuilder();
        sb.append( "#set( $cols = \"\" )" );
        sb.append( "\t" );
        sb.append( "<select id=\"selectList\" parameterType=\"Map\" resultMap=\"result\">" ).append( "\n\t" );
        sb.append( "    SELECT <include refid=\"columns\"/> FROM $table" ).append( "\n\t" );
        sb.append( "    <include refid=\"where\"/>" ).append( "\n\t" );
        sb.append( "    ORDER BY id DESC" ).append( "\n\n\t" );

        sb.append( "    <if test=\"size != null or skip != null\">" ).append( "\n\t" );
        sb.append( "        LIMIT #{skip}, #{size}" ).append( "\n\t" );
        sb.append( "    </if>" ).append( "\n\t" );

        sb.append( "</select>" ).append( "\n" );

        map.put( "content", sb.toString() );

        writer.write( PluginHelper.renderTemplate( map ) );

        return true;
    }

}
