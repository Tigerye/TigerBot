package com.tigerobo.x.pai.biz.serving;

import com.tigerobo.x.pai.biz.batch.offline.BatchContext;

import java.io.File;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description:
 * @modified By:
 * @version: $
 */
public interface Evaluable {
    default Object evaluate(File inputFile, File outputFile){return null;};
    default Object evaluate(BatchContext context){return null;};
//    Object evaluate(InputStream inputStream, File outputFile);

}
