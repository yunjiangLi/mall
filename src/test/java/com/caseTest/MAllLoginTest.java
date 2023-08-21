package com.caseTest;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.alibaba.fastjson2.JSON;
import com.excelPojo.ExcelPOI;
import com.data.Globals;
import com.data.NewEnviroment;
import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.response.Response;
import org.apache.xmlbeans.impl.xb.xsdschema.All;
import org.testng.Assert;
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

public class MAllLoginTest {

    @Test(dataProvider = "getLoginDatas")
    public void loginTest(ExcelPOI excelPOI){
        RestAssured.baseURI = Globals.BASE_URL;
        String param = excelPOI.getInputParams();
        String url = excelPOI.getUrl();
        System.out.println(param);
        Map<String,Object> headersMap = JSON.parseObject(excelPOI.getRequestHeader());
        Response res =
        request(excelPOI,"login");
        //断言
        assertResponse(excelPOI,res);
    }
    @DataProvider
    public Object[] getLoginDatas(){
        List<ExcelPOI> listData = readSheetRowData(0,0,1);
        return listData.toArray();//toArray方法将list类型转化为数组
    }

}
