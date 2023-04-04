package com.tigerobo.x.pai.biz.utils;

public class MdUtil {


    public static String getCurl(String uri,Object data){
        StringBuilder builder = new StringBuilder();
        builder.append("curl --location --request POST '");
        builder.append(uri);
        builder.append("' \\\n");
        builder.append("-H 'Content-Type: application/json' \\\n");
        builder.append("-d '");

        builder.append(data);

        builder.append("'");

        return builder.toString();
    }



    public static String getRestReq(String data,String curl){
        StringBuilder builder = new StringBuilder();

        builder.append("For text input").append("\n");
        builder.append("```json").append("\n");
        builder.append(data);
        builder.append("\n");
        builder.append("```").append("\n");
        builder.append("\n\n");
        builder.append("Execute the request").append("\n");
        builder.append("```bash").append("\n");

        builder.append(curl).append("\n");

        builder.append("```").append("\n");

//        builder.append(curl);

        return builder.toString();
    }

    public static String getPythonReq(String uri,String data){
        StringBuilder builder = new StringBuilder();
        builder.append("```python").append("\n");
        String pythonContent = getPythonContent(uri, data);
        builder.append(pythonContent);
        builder.append("\n");
        builder.append("```").append("\n");


        return builder.toString();
    }
    public static String getPythonContent(String uri,String data){

        StringBuilder builder = new StringBuilder();

        builder.append("import requests").append("\n").append("\n");

        builder.append("uri = \"").append(uri).append("\"\n");


        builder.append("input = ").append(data).append("\n\n");

        builder.append("output = requests.post(uri, json=input).json()").append("\n");

        return builder.toString();
    }

}
