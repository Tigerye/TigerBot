package com.tigerobo.x.pai.engine.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

@RestController
@RequestMapping("/")
public class HeartbeatController {
    @GetMapping(path = "/heartbeat")
    public Boolean heartbeat() {
        return true;
    }


    @GetMapping(path = "read_file")
    public String getSize()throws Exception{
        String path = "/mnt/xpai/test.txt";

        File file = new File(path);

        if (file.exists()){
            String content = "";
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));){
                content = reader.readLine();
            }
            return content;
        }else {
            return "empty";
        }
    }
}
