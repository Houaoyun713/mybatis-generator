package enn.base.generator;


import enn.base.generator.utils.GeneratorFacade;
import enn.base.generator.utils.util.FileHelper;

public class Generator {


	public static void main(String[]args) throws Exception{
        GeneratorFacade facade = new GeneratorFacade();
        byte i = 0;
        byte i1 = 127;
//        facade.getGenerator().addTemplateRootDir("F:\\cc");
        //删除生成器的输出目录
        facade.deleteOutRootDir();
        facade.getGenerator().addTemplateRootDir(FileHelper.getFile("classpath:template-idLong/cloud"));
        // facade.getGenerator().addTemplateRootDir(FileHelper.getFile("classpath:template"));
        //facade.generateByAllTable();
        /** 指定表对应代码 */
//        facade.generateByTable("db_info");
//        facade.generateByTable("scn_alg_ref");
//        facade.generateByTable("alg_info");
        facade.generateByTable("scn_info");
//        facade.generateByTable("acc_info_10205");
//        facade.generateByTable("acc_info_10206");
//        facade.generateByTable("acc_info_10207");
//        facade.generateByTable("acc_info_10208");

    }
}
