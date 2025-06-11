package com.myhousekeepingservice;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;


public class CodeGenerator {
    static final String database = "comm-hms";
    static final String author = ""; //作者名
    static final String base_packageName = "com.myhousekeepingservice"; //修改为你的包名
    static final String tables_name = "chat_message";  //填写要生成的表名，多个用逗号分隔
    static final String tablePrefix = "";  //去除表前缀


    public static void main(String[] args) {
        FastAutoGenerator.create(
                        "jdbc:mysql://localhost:3306/" + database + "?useSSL=false&serverTimezone=Asia/Shanghai",
                        "root",
                        "123456"
                )
                .dataSourceConfig(builder -> builder
                        .typeConvert(new MySqlTypeConvert() {
                            @Override
                            public IColumnType processTypeConvert(GlobalConfig config, String fieldType) {
                                if (fieldType.toLowerCase().contains("tinyint")) {
                                    return DbColumnType.BOOLEAN;
                                } else if (fieldType.toLowerCase().contains("bigint")) {
                                    return DbColumnType.LONG;
                                } else if (fieldType.toLowerCase().contains("decimal")) {
                                    return DbColumnType.BIG_DECIMAL;
                                } else if (fieldType.toLowerCase().contains("varchar") || fieldType.toLowerCase().contains("text") || fieldType.toLowerCase().contains("longtext")) {
                                    return DbColumnType.STRING;
                                } else if (fieldType.toLowerCase().contains("int") || fieldType.toLowerCase().contains("integer")) {
                                    return DbColumnType.INTEGER;
                                } else if (fieldType.toLowerCase().contains("timestamp") || fieldType.toLowerCase().contains("date")) {
                                    return DbColumnType.LOCAL_DATE_TIME;
                                }else if (fieldType.toLowerCase().contains("enum")) {
                                    return DbColumnType.STRING;
                                }
                                return super.processTypeConvert(config, fieldType);
                            }
                        })
                )
                .globalConfig(builder -> {
                    builder.author("J") // 改成你的名字
                            .enableSwagger() // 如果需要 Swagger 注解
                            .outputDir(System.getProperty("user.dir") + "/backend/src/main/java")
                            .disableOpenDir(); // 不打开生成目录
                })
                .packageConfig(builder -> {
                    builder.parent(base_packageName)
                            .entity("entity")
                            .mapper("mapper")
                            .service("service")
                            .serviceImpl("service.impl")
                            .controller("controller");
                })
                .strategyConfig(builder -> {
                    builder.addInclude(tables_name)
                            .entityBuilder().enableFileOverride()
                            .enableLombok()
                            .enableTableFieldAnnotation() // 添加@TableField("xxx")
                            .idType(IdType.AUTO)
                            .naming(NamingStrategy.underline_to_camel)
                            .columnNaming(NamingStrategy.underline_to_camel)
                            .mapperBuilder().enableFileOverride()
                            .superClass(BaseMapper.class)
                            .enableMapperAnnotation()
                            .serviceBuilder().enableFileOverride()
                            .superServiceClass("com.baomidou.mybatisplus.extension.service.IService")
                            .superServiceImplClass("com.baomidou.mybatisplus.extension.service.impl.ServiceImpl")
                            .controllerBuilder().enableFileOverride()
                            .enableRestStyle()
                            .enableHyphenStyle();
                })
                .templateConfig(builder -> {
                    builder.entity("entity.java");
                    builder.service("service.java");
                    builder.serviceImpl("serviceImpl.java");
                    builder.controller("controller.java");
                    builder.xml(null);
                    // 如果不需要某类模板生成，可以设置为 null，比如：
                    // builder.mapper(null);
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 指定使用 Freemarker
                .execute();
    }
}

