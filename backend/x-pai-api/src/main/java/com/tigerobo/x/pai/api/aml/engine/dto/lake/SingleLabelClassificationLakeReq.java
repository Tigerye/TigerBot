package com.tigerobo.x.pai.api.aml.engine.dto.lake;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SingleLabelClassificationLakeReq {

    String task = "single_label_classification";
    String model_name;
//    String model_name_or_path = "bert-base-chinese";//hfl/rbt3
    String model_name_or_path;
    String train_file;
    String validation_file;
    String test_file;
    String output_dir;
    int num_train_epochs = 5;
    int max_seq_length = 512;
    boolean overwrite_output_dir = true;

    String model_area;
}
