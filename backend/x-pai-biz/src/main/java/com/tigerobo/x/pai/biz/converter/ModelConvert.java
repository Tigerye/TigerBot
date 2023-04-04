package com.tigerobo.x.pai.biz.converter;

import com.tigerobo.x.pai.api.auth.entity.Group;
import com.tigerobo.x.pai.api.biz.entity.Model;
import com.tigerobo.x.pai.biz.utils.IdGenerator;
import com.tigerobo.x.pai.dal.biz.entity.ModelDo;

public class ModelConvert {
    public static Model convert(ModelDo modelDo) {
        if (modelDo == null) {
            return null;
        }
        Group group = new Group();
        group.setId(modelDo.getId());
        group.setUuid(modelDo.getUuid());

        return Model.builder()
                .id(modelDo.getId())
                .createTime(modelDo.getCreateTime())
                .updateTime(modelDo.getUpdateTime())
                .isDeleted(modelDo.getIsDeleted())
                .uuid(modelDo.getUuid())
                .name(modelDo.getName())
                .intro(modelDo.getIntro())
                .desc(modelDo.getDesc())
                .image(modelDo.getImage())
//                .limited(Limited.valueOf(modelDo.getLimited()))
//                .repoAddr(modelDo.getRepoAddr())
                .apiUri(modelDo.getApiUri())
                .style(modelDo.getStyle())
//                .status(Model.Status.valueOf(modelDo.getStatus()))
//                .subject(Model.Subject.valueOf(modelDo.getSubject()))
                .group(group)
                .groupUuid(modelDo.getGroupUuid())
                .createBy(modelDo.getCreateBy())
                .build();
    }

    public static ModelDo convert2po(Model model) {
        if (model == null){
            return null;
        }
        String uuid = model.getUuid();
        if (uuid == null){
            uuid = IdGenerator.getId();
        }

        Group group = model.getGroup() == null?new Group():model.getGroup();
        return ModelDo.builder()
                .id(model.getId())
                .uuid(uuid)
                .isDeleted(model.getIsDeleted())
                .name(model.getName())
                .intro(model.getIntro())
                .desc(model.getDesc())
//                .nameEn(model.getNameEn() != null ? model.getNameEn() : model.getName())
//                .introEn(model.getIntroEn())
//                .descEn(model.getDescEn())
                .image(model.getImage())
//                .limited(model.getLimited() != null ? model.getLimited().getVal() : 0)
//                .repoAddr(model.getRepoAddr())
                .apiUri(model.getApiUri())
                .style(model.getStyle())
//                .status(model.getStatus().getVal())
//                .subject(model.getSubject() != null ? model.getSubject().getVal() : 0)
                .groupId(group.getId())
                .groupUuid(group.getUuid())
                .build();
    }
}
