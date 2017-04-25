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

public class ModelPropDirective extends Directive {

    @Override
    public String getName() {
        return "genModelProp";
    }

    @Override
    public int getType() {
        return LINE;
    }

    @Override
    public boolean render( InternalContextAdapter context, Writer writer, Node node )
            throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {

        StringBuilder fragment = new StringBuilder();
        fragment.append( "private $javaType $stringHelper.camel( $columnMetadata.columnName.toLowerCase() );" );
        fragment.append( "\t" );
        fragment.append( "#if( $columnMetadata.remarks != '' )" );
        fragment.append( "// $columnMetadata.remarks" );
        fragment.append( "#end" );
        fragment.append( "\n" );
        Map< String, Object > map     = PluginHelper.getModelFragment( node, context, fragment.toString() );
        String                content = PluginHelper.renderTemplate( map );
        writer.write( content );

        return true;

    }

}
