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

public class ModelMethodDirective extends Directive {

    @Override
    public String getName() {
        return "genModelMethod";
    }

    @Override
    public int getType() {
        return LINE;
    }

    @Override
    public boolean render( InternalContextAdapter context, Writer writer, Node node )
            throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {

        StringBuilder fragment = new StringBuilder();

        fragment.append( "#set( $propName = $columnMetadata.columnName.substring(0, 1).toUpperCase() + $stringHelper.camel( $columnMetadata.columnName.substring(1).toLowerCase() ) )" );
        fragment.append( "#set( $getter   = 'get' + $propName )" );
        fragment.append( "#set( $setter   = 'set' + $propName )" );
        fragment.append( "#set( $prop     = $stringHelper.camel( $columnMetadata.columnName.toLowerCase() ) )" );

        fragment.append( "public $javaType $getter() {" );
        fragment.append( "  return $prop;  " );
        fragment.append( "}" );

        fragment.append( "\n" );

        fragment.append( "public void $setter( $javaType $prop ) {" );
        fragment.append( "  this.$prop = $prop;  " );
        fragment.append( "}" );

        fragment.append( "\n" );

        Map< String, Object > map = PluginHelper.getModelFragment( node, context, fragment.toString() );
        writer.write( PluginHelper.renderTemplate( map ) );

        return true;

    }


}
