package com.caseTest.MallTagTest;

import com.data.Globals;
import com.excelPojo.ExcelPOI;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static com.common.BaseTest.*;

public class MAllUpdateTagTest {
    @BeforeClass
    public void setUp(){
        RestAssured.baseURI = Globals.BASE_URL;
        List<ExcelPOI> listData = readSheetRowData(3,0,3);
    //登录
        Response resLogin = request(listData.get(0),"login");
        //将access_token值存入环境变量中
        extractToEvn(listData.get(0),resLogin);
    //CreatTag接口调用
        replaceData(listData.get(1));
        Response resCreatTag = request(listData.get(1),"creatag");
    //selectTag接口调用
        caseReplace(listData.get(2));
       // replaceData(listData.get(2));
        //Response resSelectTag = request(listData.get(2),"selectTag");
        Response resSelectTag = request(listData.get(2),"selectTag");
        //将tagId值存入环境变量中
        extractToEvn(listData.get(2),resSelectTag);
    }
    @Test (dataProvider = "getUpdateTagData")
    public void mallUpdateTagTest(ExcelPOI excelPOI){
        //System.out.println(NewEnviroment.envMap);
        //正则替换
        caseReplace(excelPOI);
//        Response res = request(excelPOIResponse res = request(excelPOI,"updateTagData");
        Response res = request(excelPOI,"updateTag");

        assertSqlServer(excelPOI);
    }

    //数据驱动
    @DataProvider
    public Object[] getUpdateTagData(){
        List<ExcelPOI> list = readSheetRowData(3,3);
        return  list.toArray();
    }
}
