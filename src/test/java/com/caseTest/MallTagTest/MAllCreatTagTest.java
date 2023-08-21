package com.caseTest.MallTagTest;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.Utils.JDBCUtils;
import com.alibaba.fastjson2.JSON;
import com.excelPojo.ExcelPOI;
import com.data.Globals;
import com.data.NewEnviroment;
import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.config.LogConfig;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import static com.common.BaseTest.*;
import static io.restassured.RestAssured.given;

public class MAllCreatTagTest {

   @BeforeClass
    public void setUp(){
       RestAssured.baseURI = Globals.BASE_URL;
       RestAssured.config = RestAssured.config().jsonConfig(JsonConfig.jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));
        List<ExcelPOI> listData = readSheetRowData(1,0,1);
        String param = listData.get(0).getInputParams();
        String url = listData.get(0).getUrl();
       Response resLogin = request(listData.get(0),"login");
        //将access_token值存入环境变量中
        extractToEvn(listData.get(0),resLogin);
       //System.out.println(NewEnviroment.envMap);
    }
    @Test(dataProvider = "getTagDatas")
    public void tagTest(ExcelPOI excelPOI){
        // String param = excelPOI.getInputParams();
        //随机生成tag名title并存入环境变量中并替换
        replaceData(excelPOI);

        Map<String,Object> headersMap = JSON.parseObject(excelPOI.getRequestHeader());
        Response res = request(excelPOI,"creatTag");
            assertSqlServer(excelPOI);

    }
    @DataProvider
    public Object[] getTagDatas(){
        List<ExcelPOI> listData = readSheetRowData(1,1);
        return listData.toArray();//toArray方法将list类型转化为数组
    }
}

