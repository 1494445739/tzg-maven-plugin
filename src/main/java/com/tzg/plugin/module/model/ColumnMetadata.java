package com.tzg.plugin.module.model;

public class ColumnMetadata {

    private String columnName;

    private String typeName;

    private String remarks;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName( String columnName ) {
        this.columnName = columnName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName( String typeName ) {
        this.typeName = typeName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks( String remarks ) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "ColumnMetadata{" +
                "columnName='" + columnName + '\'' +
                ", typeName='" + typeName + '\'' +
                ", remarks='" + remarks + '\'' +
                '}';
    }

}
