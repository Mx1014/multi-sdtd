package com.rzt.utils;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StorageUtils {

    private static String fileDir = null;
    private static String picDir = null;
    private static String thumHeight = null;
    private static String thumWidth = null;

    /**
     * 图片缩放
     * @param file    原图文件
     * @param targetFile   缩放图路径
     * @param height 高度
     * @param width  宽度
     */
    public static boolean resize(MultipartFile file, String targetFile, int height, int width) throws IOException {
        boolean bol = false; //是否进行了压缩
        String filename = file.getOriginalFilename();
        String pictype="";
        if(!file.isEmpty()){
            pictype = filename.substring(filename.lastIndexOf(".")+1,filename.length());
        }
        double ratio = 0; //缩放比例
        InputStream o = file.getInputStream();
        File d = new File(targetFile);
        BufferedImage bi;

        bi = ImageIO.read(o);
        Image itemp = bi.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH);
        int itempWidth = bi.getWidth();
        int itempHeight = bi.getHeight();

        //计算比例
        if ((itempHeight > height) || (itempWidth > width)) {
            ratio = Math.min((new Integer(height)).doubleValue() / itempHeight, (new Integer(width)).doubleValue() / itempWidth);
            AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(ratio, ratio), null);
            itemp = op.filter(bi, null);
            ImageIO.write((BufferedImage) itemp,pictype, d);
            bol = true;
        }

        o.close();
        return bol;
    }

    /**
     * 保存原图并缩略保存缩略图
     * @param file    原图文件
     * @param targetDir   图片存放文件夹名称(原图路径：配置路径+日期+targetDir，缩略图在targetDir下的thum文件夹里)
     */
    public static Map<String, Object> resizeDefault(MultipartFile file, String targetDir) throws IOException {

        Map<String, Object> result = new HashMap<>();

        if(file.isEmpty()){
            throw new IOException("上传文件为空！");
        }
        //String fileName = file.getOriginalFilename();
        String fileName = UUID.randomUUID()+".jpg";//todo
        String picType = fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());
        String picName = fileName.substring(0,fileName.lastIndexOf("."));
        if(!"jpg".equals(picType)){
            throw new IOException("上传文件不是jpg图片！");
        }
        double ratio = 0; //缩放比例
        InputStream o = file.getInputStream();
        String currentDate = DateUtil.getCurrentDate();
        StringBuilder baseDir = new StringBuilder();
        baseDir.append(picDir).append(currentDate).append(File.separator).append(targetDir).append(File.separator);

        StringBuilder targetPath = new StringBuilder();
        targetPath.append(baseDir).append(picName).append(".").append(picType);

        StringBuilder thumPath = new StringBuilder();
        thumPath.append(baseDir).append("thum").append(File.separator).append(picName).append(thumHeight).append("x").append(thumWidth).append(".").append(picType);

        //原文件存放路径
        File targetFile = new File(targetPath.toString());
        //缩略图存放路径
        File thumFile = new File(thumPath.toString());
        //目标目录是否存在
        File folder = new File(baseDir + "thum");
        if(!folder.exists()&&!folder.isDirectory()){
            folder.mkdirs();
        }

//        int width = Integer.parseInt(thumWidth);
//        int height = Integer.parseInt(thumHeight);
//        BufferedImage bi;
//        bi = ImageIO.read(o);
//
//        Image itemp = bi.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH);
//        int itempWidth = bi.getWidth();
//        int itempHeight = bi.getHeight();
//
//        //计算比例
//        if ((itempHeight > height) || (itempWidth > width)) {
//            ratio = Math.min((new Integer(height)).doubleValue() / itempHeight, (new Integer(width)).doubleValue() / itempWidth);
//            AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(ratio, ratio), null);
//            itemp = op.filter(bi, null);
//            ImageIO.write((BufferedImage) itemp,picType, thumFile);
//        }
        o.close();
        //保存原图到指定路径
        file.transferTo(targetFile);
        StringBuilder picUrl = new StringBuilder();
        picUrl.append(File.separator).append(currentDate).append(File.separator).append(targetDir).append(File.separator).append(picName).append(".").append(picType);

        StringBuilder thumUrl = new StringBuilder();
        thumUrl.append(File.separator).append(currentDate).append(File.separator).append(targetDir).append(File.separator).append("thum").append(File.separator).append(picName).append(thumHeight).append("x").append(thumWidth).append(".").append(picType);

        result.put("picName",picName+"."+picType);
        result.put("picPath",picUrl);
        result.put("thumPath",thumUrl);
        result.put("success",true);

        return result;

    }

    /**
     * 存储文件
     * @param file    文件
     */
    public static Map<String, Object> storageFiles(MultipartFile file) throws IOException {

        Map<String, Object> result = new HashMap<>();

        if(file.isEmpty()){
            throw new IOException("上传文件为空！");
        }
        String fileName = file.getOriginalFilename();
        String fileType="";
        fileType = fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());


        String saveName = String.valueOf(UUID.randomUUID());
        String baseDir = fileDir + File.separator + fileType + File.separator;
        String targetPath = baseDir+saveName+"."+fileType;

        //原文件存放路径
        File targetFile = new File(targetPath);
        //目标目录是否存在
        File folder = new File(baseDir);
        if(!folder.exists()&&!folder.isDirectory()){
            folder.mkdirs();
        }

        //保存原图到指定路径
        file.transferTo(targetFile);
        String url = File.separator + fileType + File.separator + saveName+"."+fileType;
        result.put("fileName",saveName+"."+fileType);
        result.put("filePath",url);
        result.put("success",true);

        return result;

    }

    /**
     * 存储文件
     * @param file    文件
     */
    public static Map<String, Object> storageFilesByDay(MultipartFile file) throws IOException {

        Map<String, Object> result = new HashMap<>();

        if(file.isEmpty()){
            throw new IOException("上传文件为空！");
        }
        String fileName = file.getOriginalFilename();
        String fileType = fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());

        String currentDate = DateUtil.getCurrentDate();
        String saveName = String.valueOf(UUID.randomUUID());

        StringBuilder baseDir = new StringBuilder();
        baseDir.append(picDir).append(currentDate).append(File.separator).append(fileType).append(File.separator);
        StringBuilder targetPath = new StringBuilder();
        targetPath.append(baseDir).append(saveName).append(".").append(fileType);

        //原文件存放路径
        File targetFile = new File(targetPath.toString());
        //目标目录是否存在
        File folder = new File(baseDir.toString());
        if(!folder.exists()&&!folder.isDirectory()){
            folder.mkdirs();
        }

        //保存原图到指定路径
        file.transferTo(targetFile);
        StringBuilder url = new StringBuilder();
        url.append(File.separator).append(currentDate).append(File.separator).append(fileType).append(File.separator).append(saveName).append(".").append(fileType);
        result.put("fileName",saveName+"."+fileType);
        result.put("filePath",url);
        result.put("success",true);

        return result;

    }


    /**
     * 删除图片
     * @param suffixPath
     */
    public static boolean deleteImg(String suffixPath) {

        String deltePath = picDir + suffixPath;
        File file = new File(deltePath);
        return file.delete();

    }


    //设定配置参数
    static{

        if(fileDir==null){
            fileDir = YmlConfigUtil.getConfigByKey("file-dir");
        }
        if(picDir==null){
            picDir = YmlConfigUtil.getConfigByKey("pic-dir");
        }
        if(thumHeight==null){
            thumHeight = YmlConfigUtil.getConfigByKey("thum-height");
        }
        if(thumWidth==null){
            thumWidth = YmlConfigUtil.getConfigByKey("thum-width");
        }
    }


    public static void main(String[] args) throws IOException{

        //pressImage("D:\\images\\444.jpg", "D:\\images\\wmlogo.gif", 100, 50, 0.5f);
//        pressText("D:\\\\images\\\\444.jpg", "旺仔之印", "宋体", Font.BOLD|Font.ITALIC, 20, Color.red, 50, 50,.8f);
        //resizeWidth("c:\\test\\VIP3.png","c:\\test\\VIP3_1.png", 90, 245);
        //resize("E:\\testdata\\1.jpg","E:\\testdata\\2.jpg", 200, 200);

        //String targetPath = "111.jpg".substring(0,"111.jpg".indexOf(".jpg"));
        //System.out.println(targetPath);
    }
}
