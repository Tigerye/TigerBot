package com.tigerobo.x.pai.engine.auto.ml.operator.cmd;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.C;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class CmdOperator {
    private static CmdOperator operator = new CmdOperator();
    public static CmdOperator getInstance(){
        return operator;
    }
    private long MAX_TIMEOUT_SECONDS = 5*60;


    public String runModelQa(String runScriptPath,String modelPath)throws Exception{
        StringBuilder cmd = new StringBuilder();
        cmd.append("bash").append(" ");
        cmd.append(runScriptPath).append(" ");
        cmd.append(modelPath);

        return run(cmd.toString());
    }


    public void chmod(String path){

        StringBuilder builder = new StringBuilder();

        builder.append("chmod -R 777 ").append(path);

        String cmd = builder.toString();
        try {
            long start = System.currentTimeMillis();
            run(cmd, 10, TimeUnit.SECONDS);
            log.info("chmod delta:{}ms,path:{}",(System.currentTimeMillis()-start),path);
        }catch (Exception ex){
            log.error("cmd:{}",cmd,ex);
        }

    }

    public String run(String cmd)throws Exception{
        return run(cmd,MAX_TIMEOUT_SECONDS,TimeUnit.SECONDS);
    }
    public String run(String cmd,long timeout,TimeUnit timeUnit)throws Exception{
        log.info("cmd:{}",cmd);
        Process proc = Runtime.getRuntime().exec(cmd);
        List<String> outputList = Stream.of(proc.getInputStream(), proc.getErrorStream()).parallel().map(
                this::readStream).collect(Collectors.toList());
        if (proc.isAlive()){
            proc.destroy();
            if (!proc.waitFor(timeout, timeUnit)){
                log.error("convert timeout. try to force-kill.");
                proc.destroyForcibly();
            }
        }
        if (outputList.size()>0){

            if (outputList.size()>1){
                String errorOut = outputList.get(1);
                if (StringUtils.isNotBlank(errorOut)){
                    log.error("cmd error:{},cmd:{}",errorOut,cmd);
                }

            }
            String info = outputList.get(0);
            return info;
        }
        return null;
    }
    private String readStream(InputStream is) {
        String output = null;
        try {
            output = IOUtils.toString(is, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Error: ", e);
        }
        return output;
    }
}
