package com.tigerobo.x.pai.api.enums;

import lombok.Getter;

@Getter
public enum ArtImageType {
    DISCO(1,"意象画"),
    STABLE(2,"具象画"),
    ;

    Integer type;
    String text;

    ArtImageType(Integer type, String text) {
        this.type = type;
        this.text = text;
    }


    public static ArtImageType getByName(String name){
        if (name == null||name.isEmpty()){
            return null;
        }
        for (ArtImageType value : values()) {
            if (value.toString().equalsIgnoreCase(name)){
                return value;
            }
        }
        return null;
    }

    public static ArtImageType getByType(Integer type){
        if (type == null){
            return null;
        }
        for (ArtImageType value : values()) {
            if (value.getType().equals(type)){
                return value;
            }
        }
        return null;
    }


}
