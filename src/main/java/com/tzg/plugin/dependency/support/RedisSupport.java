package com.tzg.plugin.dependency.support;

import com.tzg.plugin.module.support.ModuleSupport;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class RedisSupport {

    public static int REDIS_COMMENT_LENGTH = 12;

    public static Map< String, String > getRedisMap() {

        Map< String, String > map = new HashMap<>();
        map.put( "redis.host", "127.0.0.1" );
        map.put( "redis.port", "6379" );
        map.put( "redis.password", "" );
        map.put( "redis.maxIdle", "5000" );
        map.put( "redis.maxTotal", "5000" );
        map.put( "redis.maxWaitMillis", "10000" );
        map.put( "redis.testOnBorrow", "true" );

        return map;
    }

    public static void genRedisDBModule() throws IOException {

        String[] templates = getTemplates();
        String[] files     = getFiles();

        for ( int i = 0; i < files.length; i++ ) {
            DependencySupport.mergeTemplate( files[ i ], templates[ i ] );
        }

    }

    public static String[] getTemplates() {
        String vmJavaDir = "dependency/redis/";

        return new String[]{
                vmJavaDir + "model.vm",
                vmJavaDir + "controller.vm"
        };
    }

    public static String[] getFiles() {
        String modelFile = "Foo.java";
        String ctrlFile  = "FooController.java";

        String javaDir = getRedisModulePath();

        return new String[]{
                javaDir + modelFile,
                javaDir + ctrlFile
        };
    }

    public static String getRedisModulePath() {
        return DependencySupport.getRootPath() + "/src/main/java/com/tzg/web/project/redis/".replaceAll( "project", ModuleSupport.getCurrentProjectName() );
    }

    public static String getRedisDeclaration() {

        StringBuilder sb = new StringBuilder();
        sb.append( "\n## ---------------------------------------------------------------------------------------- ##" );
        sb.append( "\n##                                                                                          ##" );
        sb.append( "\n## redis config:                                                                            ##" );
        sb.append( "\n##                                                                                          ##" );
        sb.append( "\n## redis.maxIdle:      最大空闲时间. 超过则数据库连接被标记为不可用, 然后被释放. 设为0表示无限制    ##" );
        sb.append( "\n## redis.maxTotal:     最大建立连接等待时间. 如果超过此时间将接到异常. 设为-1表示无限制            ##" );
        sb.append( "\n## redis.maxWaitMillis:连接池的最大数据库连接数. 设为0表示无限制                                 ##" );
        sb.append( "\n## redis.testOnBorrow: 在borrow一个jedis实例时, 是否提前进行validate操作.                      ##" );
        sb.append( "\n##                     如果为true, 则得到的jedis实例均是可用的                                 ##" );
        sb.append( "\n##                                                                                          ##" );
        sb.append( "\n## ---------------------------------------------------------------------------------------- ##" );
        sb.append( "\n" );
        return sb.toString();
    }

}
