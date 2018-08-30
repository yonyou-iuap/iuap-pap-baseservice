package com.yonyou.iuap.baseservice.attachment.controller;

import com.alibaba.fastjson.JSONObject;
import com.yonyou.iuap.file.FileManager;
import com.yonyou.iuap.file.client.FastdfsClient;
import com.yonyou.iuap.file.utils.BucketPermission;
import com.yonyou.iuap.utils.PropertyUtil;
import com.yonyou.iuap.wb.utils.JsonResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/fileMananger")
public final class FileManageController {
    @RequestMapping(value = "/fastDfs/imgUpload", method = RequestMethod.POST)
    public @ResponseBody
    JsonResponse fastDfsImgUpload(HttpServletRequest request) {
        JsonResponse results = new JsonResponse();
        try {
            List<JSONObject> list = new ArrayList<JSONObject>();
            CommonsMultipartResolver resolver = new CommonsMultipartResolver();
            if (resolver.isMultipart(request)) {
                MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
                int size = multipartRequest.getMultiFileMap().size();
                MultiValueMap<String, MultipartFile> multiValueMap = multipartRequest.getMultiFileMap();
                if(multiValueMap !=null && size > 0){
                    for(MultiValueMap.Entry<String, List<MultipartFile>> me : multiValueMap.entrySet()){
                        List<MultipartFile> multipartFile = me.getValue();
                        for(MultipartFile mult : multipartFile){
                            //local
                            JSONObject obj = new JSONObject();
                            if(PropertyUtil.getPropertyByKey("storeType").equalsIgnoreCase("Local")){
                                String OringinalFilename = mult.getOriginalFilename();
                                String savedFilename = OringinalFilename.substring(OringinalFilename.lastIndexOf("."));
                                String fileName = FileManager.uploadFile(BucketPermission.FULL.toString(), savedFilename, mult.getBytes());
                                obj.put("accessAddress", FileManager.getImgUrl(BucketPermission.READ, fileName, 0));
                                obj.put("fileName", fileName);
                                obj.put("originalFileName", OringinalFilename);
                            }else{
                                String OringinalFilename = mult.getOriginalFilename();
                                FastdfsClient client = FastdfsClient.getInstance();
                                String fileName = client.upload(mult.getBytes());
                                obj.put("fileName", fileName);
                                if (!StringUtils.isNotBlank(fileName)) {
                                    obj.put("accessAddress", null);
                                }
                                obj.put("accessAddress", FileManager.getImgUrl(BucketPermission.READ, fileName, 0));
                                obj.put("originalFileName", OringinalFilename);
                            }
                            list.add(obj);
                        }
                    }
                    results.put("data", list);
                }
            }
            results.success("附件上传成功！");
        } catch (Exception e) {
            results.failed("附件上传失败！");
        }
        return results;
    }
}
