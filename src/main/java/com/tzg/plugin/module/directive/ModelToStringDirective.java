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
import java.util.HashMap;
import java.util.Map;

public class ModelToStringDirective extends Directive {

    @Override
    public String getName() {
        return "genModelToString";
    }

    @Override
    public int getType() {
        return LINE;
    }

    @Override
    public boolean render( InternalContextAdapter context, Writer writer, Node node )
            throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {

        SimpleNode propsNode          = ( SimpleNode ) node.jjtGetChild( 0 );
        Object     columnMetadataList = propsNode.value( context );

        SimpleNode classNameNode = ( SimpleNode ) node.jjtGetChild( 1 );
        String     className     = ( String ) classNameNode.value( context );

        SimpleNode stringHelperNode = ( SimpleNode ) node.jjtGetChild( 2 );
        Object     stringHelper     = stringHelperNode.value( context );

        Map< String, Object > map = new HashMap<>();
        map.put( "columnMetadataList", columnMetadataList );
        map.put( "className", className );
        map.put( "stringHelper", stringHelper );

        StringBuilder sb = new StringBuilder();

        sb.append( "#set( $i     = 0 )" );
        sb.append( "#set( $toStr = '' )" );

        sb.append( "#foreach( $columnMetadata in $!{columnMetadataList} )" );
        sb.append( "    #set( $i    = $i + 1 )" );
        sb.append( "    #set( $prop = $stringHelper.camel( $columnMetadata.columnName.toLowerCase() ) )" );

        sb.append( "    #if( $i == 1 ) " );
        sb.append( "        #set( $toStr = $toStr + '\"' + $prop +  \" = \"  + '\" + ' + $prop + \" + \"  )" );
        sb.append( "    #else" );
        sb.append( "        #set( $toStr = $toStr + '\", ' + $prop + \" = \" + '\" + ' + $prop + \" + \" )" );
        sb.append( "    #end" );
        sb.append( "#end" );

        sb.append( "#set( $len   = $toStr.length() - 3 )" );
        sb.append( "#set( $toStr = $toStr.substring( 0, $len ) )" );

        sb.append( "@Override" ).append( "\n" );
        sb.append( "public String toString()    {" ).append( "\n\t" );
        sb.append( "    return \"$className{\" + $toStr + '}';" ).append( "\n\t" );
        sb.append( "}" ).append( "\n" );

        map.put( "content", sb.toString() );

        writer.write( ModuleSupport.renderTemplate( map ) );

        return true;

    }

}
