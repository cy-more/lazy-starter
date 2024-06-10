package com.lazy.web.util;

import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.BigExcelWriter;
import cn.hutool.poi.excel.ExcelWriter;
import com.lazy.exception.BizException;
import com.lazy.utils.YsAsserts;
import com.lazy.utils.YsXlsUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author ：cy
 * @description：Web-excel
 * @date ：2022/6/8 19:24
 */
@Slf4j
public class YsWebXlsUtil {

    private static final String ERROR_MSG = "xls解析失败";

    /**
     * 导出
     * @param response
     * @param headList
     * @param importList
     * @param sheetName
     */
    public static void exportXls(HttpServletResponse response, List<String> headList, List<?> importList, String sheetName){
        // 通过工具类创建writer，默认创建xls格式
        BigExcelWriter writer = new BigExcelWriter(new SXSSFWorkbook(), sheetName);
        // 一次性写出内容，使用默认样式，强制输出标题
        if (headList != null) {
            writer.write(Collections.singletonList(headList), true);
        }
        writer.write(importList);
        //out为OutputStream，需要写出到的目标流

        //设置默认宽
        writer.getSheet().setDefaultColumnWidth(15);
        exportXls(response, writer);
    }

    public static void exportXls(HttpServletResponse response, ExcelWriter writer){
        //response为HttpServletResponse对象
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        //download.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
        response.setHeader("Content-Disposition","attachment;filename=download.xls");
        ServletOutputStream out= null;
        try {
            out = response.getOutputStream();
            writer.flush(out, true);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            // 关闭writer，释放内存
            writer.close();
            //此处记得关闭输出Servlet流
            IoUtil.close(out);
        }
    }

    /**
     * 默认根据注解head导出
     * 优先级：
     * 1.YsXlsHead
     * 2.ApiModelProperty
     * @param response
     * @param importList
     * @param sheetName
     */
    public static void exportXls(HttpServletResponse response, List<?> importList, String sheetName){
        if (CollectionUtils.isEmpty(importList)){
            return;
        }
        exportXls(response, YsXlsUtil.getExportXlsHeader(importList), importList, sheetName);
    }

    public static void exportXls(HttpServletResponse response, List<?> importList) {
        exportXls(response, importList, "sheet1");
    }

    public static <T> List<T> importXls(Class<T> clazz, MultipartFile file) {
        YsAsserts.isTrue(file == null || file.isEmpty(), "未导入excel文件");
        try {
            return YsXlsUtil.importXls(clazz, file.getInputStream());
        } catch (IOException e) {
            log.error(ERROR_MSG, e);
            throw new BizException(ERROR_MSG + "，error:" + e.getMessage());
        }
    }
}
