package com.caseTest.MallTagTest;

import com.data.Globals;
import com.excelPojo.ExcelPOI;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static com.common.BaseTest.*;

public class MallDropTagTest {
    @BeforeClass
    public void setUp(){
        RestAssured.baseURI = Globals.BASE_URL;
        List<ExcelPOI> excelPoiList = readSheetRowData(4,0,4);
        //登录
        ExcelPOI loginData = excelPoiList.get(0);
        Response resLogin = request(loginData,"login");
        //将token存入参数池
        extractToEvn(loginData,resLogin);
        //新增分组数据
        //替换数据

        ExcelPOI createTagData = excelPoiList.get(1);
        replaceData(createTagData);
        request(createTagData,"creatTag");
        //查询分组数据
        ExcelPOI selectTagData = excelPoiList.get(2);
        caseReplace(selectTagData);
        Response resSelectTag = request(selectTagData,"selectTag");
        //将tag的id值存入参数池
        extractToEvn(selectTagData,resSelectTag);
        //修改tag名称
        ExcelPOI updateTag = excelPoiList.get(3);
        caseReplace(updateTag);
        request(updateTag,"updateTag");

    }
    @Test (dataProvider = "getDropTagData")
    public void MallDropTagTest(ExcelPOI excelPOI){
        //替换excelPojo数据
        caseReplace(excelPOI);
        //发送请求
        Response res = request(excelPOI,"dropTag");

    }
    @DataProvider
    public Object[] getDropTagData(){
        List<ExcelPOI> dropTagData = readSheetRowData(4,4);
        return dropTagData.toArray();
    }

}
