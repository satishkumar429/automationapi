package com.cdk.dms.tests.smoke;

import com.cdk.dms.endpoints.SetUpsEndpoints;
import com.cdk.dms.setups.common.utils.AutomationUtility;
import com.cdk.dms.tests.BaseSteps;
import com.cdk.dms.utils.CommonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.ITest;
import org.testng.ITestResult;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.cdk.dms.constants.Constants.*;

public class ProdSmokeTest extends BaseSteps implements ITest {

  @Autowired
  private AutomationUtility automationUtility;
  @Autowired
  private CommonUtil commonUtil;

  private String accessToken;
  private final ThreadLocal<String> testName = new ThreadLocal<>();


  public enum ProdSetupCodes {
    SOSOptions, PriorityCodes,  DictPartNo, InventoryBin;
  }

  @DataProvider(name = "provider")
  public static Object[][] provider() {
    List<ProdSetupCodes> setupCodeList = new ArrayList<>(Arrays.asList(ProdSetupCodes.values()));
    Object[][] setups = new Object[setupCodeList.size()][];
    int counter = 0;
    for (ProdSetupCodes ele : setupCodeList) {
      setups[counter] = new Object[]{ele.name()};
      counter++;
    }
    return setups;
  }

  @Test(dataProvider = "provider", description = "Get all fields for Setups ")
  public void testGetRequestAllFields(String code) {
      commonUtil.getSetupsGenericRead(
              getResponse("", SetUpsEndpoints.genericReadSetups(code), Method.GET,
                      getBaseReadServicePath(), accessToken, false));
  }

  @Test(dataProvider = "provider", priority = 4, description = "Search Setups with Empty Filters Fields ")
  public void testSearchSetupsWithEmptyFilters(String code) {
    commonUtil.searchSetupsEmptyFilterFieldTest(
            getResponse(SEARCH_SETUPS_BY_EMPTY_FILTERS_PAYLOAD,
                    SetUpsEndpoints.getSearchSetupsURL(code), Method.POST, getBaseReadServicePath(),
                    accessToken, false));
  }

  @Test(dataProvider = "provider", description = "POST Search Setups with One Response Field Payload ")
  public void testSearchWithOneFieldInResponseFieldsFilter(String code) throws IOException {
    commonUtil.searchWithOneFieldInResponseFieldsFilterTest(
        getResponse(SEARCH_SETUPS_BY_SPECIFIC_RESPONSE_FIELDS_NO_SEARCH_PAYLOAD,
            SetUpsEndpoints.getSearchSetupsURL(code), Method.POST, getBaseReadServicePath(),
            accessToken, false), code);
  }
  
  @BeforeMethod
  public void BeforeMethod(java.lang.reflect.Method method, Object[] testData) {

    testName.set(method.getName() + "_" + testData[0]);
  }

  @BeforeMethod(alwaysRun = true)
  public void setResultTestName(ITestResult result) throws NoSuchFieldException, IllegalAccessException {
    BaseTestMethod baseTestMethod = (BaseTestMethod) result.getMethod();
    Field field = baseTestMethod.getClass().getSuperclass().getDeclaredField("m_methodName");
    field.setAccessible(true);
    field.set(baseTestMethod, getTestName());
  }

  @BeforeClass(alwaysRun = true)
  public void fetchAccessToken() {
    accessToken=commonUtil.fetchAccessToken();
  }

  @Override
  public String getTestName() {
    return testName.get();
  }
}
