package com.yonyou.iuap.baseservice.attachment.entity;

import com.yonyou.iuap.baseservice.entity.AbsDrModel;
import com.yonyou.iuap.baseservice.support.condition.Condition;
import com.yonyou.iuap.baseservice.support.condition.Match;
import com.yonyou.iuap.baseservice.support.generator.GeneratedValue;
import com.yonyou.iuap.baseservice.support.generator.Strategy;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;

@Table(name="base_attachment")
public class AttachmentEntity extends AbsDrModel {

    @Id
    @GeneratedValue(strategy=Strategy.UUID, module="base")
    @Column(name="ID")
    @Condition
    protected String id;        //附件ID
    @Condition(match=Match.LIKE)
    @Column(name="FILENAME")
    private String fileName;    //附件名称
    @Condition(match=Match.LIKE)
    @Column(name="accessAddress")
    private String accessAddress;//附件路径
    @Condition(match=Match.EQ)
    @Column(name="REFID")
    private String refId;       //表单ID
    @Condition(match=Match.LIKE)
    @Column(name="REFNAME")
    private String refName;     //表单名称
    @Condition(match=Match.LIKE)
    @Column(name="ORIGINALFILENAME")
    private String originalFileName;    //原始文件名称

    @Transient
    private String del ;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(Serializable id){//base use
        this.id= id.toString();
        super.id = id;
    }
    public void setId(String id) {
        this.id = id;
    }// mybatis use
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getAccessAddress() {
        return accessAddress;
    }
    public void setAccessAddress(String accessAddress) {
        this.accessAddress = accessAddress;
    }
    public String getRefId() {
        return refId;
    }
    public void setRefId(String refId) {
        this.refId = refId;
    }
    public String getRefName() {
        return refName;
    }
    public void setRefName(String refName) {
        this.refName = refName;
    }
    public String getDel() {
        return del;
    }
    public void setDel(String del) {
        this.del = del;
    }

}