package com.tzg.plugin.module.model;

import java.util.List;

public class Parameter {

    private String                 project;
    private String                 module;
    private String                 table;
    private List< ColumnMetadata > columnMetadataList;

    public Parameter( String project, String module, String table, List< ColumnMetadata > columnMetadataList ) {
        this.project = project;
        this.module = module;
        this.table = table;
        this.columnMetadataList = columnMetadataList;
    }

    public String getProject() {
        return project;
    }

    public String getModule() {
        return module;
    }

    public String getTable() {
        return table;
    }

    public List< ColumnMetadata > getColumnMetadataList() {
        return columnMetadataList;
    }

    @Override
    public String toString() {
        return "Parameter{" +
                "project='" + project + '\'' +
                ", module='" + module + '\'' +
                ", table='" + table + '\'' +
                ", columnMetadataList=" + columnMetadataList +
                '}';
    }

}
