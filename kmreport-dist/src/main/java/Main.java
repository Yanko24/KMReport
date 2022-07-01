import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Column;
import com.baomidou.mybatisplus.generator.fill.Property;

import java.util.Collections;

public class Main {
    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://127.0.0.1:3306/kmreport", "root", "xiaoer")
                .globalConfig(builder -> {
                    builder.author("Yanko24")
                            .enableSwagger()
                            .disableOpenDir()
                            .outputDir("./kmreport-admin/src/main/java/");
                })
                .packageConfig(builder -> {
                    builder.parent("com")
                            .moduleName("kmreport")
                            .entity("model")
                            .pathInfo(Collections.singletonMap(OutputFile.xml, "./kmreport-admin/src/main/resources" +
                                    "/mapper/"));
                })
                .strategyConfig(builder -> {
                    builder.addInclude("kmreport_user");
                })
                .templateEngine(new FreemarkerTemplateEngine())
                .templateConfig(builder -> {

                })
                .strategyConfig(builder -> {
                    builder.entityBuilder()
                            .fileOverride()
                            .enableLombok()
                            .addTableFills(new Column("create_time", FieldFill.INSERT))
                            .addTableFills(new Property("updateTime", FieldFill.INSERT_UPDATE))
                            .idType(IdType.AUTO)
                            .convertFileName(entityName -> entityName.replace("Kmreport", ""));
                    builder.mapperBuilder()
                            .fileOverride()
                            .enableMapperAnnotation()
                            .enableBaseColumnList()
                            .enableBaseResultMap()
                            .convertMapperFileName(entityName -> entityName.replace("Kmreport", "") + "Mapper")
                            .convertXmlFileName(entityName -> entityName.replace("Kmreport", "") + "Mapper");
                    builder.controllerBuilder()
                            .fileOverride()
                            .enableRestStyle()
                            .convertFileName(entityName -> entityName.replace("Kmreport", "") + "Controller");
                    builder.serviceBuilder()
                            .fileOverride()
                            .convertServiceFileName(entityName -> entityName.replace("Kmreport", "") + "Service")
                            .convertServiceImplFileName(entityName -> entityName.replace("Kmreport", "") + "ServiceImpl");
                })
                .execute();
    }
}
