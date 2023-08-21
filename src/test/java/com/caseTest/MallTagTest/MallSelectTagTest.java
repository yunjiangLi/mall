package com.caseTest.MallTagTest;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import com.data.Globals;
import com.excelPojo.ExcelPOI;
import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;

import static com.common.BaseTest.*;

public class MallSelectTagTest {
    @BeforeClass
    public void setUp(){
        RestAssured.baseURI = Globals.BASE_URL;
        List<ExcelPOI> listData = readSheetRowData(2,0,2);
        //登录
        //Response resLogin = request(listData.get(0),"login");
        Response resLogin = request(listData.get(0),"login");
        //将access_token值存入环境变量中
        extractToEvn(listData.get(0),resLogin);
        //CreatTag接口调用
        replaceData(listData.get(1));
        //Response resCreatTag = request(listData.get(1),"creatTag");
        Response resCreatTag = request(listData.get(1),"creatTag");
    }
    @Test(dataProvider = "getSelectTagData")
    public void mallSelectTagTest(ExcelPOI excelPOI){
        //正则替换
        caseReplace(excelPOI);
        //Response res = request(excelPOI,"selectTag");
        Response res = request(excelPOI,"selectTag");
        assertResponse(excelPOI,res);
    }

    //数据驱动
    @DataProvider
    public Object[] getSelectTagData(){
        List<ExcelPOI> list = readSheetRowData(2,2);
        return  list.toArray();
    }
}
