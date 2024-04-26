import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.sql.Types;
import java.util.Collections;

/**
 * @author: cy
 * @description:
 * @date: 2024-04-24 17:31
 **/
public class DataGenerate {

    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://localhost:3307/group_report?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8"
                        , "root", "123456")
                .globalConfig(builder -> {
                    builder.author("cy") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .fileOverride() // 覆盖已生成文件
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
                            .moduleName("groupReport") // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, "D://IDEA//dataGenerate")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("group_report_catalogue,group_report_content_abnormal,group_report_content_abnormal_his,group_report_content_abnormal_pe_user_info_ref,group_report_content_abnormal_pe_user_info_ref_his,group_report_content_catalogue,group_report_content_conclusion,group_report_content_conclusion_his,group_report_content_config_field,group_report_content_detail_field_his,group_report_content_other_instructions,group_report_content_pe_user_info,group_report_content_pe_user_info_his,group_report_content_project_conclusion,group_report_content_project_conclusion_his,group_report_content_reading_suggestions,group_report_content_suggestion_explain,group_report_content_suggestion_explain_his,group_report_params,group_report_task,group_report_task_his,group_report_task_ref,group_report_task_ref_his,group_report_template,group_report_template_catalogue_ref,group_report_template_catalogue_ref_his"); // 设置需要生成的表名
//                            .addTablePrefix("t_", "c_"); // 设置过滤表前缀

                    builder.entityBuilder()
                            .enableLombok()
                            .enableFileOverride()
                            .enableChainModel()
//                            .superClass(BaseTenantBO.class)
//                            .addIgnoreColumns("id","tenant_id","created_id","created_name","created_time","updated_id","updated_name","updated_time","deleted")
                    ;

                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }
}
