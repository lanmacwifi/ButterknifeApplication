package com.demo.butterknife_compiler;

import javax.lang.model.type.TypeMirror;

public final class FieldViewBinding {

    private String fieldName;
    private TypeMirror fieldType;
    private int viewId;


    public FieldViewBinding(String fieldName, TypeMirror fieldType, int viewId) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.viewId = viewId;
    }


    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public TypeMirror getFieldType() {
        return fieldType;
    }

    public void setFieldType(TypeMirror fieldType) {
        this.fieldType = fieldType;
    }

    public int getViewId() {
        return viewId;
    }

    public void setViewId(int viewId) {
        this.viewId = viewId;
    }


    @Override
    public String toString() {
        return "{" +
                "fieldType:" + fieldType + ", fieldName:" + fieldName
                + ", viewId:" + viewId
                + "}";
    }
}
