package com.lazy.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.sax.handler.MapRowHandler;
import com.lazy.exception.BizException;
import com.lazy.exception.ResultCode;
import com.lazy.utils.support.XlsHeader;
import com.lazy.utils.support.YsRowHandler;
import com.lazy.utils.support.YsXlsHead;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.*;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ：cy
 * @description：Xls操作
 * @date ：2021/9/26 13:53
 */
@Slf4j
public class YsXlsUtil {

    private static final String ERROR_MSG = "xls解析失败";

    /**
     * 获取导出xls头
     * @param importList
     * @return
     */
    public static List<String> getExportXlsHeader(List<?> importList){
        return getExportXlsHeader(importList.get(0).getClass());
    }

    public static List<String> getExportXlsHeader(Class<?> clazz){
        return getExportXlsHeader(clazz.getDeclaredFields());
    }

    public static List<String> getExportXlsHeader(Field[] fields){
        List<String> headList = new LinkedList<>();

        for (Field field : fields) {
            field.setAccessible(true);
            //解析注解
            YsXlsHead xlsHead = field.getDeclaredAnnotation(YsXlsHead.class);
            if (ObjectUtil.isNotEmpty(xlsHead)){
                headList.add(xlsHead.value());
                continue;
            }

            //解析注解
            ApiModelProperty apiHead = field.getDeclaredAnnotation(ApiModelProperty.class);
            if (ObjectUtil.isNotEmpty(apiHead)){
                headList.add(apiHead.value());
            }
        }

        return headList;
    }

    /**
     * xls解析
     * @param clazz
     * @param inputStream
     * @param <T>
     * @return
     */
    public static <T> List<T> importXls(Class<T> clazz, InputStream inputStream){
        //获取数据
        YsAsserts.isNull(inputStream, "未导入excel文件");
        List<Map<String, Object>> maps = null;
        maps = YsXlsUtil.importXlsToMap(inputStream);
        YsAsserts.isNull(maps, "数据为空或xls不正确");

        return importXls(clazz, maps);
    }

    /**
     * xls数据转 bean
     * @param clazz
     * @param maps
     * @param <T>
     * @return
     */
    public static <T> List<T> importXls(Class<T> clazz, List<Map<String, Object>> maps){
        //解析需要转换的字段
        ArrayList<XlsHeader> headList = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String name = field.getName();
            Class<?> fieldType = field.getType();
            //解析注解
            YsXlsHead xlsHead = field.getDeclaredAnnotation(YsXlsHead.class);
            if (ObjectUtil.isNotEmpty(xlsHead)){
                headList.add(new XlsHeader(name, fieldType, xlsHead.value(), xlsHead.isCheck()));
                continue;
            }

            //解析注解(默认不检测null)
            ApiModelProperty apiHead = field.getDeclaredAnnotation(ApiModelProperty.class);
            if (ObjectUtil.isNotEmpty(apiHead)){
                headList.add(new XlsHeader(name, fieldType, apiHead.value(), false));
            }
        }

        //数据转换
        List<T> result = new ArrayList<>();
        for (Map<String, Object> entry : maps) {
            Map<String, Object> resultEntityMap = new HashMap<>(headList.size());
            for (XlsHeader header : headList) {
                Object valObj = entry.get(header.getValue());
                if (header.isCheck() && ObjectUtil.isEmpty(valObj)){
                    throw new BizException(ERROR_MSG + "，error: 列[" + header.getKeyName() + "]不能为空");
                }
                resultEntityMap.put(header.getKeyName(), Convert.convert(header.getKeyType(), valObj));
            }
            result.add(BeanUtil.toBean(resultEntityMap, clazz));
        }
        return result;
    }

    /**
     * xls图片写入
     * @param writer
     * @param x           单元格x轴坐标
     * @param y           单元格y轴坐标
     * @param pictureData 图片二进制数据
     * @param picType     图片格式
     */
    public static void writePic(ExcelWriter writer, int x, int y, byte[] pictureData, int picType) {
        Sheet sheet = writer.getSheet();
        Drawing drawingPatriarch = sheet.createDrawingPatriarch();

        //设置图片单元格位置
        ClientAnchor anchor = drawingPatriarch.createAnchor(0, 0, 0, 0, x, y, x + 1, y + 1);
        //随单元格改变位置和大小
        anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);

        //添加图片
        int pictureIndex = sheet.getWorkbook().addPicture(pictureData, picType);
        drawingPatriarch.createPicture(anchor, pictureIndex);
    }

    /**
     * xls解析
     * @param inputStream
     * @return
     */
    public static List<Map<String, Object>> importXlsToMap(InputStream inputStream){
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        return reader.readAll();
    }

    /**
     * 获取Excel中的图片
     * @param xssfSheet
     * @return
     */
    public static Map<String, XSSFPictureData> getPictures(XSSFSheet xssfSheet){
        XSSFDrawing drawingPatriarch = xssfSheet.getDrawingPatriarch();
        if (drawingPatriarch == null){
            return Collections.emptyMap();
        }
        List<XSSFShape> list = drawingPatriarch.getShapes();
        Map<String,XSSFPictureData> map = new HashMap<>(list.size());

        for (XSSFShape shape : list){
            XSSFPicture picture = (XSSFPicture) shape;
            XSSFClientAnchor xssfClientAnchor=(XSSFClientAnchor) picture.getAnchor();
            XSSFPictureData pdata = picture.getPictureData();
            // 行号-列号
            String key = xssfClientAnchor.getRow1() + "-" + xssfClientAnchor.getCol1();
            log.info("key数据:{}",key);
            map.put(key, pdata);
        }

        return map;
    }

    /**
     * 获取合并xls-writer
     * @param importList
     * @return
     * @throws Exception
     */
    public static ExcelWriter getMergeXlsWriter(List<?> importList) throws Exception{
        ExcelWriter writer = ExcelUtil.getWriterWithSheet("sheet1");
        if (ObjectUtil.isEmpty(importList)){
            return writer;
        }

        //合并后头信息
        List<String> exportXlsHeader = null;
        //子列表字段名
        String subListName = null;
        //子列表field
        Field subListField = null;
        //子列表头信息数组
        List<String> subHeadList = Collections.emptyList();
        //初始原字段数组
        Field[] fields = importList.get(0).getClass().getDeclaredFields();
        int subIndex = 0;
        for (int n = 0; n < fields.length; n++) {
            Field field = fields[n];
            field.setAccessible(true);
            //找到子列表
            if (Arrays.asList(field.getType().getInterfaces()).contains(Collection.class)){
                subIndex = n;
                subListName = field.getName();
                List<Field> fieldList = new ArrayList<>(Arrays.asList(fields));
                //获取子列表field
                subListField = field;
                //删除子列表原头信息
                fieldList.remove(field);
                //获取合并后头信息
                exportXlsHeader = YsXlsUtil.getExportXlsHeader(fieldList.toArray(new Field[fieldList.size() - 1]));
                Type actualTypeArgument = ((ParameterizedTypeImpl) field.getGenericType()).getActualTypeArguments()[0];
                subHeadList = YsXlsUtil.getExportXlsHeader((Class<?>) actualTypeArgument);
                exportXlsHeader.addAll(subHeadList);
                break;
            }
        }
        if (ObjectUtil.isEmpty(exportXlsHeader) || ObjectUtil.isEmpty(subListField)){
            throw new BizException("不存在子列表");
        }

        writer.write(Collections.singletonList(exportXlsHeader), true);

        List<Map<String, Object>> resultAllList = new ArrayList<>();
        //起始行合并
        int rowIndex = 1
                //子列表前合并列长
                , subBeginColumnSize = subIndex
                //子列表后合并列起始坐标
                , subEndColumnIndex = subIndex + subHeadList.size()
                //子列表后合并列长
                , mergeColumnSize = fields.length - 2;
        for (Object vo : importList){
            Collection subList = (Collection) subListField.get(vo);
            int brandsSize = subList.size();

            if (brandsSize != 1) {
                for (int i = 0; i < mergeColumnSize; i++) {
                    writer.merge(rowIndex, rowIndex + brandsSize - 1, i, i, null, false);
                }
            }
            rowIndex = rowIndex + brandsSize;

            //组装xls数据
            List<Map<String, Object>> allList = (List<Map<String, Object>>)subList.stream().map(sub -> BeanUtil.beanToMap(sub)).collect(Collectors.toList());
            Map<String, Object> objectMap = BeanUtil.beanToMap(vo);
            objectMap.remove(subListName);
            List<Map<String, Object>> resultList = allList.stream().map(map -> {
                Map<String, Object> resultMap = new LinkedHashMap<>();
                resultMap.putAll(objectMap);
                resultMap.putAll(map);
                return resultMap;
            }).collect(Collectors.toList());
            resultAllList.addAll(resultList);
        }

        //数据写入
        writer.write(resultAllList);

        //设置默认宽
        writer.getSheet().setDefaultColumnWidth(15);

        return writer;
    }

    /**
     * 流式xls读取处理
     * @param inputStream
     * @param rowHandler
     * @param <T> 务必指定 如无需要请直接调用ExcelUtil.readBySax
     */
    public static <T> void readBySax(InputStream inputStream, YsRowHandler<T> rowHandler){
        Class<T> clazz = rowHandler.getBeanClass();
        ExcelUtil.readBySax(inputStream, 0, new MapRowHandler(0, 1, Integer.MAX_VALUE) {
            @Override
            public void handleData(int sheetIndex, long rowIndex, Map<String, Object> data) {
                List<T> beanList = importXls(clazz, Collections.singletonList(data));
                try {
                    rowHandler.handle(sheetIndex, rowIndex, beanList.get(0));
                }catch (Exception e){
                    String errorMsg = String.format("xls导入异常,sheet:[%d],第[%d]行异常,成功数:[%d],错误信息:[%s]"
                            , sheetIndex, rowIndex + 1, rowIndex - 1, e.getMessage());
                    YsLogUtil.logError(e, errorMsg);
                    throw new BizException(ResultCode.CONFIRM, errorMsg);
                }
            }
        });
    }

    /**
     * 限制xls文件行数
     * 不能避免oom风险
     * 会进行一次流读取，注意避免流关闭
     * @param inputStream
     * @param limitNum
     */
    public static void limitRowNum(InputStream inputStream, Integer limitNum, String msg){
        YsAsserts.isTrue(ExcelUtil.getReader(inputStream).getRowCount() > limitNum, msg);
    }
    public static void limitRowNum(InputStream inputStream, Integer limitNum){
        limitRowNum(inputStream, limitNum, String.format("excel文件行过多，不建议超过%d条", limitNum));
    }

}
