package com.yonyou.iuap.baseservice.ref.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * 从uitemplate_common移入,解决发版时的依赖问题,后续可能切换为其他ref-sdk
 * @author leon
 * */
@JsonSerialize(
        include = JsonSerialize.Inclusion.NON_DEFAULT
)
public class RefViewModelVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String appcode;
    private String providerid;
    private String serviceinterface;
    private String tenantId;
    private String sysId;
    private String[] refShowClassCode;
    private String[] refShowClassName;
    private RefUITypeEnum refUIType;
    private String refCode;
    private int defaultFieldCount = 2;
    private String condition;
    private String[] strFieldCode;
    private String[] strFieldName;
    private String[] strHiddenFieldCode;
    private String[] refCodeNamePK;
    private boolean isUseDataPower = true;
    private boolean isMultiSelectedEnabled = false;
    private boolean isNotLeafSelected = true;
    private boolean isCheckListEnabled = false;
    private boolean isZtreeStyle = true;
    private String rootName;
    private String dataPowerOperation_Code = null;
    private boolean isIncludeSub;
    private boolean isDisabledDataShow;
    private boolean isReturnCode;
    private String pk_group;
    private String pk_org;
    private String pk_user;
    private boolean isMatchPkWithWherePart = true;
    private String[] pk_val;
    private boolean isClassDoc = false;
    private String[] filterPks;
    private String clientParam;
    private String cfgParam;
    private String content;
    private String refModelClassName;
    private String refModelUrl;
    private String refModelHandlerClass;
    private String refName;
    private String pks;
    private String id;
    private boolean isTreeAsync = false;
    private String transmitParam;
    private String treeNode;
    private boolean treeloadData = false;
    private RefClientPageInfo refClientPageInfo = new RefClientPageInfo();

    public RefViewModelVO() {
    }

    public String getTreeNode() {
        return this.treeNode;
    }

    public void setTreeNode(String treeNode) {
        this.treeNode = treeNode;
    }

    public boolean isTreeloadData() {
        return this.treeloadData;
    }

    public boolean getTreeloadData() {
        return this.treeloadData;
    }

    public void setTreeloadData(boolean treeloadData) {
        this.treeloadData = treeloadData;
    }

    public int getDefaultFieldCount() {
        return this.defaultFieldCount;
    }

    public void setDefaultFieldCount(int defaultFieldCount) {
        this.defaultFieldCount = defaultFieldCount;
    }

    public boolean isIsTreeAsync() {
        return this.isTreeAsync;
    }

    public boolean getIsTreeAsync() {
        return this.isTreeAsync;
    }

    public void setIsTreeAsync(boolean isTreeAsync) {
        this.isTreeAsync = isTreeAsync;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String[] getRefCodeNamePK() {
        return this.refCodeNamePK;
    }

    public void setRefCodeNamePK(String[] refCodeNamePK) {
        this.refCodeNamePK = refCodeNamePK;
    }

    public boolean isIsMatchPkWithWherePart() {
        return this.isMatchPkWithWherePart;
    }

    public boolean getIsMatchPkWithWherePart() {
        return this.isMatchPkWithWherePart;
    }

    public void setIsMatchPkWithWherePart(boolean isMatchPkWithWherePart) {
        this.isMatchPkWithWherePart = isMatchPkWithWherePart;
    }

    public String getCfgParam() {
        return this.cfgParam;
    }

    public void setCfgParam(String cfgParam) {
        this.cfgParam = cfgParam;
    }

    public String getClientParam() {
        return this.clientParam;
    }

    public void setClientParam(String clientParam) {
        this.clientParam = clientParam;
    }

    public String[] getStrFieldCode() {
        return this.strFieldCode == null ? null : this.strFieldCode;
    }

    public void setStrFieldCode(String[] strFieldCode) {
        this.strFieldCode = strFieldCode;
    }

    public String[] getStrFieldName() {
        return this.strFieldName;
    }

    public void setStrFieldName(String[] strFieldName) {
        this.strFieldName = strFieldName;
    }

    public String[] getStrHiddenFieldCode() {
        return this.strHiddenFieldCode;
    }

    public void setStrHiddenFieldCode(String[] strHiddenFieldCode) {
        this.strHiddenFieldCode = strHiddenFieldCode;
    }

    public boolean isIsClassDoc() {
        return this.isClassDoc;
    }

    public boolean getIsClassDoc() {
        return this.isClassDoc;
    }

    public void setIsClassDoc(boolean isClassDoc) {
        this.isClassDoc = isClassDoc;
    }

    public String getRootName() {
        return this.rootName;
    }

    public void setRootName(String rootName) {
        this.rootName = rootName;
    }

    public boolean isIsUseDataPower() {
        return this.isUseDataPower;
    }

    public void setIsUseDataPower(boolean useDataPower) {
        this.isUseDataPower = useDataPower;
    }

    public boolean isIsMultiSelectedEnabled() {
        return this.isMultiSelectedEnabled;
    }

    public boolean getIsMultiSelectedEnabled() {
        return this.isMultiSelectedEnabled;
    }

    public void setIsMultiSelectedEnabled(boolean isMultiSelectedEnabled) {
        this.isMultiSelectedEnabled = isMultiSelectedEnabled;
    }

    public boolean isIsNotLeafSelected() {
        return this.isNotLeafSelected;
    }

    public boolean getIsNotLeafSelected() {
        return this.isNotLeafSelected;
    }

    public void setIsNotLeafSelected(boolean isNotLeafSelected) {
        this.isNotLeafSelected = isNotLeafSelected;
    }

    public String getDataPowerOperation_Code() {
        return this.dataPowerOperation_Code;
    }

    public void setDataPowerOperation_Code(String dataPowerOperation_Code) {
        this.dataPowerOperation_Code = dataPowerOperation_Code;
    }

    public boolean isIsReturnCode() {
        return this.isReturnCode;
    }

    public boolean getIsReturnCode() {
        return this.isReturnCode;
    }

    public void setIsReturnCode(boolean isReturnCode) {
        this.isReturnCode = isReturnCode;
    }

    public String[] getFilterPks() {
        if (null != this.filterPks && this.filterPks.length == 0) {
            this.filterPks = null;
            return this.filterPks;
        } else {
            return this.filterPks;
        }
    }

    public void setFilterPks(String[] filterPks) {
        this.filterPks = filterPks;
    }

    public String[] getPk_val() {
        return this.pk_val;
    }

    public void setPk_val(String[] pk_val) {
        this.pk_val = pk_val;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        if (null != content) {
            this.content = this.decoding(content);
        }

    }

    public String getPk_group() {
        if (StringUtils.isEmpty(this.pk_group)) {
            ;
        }

        return this.pk_group;
    }

    public void setPk_group(String pk_group) {
        this.pk_group = pk_group;
    }

    public String getPk_org() {
        return this.pk_org;
    }

    public void setPk_org(String pk_org) {
        this.pk_org = pk_org;
    }

    public String getPk_user() {
        if (StringUtils.isEmpty(this.pk_user)) {
            ;
        }

        return this.pk_user;
    }

    public void setPk_user(String pk_user) {
        this.pk_user = pk_user;
    }

    public boolean isIsIncludeSub() {
        return this.isIncludeSub;
    }

    public boolean getIsIncludeSub() {
        return this.isIncludeSub;
    }

    public void setIsIncludeSub(boolean isIncludeSub) {
        this.isIncludeSub = isIncludeSub;
    }

    public boolean isIsDisabledDataShow() {
        return this.isDisabledDataShow;
    }

    public boolean getIsDisabledDataShow() {
        return this.isDisabledDataShow;
    }

    public void setIsDisabledDataShow(boolean isDisabledDataShow) {
        this.isDisabledDataShow = isDisabledDataShow;
    }

    private String decoding(String param) {
        try {
            return URLDecoder.decode(param, "utf-8");
        } catch (UnsupportedEncodingException var3) {
            var3.printStackTrace();
            return null;
        }
    }

    public  RefUITypeEnum getRefUIType() {
        return this.refUIType;
    }

    public void setRefUIType(RefUITypeEnum refUIType) {
        this.refUIType = refUIType;
    }

    public String getRefModelClassName() {
        return this.refModelClassName;
    }

    public void setRefModelClassName(String refModelClassName) {
        this.refModelClassName = refModelClassName;
    }

    public String getRefName() {
        return this.refName;
    }

    public void setRefName(String refModelName) {
        this.setRefName(refModelName, true);
    }

    public void setRefName(String refModelName, boolean needDecode) {
        if (refModelName != null && needDecode) {
            refModelName = this.decoding(refModelName);
            if (refModelName.indexOf(44) != -1) {
                refModelName = refModelName.substring(0, refModelName.indexOf(44));
            }
        }

        this.refName = refModelName;
    }

    public RefClientPageInfo getRefClientPageInfo() {
        return this.refClientPageInfo;
    }

    public void setRefClientPageInfo(RefClientPageInfo refClientPageInfo) {
        this.refClientPageInfo = refClientPageInfo;
    }

    public String getPks() {
        return this.pks;
    }

    public void setPks(String pks) {
        this.pks = pks;
    }

    public String getRefModelHandlerClass() {
        return this.refModelHandlerClass;
    }

    public void setRefModelHandlerClass(String refModelHandlerClass) {
        this.refModelHandlerClass = refModelHandlerClass;
    }

    public boolean isIsCheckListEnabled() {
        return this.isCheckListEnabled;
    }

    public boolean getIsCheckListEnabled() {
        return this.isCheckListEnabled;
    }

    @JsonIgnore
    public void setCheckListEnabled(boolean isCheckListEnabled) {
        this.isCheckListEnabled = isCheckListEnabled;
        this.setIsCheckListEnabled(isCheckListEnabled);
    }

    public void setIsCheckListEnabled(boolean isCheckListEnabled) {
        this.isCheckListEnabled = isCheckListEnabled;
    }

    public boolean isZtreeStyle() {
        return this.isZtreeStyle;
    }

    public boolean getIsZtreeStyle() {
        return this.isZtreeStyle;
    }

    @JsonIgnore
    public void setZtreeStyle(boolean isZtreeStyle) {
        this.isZtreeStyle = isZtreeStyle;
        this.setIsZtreeStyle(isZtreeStyle);
    }

    public void setIsZtreeStyle(boolean isZtreeStyle) {
        this.isZtreeStyle = isZtreeStyle;
    }

    public String getRefCode() {
        return this.refCode;
    }

    public void setRefCode(String refCode) {
        this.refCode = refCode;
    }

    public String getRefModelUrl() {
        return this.refModelUrl;
    }

    public void setRefModelUrl(String refModelUrl) {
        this.refModelUrl = refModelUrl;
    }

    public String getCondition() {
        return this.condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getTransmitParam() {
        return this.transmitParam;
    }

    public void setTransmitParam(String transmitParam) {
        this.transmitParam = transmitParam;
    }

    public String getAppcode() {
        return this.appcode;
    }

    public void setAppcode(String appcode) {
        this.appcode = appcode;
    }

    public String getProviderid() {
        return this.providerid;
    }

    public void setProviderid(String providerid) {
        this.providerid = providerid;
    }

    public String getServiceinterface() {
        return this.serviceinterface;
    }

    public void setServiceinterface(String serviceinterface) {
        this.serviceinterface = serviceinterface;
    }

    public String getTenantId() {
        return this.tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getSysId() {
        return this.sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }

    public String[] getRefShowClassCode() {
        return this.refShowClassCode;
    }

    public void setRefShowClassCode(String[] refShowClassCode) {
        this.refShowClassCode = refShowClassCode;
    }

    public String[] getRefShowClassName() {
        return this.refShowClassName;
    }

    public void setRefShowClassName(String[] refShowClassName) {
        this.refShowClassName = refShowClassName;
    }
}