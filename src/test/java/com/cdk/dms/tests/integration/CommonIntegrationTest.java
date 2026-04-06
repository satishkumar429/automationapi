package com.cdk.dms.tests.integration;

import static com.cdk.dms.constants.Constants.*;
import com.cdk.dms.endpoints.SetUpsEndpoints;
import com.cdk.dms.setups.common.domain.PartsSetupCode;
import com.cdk.dms.setups.common.utils.AutomationUtility;
import com.cdk.dms.setups.common.utils.CommonUtility;
import com.cdk.dms.tests.BaseSteps;
import com.cdk.dms.utils.CommonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.Method;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.restassured.response.Response;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.ITest;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;

public class CommonIntegrationTest extends BaseSteps implements ITest {

  private final ThreadLocal<String> testName = new ThreadLocal<>();
  @Autowired
  private AutomationUtility automationUtility;
  @Autowired
  private CommonUtil commonUtil;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private CommonUtility commonUtility;
  private String accessToken;
  public Map<String, Map<String, String>> uniqueCriteriaKeyNames;

  @DataProvider(name = "provider")
  public static Object[][] provider() {
    List<PartsSetupCode> setupCodeList = new ArrayList<>(FUS_Implemented);
    setupCodeList.clear();
    setupCodeList.addAll(FUS_Implemented);
    Object[][] setups = new Object[setupCodeList.size()][];
    int counter = 0;
    for (PartsSetupCode ele : setupCodeList) {
      setups[counter] = new Object[]{ele.name()};
      counter++;
    }
    return setups;
  }


  @Test(dataProvider = "provider", description = "GET Request all fields for Setups")
  public void testGetRequestAllFields(String code) {
      commonUtil.getSetupsGenericRead(
              getResponse("", SetUpsEndpoints.genericReadSetups(code), Method.GET,
                      getBaseReadServicePath(), accessToken, false));
  }

  @Test(dataProvider = "provider", description = "POST Request all fields for Setups")
  public void testSearchAllFields(String code) {
    commonUtil.searchAllFieldsTest(
        getResponse(SEARCH_PAYLOAD, SetUpsEndpoints.getSearchSetupsURL(code), Method.POST,
            getBaseReadServicePath(), accessToken, false));
  }

  @Test(dataProvider = "provider", priority = 4, description = "Search Setups with Empty Filters Fields")
  public void testSearchSetupsWithEmptyFilters(String code) {
    commonUtil.searchSetupsEmptyFilterFieldTest(
        getResponse(SEARCH_SETUPS_BY_EMPTY_FILTERS_PAYLOAD,
            SetUpsEndpoints.getSearchSetupsURL(code), Method.POST, getBaseReadServicePath(),
            accessToken, false));
  }

  @Test(dataProvider = "provider", description = "POST Request with Unique Criteria fields for Setups with Search Payload", priority = 0)
  public void testSearchSetupsWithUniqueCriteriaInSearchFilter(String code) throws IOException, JSONException {
    Map<String, Object> uniqueFieldValues = commonUtil.getUniqueKVMap(code);
    List<Map<String, Object>> filters = commonUtil.convertToFilterList(uniqueFieldValues);

    String searchPayload = "{ \"filters\": " + objectMapper.writeValueAsString(filters) + " }";

    Response response = getResponse( searchPayload, SetUpsEndpoints.getSearchSetupsURL(code), Method.POST,
            getBaseReadServicePath(), accessToken, false);

    commonUtil.SearchSetupsWithUniqueCriteriaInSearchFilterTest(response, code);
  }

  @Test(dataProvider = "provider", description = "POST Search Setups with One Response Field Payload ")
  public void testSearchWithOneFieldInResponseFieldsFilter(String code) throws IOException {
    commonUtil.searchWithOneFieldInResponseFieldsFilterTest(
        getResponse(SEARCH_SETUPS_BY_SPECIFIC_RESPONSE_FIELDS_NO_SEARCH_PAYLOAD,
            SetUpsEndpoints.getSearchSetupsURL(code), Method.POST, getBaseReadServicePath(),
            accessToken, false), code);
  }

  @Test(dataProvider = "provider", description = "POST Request for 400 Bad Request ")
  public void testSearchSetupsWithInvalidResponseFields(String code) throws IOException {
    commonUtil.SearchSetupsWithInvalidResponseFieldsTest(
        getResponse(SEARCH_SETUPS_BY_INVALID_RESPONSE_FIELDS_PAYLOAD,
            SetUpsEndpoints.getSearchSetupsURL(code), Method.POST, getBaseReadServicePath(),
            accessToken, false));
  }

  @Test(dataProvider = "provider", description = "POST Request for 400 Bad Request ")
  public void testSearchSetupsWithMultiInvalidResponseFields(String code) throws IOException {
    commonUtil.SearchSetupsWithMultiInvalidResponseFieldsTest(
            getResponse(SEARCH_SETUPS_BY_MULTIPLE_INVALID_RESPONSE_FIELDS_PAYLOAD,
                    SetUpsEndpoints.getSearchSetupsURL(code), Method.POST, getBaseReadServicePath(),
                    accessToken, false));
  }

  @Test(dataProvider = "provider", description = "POST Request for 400 Bad Request Validation Error")
  public void testPostRequestValidationError(String code) throws IOException {
    commonUtil.postRequestSetupWithValidationErrorPayload(
        getResponse(SEARCH_SETUPS_BY_VALIDATION_ERROR_FIELDS_PAYLOAD,
            SetUpsEndpoints.getSearchSetupsURL(code), Method.POST, getBaseReadServicePath(),
            accessToken, false));
  }

  @Test(dataProvider = "provider", description = "POST Request for 404 Schema Not Found Validation Error")
  public void testGetRequestForInvalidFunctionName(String code){
    String invalidFunctionalUnitName = code.concat(RANDOM_STRING);
    commonUtil.getInvalidFunctionNameValidation(getResponse("", SetUpsEndpoints.genericReadSetups(invalidFunctionalUnitName),
            Method.GET, getBaseReadServicePath(), accessToken, false));
  }

  @Test(dataProvider = "provider", description = "POST Request for 400 Bad Request Missing Headers Validation Error")
  public void testGetRequestForHeaderValidation(String code){
    commonUtil.getHeadersValidation(getResponse("", SetUpsEndpoints.genericReadSetups(code),
            Method.GET, getBaseReadServicePath(), accessToken, true));
  }

  
  @BeforeMethod(alwaysRun = true) //This method is meant to rename the testcase names
  public void BeforeMethod(java.lang.reflect.Method method, Object[] testData) {
    testName.set(method.getName() + "_" + testData[0]);
  }

  @BeforeMethod(alwaysRun = true)
  public void setResultTestName(ITestResult result)
      throws NoSuchFieldException, IllegalAccessException {
    BaseTestMethod baseTestMethod = (BaseTestMethod) result.getMethod();
    Field field = baseTestMethod.getClass().getSuperclass().getDeclaredField("m_methodName");
    field.setAccessible(true);
    field.set(baseTestMethod, getTestName());
  }

  @BeforeClass(alwaysRun = true)
  public void createSetup() throws IOException {
    accessToken = commonUtil.fetchAccessToken();
    uniqueCriteriaKeyNames = commonUtil.createSetupPreCondition(accessToken);
  }

  @AfterClass(alwaysRun = true)
  public void clearTestData() {
    commonUtil.clearTestDataPostCondition(accessToken);
  }

  @Override
  public String getTestName() {
    return testName.get();
  }

}
