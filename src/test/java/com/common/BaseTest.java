package com.common;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.Utils.JDBCUtils;
import com.alibaba.fastjson2.JSON;
import com.data.Globals;
import com.data.NewEnviroment;
import com.excelPojo.ExcelPOI;
import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;

import java.io.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;

public class BaseTest {
    @BeforeTest
    public void GlobalSetup() throws FileNotFoundException {
        //返回json为Decimal数据类型
        RestAssured.config=RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL));
        //BaseUrl全局配置
        RestAssured.baseURI= Globals.BASE_URL;
    }
    //封装RestAssured框架
    public static Response request(ExcelPOI excelPOI,String interfaceModuleName) {
        String logFilepath;
        //Constants.LOG_TO_FILE值为false,日志信息输出到控制台，为ture输出到allure报表中
        if(Globals.LOG_TO_FILE){
            File dirPath = new File(System.getProperty("user.dir")+"\\log\\"+interfaceModuleName);
            if(!dirPath.exists()){
                dirPath.mkdirs();
            }
            logFilepath = dirPath +"\\test"+ excelPOI.getCaseId() + ".log";
            PrintStream fileOutPutStream = null;
            try {
                fileOutPutStream =new PrintStream(new File(logFilepath));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            RestAssured.config =RestAssured.config().logConfig(LogConfig.logConfig().defaultStream(fileOutPutStream));

        }
        String url = excelPOI.getUrl();
        String header = excelPOI.getRequestHeader();
        String method = excelPOI.getMethod();
        Map<String, Object> heardsMap = JSON.parseObject(header, Map.class);
        Response res = null;
        if (method.equalsIgnoreCase("POST")) {
            res = given().headers(heardsMap).body(excelPOI.getInputParams()).when().post(url).then().log().all().extract().response();
        } else if (method.equalsIgnoreCase("get")) {
            res = given().headers(heardsMap).when().get(url).then().log().all().extract().response();
        }else if (method.equalsIgnoreCase("put")){
            res = given().headers(heardsMap).body(excelPOI.getInputParams()).when().post(url).then().log().all().extract().response();
        } else if (method.equalsIgnoreCase("delete")) {
            res = given().headers(heardsMap).when().delete(url).then().log().all().extract().response();
        }
//向Allure报表中添加日志
        if(Globals.LOG_TO_FILE) {
            try {
                Allure.addAttachment(interfaceModuleName +"接口响应数据",new FileInputStream(logFilepath));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    //读取excel文件中指定sheet页指定行数据
    public static List<ExcelPOI> readSheetRowData(int sheetNum, int startRows, int readRows) {
        File file = new File(Globals.EXCEL_FILE_PATH);
        ImportParams importParams = new ImportParams();
        importParams.setStartSheetIndex(sheetNum);
        importParams.setStartRows(startRows);
        importParams.setReadRows(readRows);
        List<ExcelPOI> list = ExcelImportUtil.importExcel(file, ExcelPOI.class, importParams);
        return list;
    }
//获取sheet页名称
//    public static String getSheetName(int sheetNum){
//        String sheetName= null;
//        File file = new File(Globals.EXCEL_FILE_PATH);
//        try {
//            Workbook workbook = WorkbookFactory.create(file);
//            sheetName= workbook.getSheetAt(sheetNum).getSheetName();
//            return sheetName;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return sheetName;
//    }

    //读取excel文件中指定sheet页开始行到结束行的数据
    public static List<ExcelPOI> readSheetRowData(int sheetNum, int startRows) {

        File file = new File(Globals.EXCEL_FILE_PATH);
        ImportParams importParams = new ImportParams();
        importParams.setStartSheetIndex(sheetNum);
        importParams.setStartRows(startRows);
        List<ExcelPOI> list = ExcelImportUtil.importExcel(file, ExcelPOI.class, importParams);
        return list;
    }

    //将关联数据存入参数池
    public static void extractToEvn(ExcelPOI excelPOI, Response res) {
        Map<String, Object> extractMap = JSON.parseObject(excelPOI.getExtract());
        for ( String key : extractMap.keySet() ) {
            Object path = extractMap.get(key);
            Object value = res.jsonPath().get(path.toString());
            NewEnviroment.envMap.put(key, value);
        }
    }

    //正则匹配
    public static String regexPlace(String orgStr) {
        if (StringUtils.isEmpty(orgStr)) {
            return orgStr;
        }
        //正则替换-定义正则表达式
        Pattern pattern = Pattern.compile("\\{\\{(.*?)}}");
        //定义匹配对象
        Matcher matcher = pattern.matcher(orgStr);
        String result = orgStr;
        while (matcher.find()) {
            String outerStr = matcher.group(0);
            String innerStr = matcher.group(1);
            Object replaceStr = NewEnviroment.envMap.get(innerStr);
            result = result.replace(outerStr, replaceStr.toString());
        }
        return result;
    }

    //正则替换
    public static ExcelPOI caseReplace(ExcelPOI excelPOI) {
        //替换请求参数
        String inputParams = regexPlace(excelPOI.getInputParams());
        excelPOI.setInputParams(inputParams);
        //替换请求头
        String headers = regexPlace(excelPOI.getRequestHeader());
        excelPOI.setRequestHeader(headers);
        //替换url
        String url = regexPlace(excelPOI.getUrl());
        excelPOI.setUrl(url);
        //替换预期结果
        String expected = regexPlace(excelPOI.getExpected());
        excelPOI.setExpected(expected);
        //替换提取数据库断言数据
        String dbAssert = regexPlace(excelPOI.getDbAssert());
        excelPOI.setDbAssert(dbAssert);
        return excelPOI;
    }

    //随机生成纯字母字符串
    public static String getAlphabetic() {
        String alphaStr = "接口-" + RandomStringUtils.randomAlphabetic(2);
        return alphaStr;
    }

    //数据库中查询是否存在该tag名
    public static String getUnregisterTagName() {
        String tagName = null;
        Object result;
        while (true) {
            tagName = getAlphabetic();
            result = JDBCUtils.singleData("SELECT count(*) from tz_prod_tag WHERE title = '" + tagName + "';");
            if ((Long) result == 0) {
                break;
            }
        }
        return tagName;
    }

    //将tag名存入参数池中
    public static void putEnvironment() {
        String tagName = getUnregisterTagName();
        NewEnviroment.envMap.put("title", tagName);
    }

    //替换数据存入环境变量
    public static void replaceData(ExcelPOI excelPOI) {
        putEnvironment();
        caseReplace(excelPOI);
    }

    //响应数据断言
    public static void assertResponse(ExcelPOI excelPOI, Response res) {
        Map<String, Object> expectedMap = (Map) JSON.parse(excelPOI.getExpected());
        for ( String key : expectedMap.keySet() ) {
            Object actualData = res.jsonPath().get(key);
            Object expectedData = expectedMap.get(key);
            Assert.assertEquals(actualData, expectedData);
        }
    }

    //数据库断言
    public static void assertSqlServer(ExcelPOI excelPOI) {
        String dbAssert  = excelPOI.getDbAssert();
        if(dbAssert != null) {
            Map<String, Object> dbAssertMap = JSON.parseObject(dbAssert);
            Set<String> keys = dbAssertMap.keySet();
            for(String key:keys){
                Object expectedValue = dbAssertMap.get(key);
                if(expectedValue instanceof BigDecimal){
                    Object actualValue = JDBCUtils.singleData(key);
                    //System.out.println("actualValue类型:" + actualValue.getClass());
                    Assert.assertEquals(actualValue,expectedValue);
                }
                else if(expectedValue instanceof Integer){
                    //此时从excel里面读取到的是integer类型
                    //从数据库里面拿到的是Long类型
                    Long expectedValue2 = ((Integer) expectedValue).longValue();
                    Object actualValue = JDBCUtils.singleData(key);
                    Assert.assertEquals(actualValue,expectedValue2);
                }
            }
        }
    }
}
