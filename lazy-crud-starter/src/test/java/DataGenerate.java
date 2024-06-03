import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.lazy.crud.mybatis.YsServiceImpl;
import org.apache.ibatis.annotations.Mapper;

import java.sql.Types;
import java.util.Collections;

/**
 * @author: cy
 * @description:
 * @date: 2024-04-24 17:31
 **/
public class DataGenerate {

    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://10.92.100.253:3307/tjuacp_new?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8"
                        , "root", "123456")
                .globalConfig(builder -> {
                    builder.author("cy") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .outputDir("D://IDEA//dataGenerate"); // 指定输出目录
                })
                .dataSourceConfig(builder -> builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                    int typeCode = metaInfo.getJdbcType().TYPE_CODE;
                    if (typeCode == Types.SMALLINT) {
                        // 自定义类型转换
                        return DbColumnType.INTEGER;
                    }
                    return typeRegistry.getColumnType(metaInfo);

                }))
                .packageConfig(builder -> {
                    builder.parent("cn.tohealth") // 设置父包名
                            .moduleName("tjuacp") // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, "D://IDEA//dataGenerate")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("system_tenant_misc,system_tenant_misc_user"); // 设置需要生成的表名
//                            .addTablePrefix("t_", "c_"); // 设置过滤表前缀

                    builder.entityBuilder()
                            .enableFileOverride()
                            .enableChainModel()

                            .enableLombok()
                            .enableChainModel();

                    builder.controllerBuilder()
                            .enableFileOverride()
                            .enableRestStyle();

                    builder.serviceBuilder()
                            .enableFileOverride()
                            .superServiceImplClass(YsServiceImpl.class);

                    builder.mapperBuilder()
                            .enableFileOverride()
                            .mapperAnnotation(Mapper.class)
                            .enableBaseResultMap()
                            .enableBaseColumnList();


                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }
}
