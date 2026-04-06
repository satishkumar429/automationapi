package com.cdk.dms.tests.smoke;

import static com.cdk.dms.constants.Constants.*;
import com.cdk.dms.endpoints.SetUpsEndpoints;
import com.cdk.dms.setups.common.domain.PartsSetupCode;
import com.cdk.dms.tests.BaseSteps;
import com.cdk.dms.utils.CommonUtil;
import io.restassured.http.Method;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.ITest;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;

@Slf4j
public class CommonSmokeTest extends BaseSteps implements ITest {

  @Autowired
  private CommonUtil commonUtil;
  public Map<String, Map<String, String>> uniqueCriteriaKeyNames;
  private String accessToken;
  private final ThreadLocal<String> testName = new ThreadLocal<>();

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

  @Test(dataProvider = "provider",groups = "prv",description = "GET Request all fields for Setups")
  public void testGetRequestAllFields(String code) {
    try {
        commonUtil.getSetupsGenericRead(getResponse("", SetUpsEndpoints.genericReadSetups(code), Method.GET,
                        getBaseReadServicePath(), accessToken, false));
    }
    catch (Exception e)
    {
      log.info("{} test failed for Setup {}  with error message {}",getTestName(),code,e.getMessage());
    }
  }

  @Test(dataProvider = "provider",groups = "prv", description = "Search Setups with Empty Filters Fields ")
  public void testSearchSetupsWithEmptyFilters(String code) {
    try {
      commonUtil.searchSetupsEmptyFilterFieldTest(
              getResponse(SEARCH_SETUPS_BY_EMPTY_FILTERS_PAYLOAD,
                      SetUpsEndpoints.getSearchSetupsURL(code), Method.POST, getBaseReadServicePath(),
                      accessToken, false));
    } catch (Exception e)
    {
      log.info("{} test failed for FunctionalUnit {} and with the message {}",getTestName(),code,e.getMessage());
    }
  }

  @Test(dataProvider = "provider",groups = "prv", description = "POST Search Setups with One Response Field Payload ")
  public void testSearchWithOneFieldInResponseFieldsFilter(String code) {
    try {
      commonUtil.searchWithOneFieldInResponseFieldsFilterTest(
              getResponse(SEARCH_SETUPS_BY_SPECIFIC_RESPONSE_FIELDS_NO_SEARCH_PAYLOAD,
                      SetUpsEndpoints.getSearchSetupsURL(code), Method.POST, getBaseReadServicePath(),
                      accessToken, false), code);
    } catch (Exception e)
    {
      log.info("{} test failed for FunctionalUnit {} and with the message {}",getTestName(),code,e.getMessage());
    }
  }


  @BeforeMethod
  public void BeforeMethod(java.lang.reflect.Method method, Object[] testData) {
    testName.set(method.getName());
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
