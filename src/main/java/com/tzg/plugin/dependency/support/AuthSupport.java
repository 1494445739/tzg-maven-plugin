package com.tzg.plugin.dependency.support;

import java.util.HashMap;
import java.util.Map;

public class AuthSupport {

    public static int AUTH_COMMENT_LENGTH = 5;
    public static Map< String, String > getAuthMap() {

        Map< String, String > map = new HashMap<>();
        map.put( "cas.loginUrl", "http://www.cas.com/cas/login?service=http://localhost:81/shiro-cas" );
        map.put( "cas.failureUrl", "http://www.cas.com/cas/login?service=http://localhost:81/shiro-cas" );
        map.put( "cas.logoutUrl", "http://www.cas.com/cas/logout?service=http://localhost:81" );
        map.put( "cas.serverUrl", "http://www.cas.com/cas" );
        map.put( "cas.clientUrl", "http://localhost:81/shiro-cas" );
        return map;
    }

    public static String getAuthDeclaration() {
        StringBuilder sb = new StringBuilder();
        sb.append( "\n## ---------------------------------------------------------------------------------------- ##" );
        sb.append( "\n##                                                                                          ##" );
        sb.append( "\n## shiro cas sso Configuration:                                                             ##" );
        sb.append( "\n## ---------------------------------------------------------------------------------------- ##" );
        sb.append( "\n" );
        return sb.toString();
    }
}
