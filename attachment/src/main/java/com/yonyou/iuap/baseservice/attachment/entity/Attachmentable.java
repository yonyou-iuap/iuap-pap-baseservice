package com.yonyou.iuap.baseservice.attachment.entity;

import com.yonyou.iuap.baseservice.entity.Model;

import java.util.List;

public interface Attachmentable extends   Model {

    public List<AttachmentEntity> getAttachment() ;

    public void setAttachment(List<AttachmentEntity> attachment);

}
