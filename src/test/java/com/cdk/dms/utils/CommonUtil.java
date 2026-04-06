package com.cdk.dms.utils;

import static com.cdk.dms.constants.Constants.*;
import static com.cdk.dms.setups.common.utils.CommonUtility.UNIQUE_CRITERIA_JSON_PATH;

import com.cdk.dms.endpoints.SetUpsEndpoints;
import com.cdk.dms.setups.common.domain.PartsSetupCode;
import com.cdk.dms.setups.common.utils.AutomationUtility;
import com.cdk.dms.setups.common.utils.CommonUtility;
import com.cdk.dms.tests.BaseSteps;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.testng.asserts.SoftAssert;

@Slf4j
@Component
public class CommonUtil extends BaseSteps {

  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private AutomationUtility automationUtility;

  @Autowired
  private CommonUtility commonUtility;

  SoftAssert softAssert = new SoftAssert();
  private Map<String, Map<String, String>> setupIds = new HashMap<>();
  private Response createResponse;

  public void createSetup(Response response, String code) {
    if (response.getStatusCode() != 201) {
      log.warn(
          "Create failed for " + code + " and the status code is " + response.getStatusCode());
    }
    log.info(response.getBody().prettyPrint());
  }

  public void getSetupsGenericRead(Response response) {
    softAssert.assertEquals(response.getStatusCode() /*actual value*/, 200 /*expected value*/,
        CORRECT_STATUS_CODE_MESSAGE);
    log.info(response.getBody().prettyPrint());
    List<Object> setupsList = (List<Object>) getData(response);
    softAssert.assertTrue(setupsList.size() > 0);
    Boolean test = setupsList.stream().allMatch(item -> {
      Map<String, Object> res = (Map<String, Object>) item;
      return (res.get("enterprise_id").equals(getEnterpriseId()) && res.get("department_id")
          .equals(getDepartmentId()));
    });
    softAssert.assertTrue(test);
    softAssert.assertAll();
  }

  public void searchAllFieldsTest(Response response) {
    softAssert.assertEquals(response.getStatusCode() /*actual value*/, 200 /*expected value*/,
        CORRECT_STATUS_CODE_MESSAGE + response.getStatusCode());
    List<Object> setupsList = (List<Object>) getData(response);
    softAssert.assertTrue(setupsList.size() > 0);
  }

  public void searchSetupsEmptyFilterFieldTest(Response response) {
    softAssert.assertEquals(response.getStatusCode(), 400, CORRECT_STATUS_CODE_MESSAGE);
    softAssert.assertTrue(response.getBody().asString().contains(FILTERS_MISSING));
    softAssert.assertAll();
  }

  public void SearchSetupsWithUniqueCriteriaInSearchFilterTest(Response response, String code)
      throws IOException, JSONException {
      softAssert.assertEquals(response.getStatusCode() /*actual value*/, 200 /*expected value*/,
              CORRECT_STATUS_CODE_MESSAGE);
      List<Object> setupsList = (List<Object>) getData(response);
      softAssert.assertTrue(setupsList.size() > 0);
      JsonNode readResponseMap = response.getBody().as(JsonNode.class).get("data").get(0);
      Map<String, Object> readResponsePayloadMap = objectMapper.convertValue(readResponseMap,
              Map.class);
      readResponsePayloadMap.keySet().removeAll(FIELDS_TO_IGNORE);
      final Map<String, Object> createPayloadMap = new ObjectMapper().readValue(
              getCreatePayload(code), HashMap.class);
      createPayloadMap.keySet().removeAll(FIELDS_TO_IGNORE);
      JSONAssert.assertEquals(mapToJsonNode(createPayloadMap), mapToJsonNode(readResponsePayloadMap),
              JSONCompareMode.LENIENT);
      softAssert.assertAll();
  }

  public void searchWithOneFieldInResponseFieldsFilterTest(Response response, String code)
      throws IOException {
    softAssert.assertTrue(
        (response.getBody().as(JsonNode.class).get("data").get(0).get("enterprise_id")
            .textValue()).equals(getEnterpriseId()));
    softAssert.assertTrue(compareKey(response.getBody().as(JsonNode.class).get("data").get(0),
        getCreatePayloadFields(code)));
    softAssert.assertAll();
  }

  public void SearchSetupsWithInvalidResponseFieldsTest(Response response) {
    softAssert.assertEquals(response.getStatusCode(), 400,
        CORRECT_STATUS_CODE_MESSAGE);
    ResponseBody body = response.getBody();
    softAssert.assertEquals(body.asString(), BAD_REQUEST_INVALID_FIELDS);
    softAssert.assertAll();
  }

  public void SearchSetupsWithMultiInvalidResponseFieldsTest(Response response) {
    softAssert.assertEquals(response.getStatusCode(), 400,
            CORRECT_STATUS_CODE_MESSAGE);
    ResponseBody body = response.getBody();
    softAssert.assertEquals(body.asString(), BAD_REQUEST_MULTIPLE_INVALID_FIELDS);
    softAssert.assertAll();
  }

  public void postRequestSetupWithValidationErrorPayload(Response response) {
    softAssert.assertEquals(response.getStatusCode(), 400,
        CORRECT_STATUS_CODE_MESSAGE);
    ResponseBody body = response.getBody();
    softAssert.assertTrue(body.asString().contains(VALIDATION_ERROR));
    softAssert.assertAll();
  }

  public void getInvalidFunctionNameValidation(Response response){
    softAssert.assertEquals(response.getStatusCode(), 404, CORRECT_STATUS_CODE_MESSAGE);
    ResponseBody body = response.getBody();
    softAssert.assertEquals(body.asString(), SCHEMA_NOT_FOUND);
    softAssert.assertAll();
  }

  public void getHeadersValidation(Response response){
    softAssert.assertEquals(response.getStatusCode(),400, CORRECT_STATUS_CODE_MESSAGE);
    ResponseBody body=response.getBody();
    softAssert.assertEquals(body.asString(), DEPARTMENT_ID_MISSING);
    softAssert.assertAll();
  }

  public List<String> getCreatePayloadFields(String code) throws IOException {
    List<String> keys = new ArrayList<>();
    JsonNode jsonNode = new ObjectMapper().readValue(getCreatePayload(code), JsonNode.class);
    Iterator<String> iterator = jsonNode.fieldNames();
    iterator.forEachRemaining(e -> keys.add(e));
    keys.remove("enterprise_id");
    return keys;
  }

  public Boolean compareKey(JsonNode jsonNode, List<String> keys) {
    log.info("compare key function");
    return keys.stream().allMatch(key -> !jsonNode.has(key));
  }

  public String mapToJsonNode(Map<String, Object> map) {
    return String.valueOf(objectMapper.valueToTree(map));
  }

  public JsonNode getJsonNode(String value) throws JsonProcessingException {
    return objectMapper.readTree(value);
  }

  public String getCreatePayload(String code) throws IOException {
    return automationUtility.readFile(JSON_PATH + code + VALID_JSON);
  }

  public Map<String, Map<String, String>> createSetupPreCondition(String accessToken)
      throws IOException {

    List<PartsSetupCode> setupCodeList = new ArrayList<>(FUS_Implemented);

    Object[][] setups = new Object[setupCodeList.size()][];

    int counter = 0;
    for (PartsSetupCode ele : setupCodeList) {
      setups[counter] = new Object[]{ele.name()};
      Map<String, String> ids = new HashMap<>();
      final String createPayload = automationUtility.readFile(JSON_PATH + ele.name() + VALID_JSON);
      try {
        createResponse = getResponse(createPayload, SetUpsEndpoints.createSetupsURL(ele.name()),
            Method.POST, getBaseCUDServicePath(), accessToken, false);
        createSetup(createResponse, ele.name());
        if (createResponse.getStatusCode() == 409) {
          Map<String, Object> uniqueFieldValues = getUniqueKVMap(ele.name());
          List<Map<String, Object>> filters = convertToFilterList(uniqueFieldValues);
          String searchPayload = "{ \"filters\": " + objectMapper.writeValueAsString(filters) + " }";
          final Response readResponse = getResponse(
                  searchPayload, SetUpsEndpoints.getSearchSetupsURL(ele.name()),
                  Method.POST, getBaseReadServicePath(), accessToken, false);
//          JsonNode jsonNode = objectMapper.readTree(createPayload);
//          List<Map<String, Object>> uniqueFieldAndValue =
//                  convertToFilterList(getResolvedUniqueKVMap(ele.name(), jsonNode, null, null, false));
//          final Response readResponse = getResponse(
//              SEARCH_SETUPS_BY_SPECIFIC_SEARCH_CRITERIA_PAYLOAD + objectMapper.writeValueAsString(
//                  uniqueFieldAndValue) + "}", SetUpsEndpoints.getSearchSetupsURL(ele.name()),
//              Method.POST, getBaseReadServicePath(), accessToken, false);
          final String id = String.valueOf(readResponse.getBody().as(JsonNode.class).get("data").get(0).get(ID));
          ids.put(ID, id.substring(1, id.length() - 1));
          setupIds.put(ele.name(), ids);
        } else if (createResponse.getStatusCode() == 201) {
          final JsonNode idJson = createResponse.getBody().as(JsonNode.class).get("data").get(ID);
          final String id = String.valueOf(idJson);
          ids.put(ID, id.substring(1, id.length() - 1));
          setupIds.put(ele.name(), ids);
        }
      } catch (Exception e)  {
        log.error(ERROR_WHILE_CALLING_NEUTRON_API_MESSAGE, e.getMessage());
      }
      counter++;
    }
    return setupIds;
  }

  public void clearTestDataPostCondition(String accessToken) {
    List<PartsSetupCode> setupCodeList = new ArrayList<>(FUS_Implemented);
    Object[][] setups = new Object[setupCodeList.size()][];
    int counter = 0;
    for (PartsSetupCode ele : setupCodeList) {
      setups[counter] = new Object[]{ele.name()};
      try {
         clearSetUpsTestData(
            getResponse(DELETE_BY_CRITERIA_PAYLOAD + setupIds.get(ele.name()).get(ID) + "\"}",
                SetUpsEndpoints.deleteSetupsByCriteria(ele.name()), Method.PUT,
                getBaseCUDServicePath(), accessToken, false));
      } catch (Exception e) {
        log.error(ERROR_WHILE_CALLING_NEUTRON_API_MESSAGE, e.getMessage());
        clearSetUpsTestData(
            getResponse("{}", SetUpsEndpoints.deleteAllSetupsTestData(ele.name()), Method.PUT,
                getBaseCUDServicePath(), accessToken, false));
      }
      counter++;
    }
  }

  public Map<String, Object> getUniqueKVMap(String code) throws IOException {
    String payload = automationUtility.readFile(JSON_PATH + code + VALID_JSON);
    JsonNode setupDataJson = objectMapper.readValue(payload, JsonNode.class);
    return getResolvedUniqueKVMap(code, setupDataJson, getEnterpriseId(), getStoreId(), true);
  }

  private List<String> resolveUniqueFields(String functionalUnit, JsonNode setupDataJson, Map<String, Object> criteriaMap) {
    JsonNode recordType = setupDataJson.findValue("record_type");
    Object criteria = criteriaMap.get(functionalUnit);

    if (criteria == null) return null;

    try {
      if (criteria instanceof List) {
        return (List<String>) criteria;
      } else if (criteria instanceof Map && recordType != null) {
        return (List<String>) ((Map<?, ?>) criteria).get(recordType.textValue());
      }
    } catch (ClassCastException e) {
      log.warn("Failed to cast unique criteria fields for functionalUnit: {}", functionalUnit);
    }
    return null;
  }

  private Map<String, Object> extractFieldValues(JsonNode setupDataJson, List<String> fields) {
    Map<String, Object> fieldValues = new HashMap<>();
    Map<String, Object> setupDataMap = objectMapper.convertValue(setupDataJson, Map.class);

    for (String field : fields) {
      fieldValues.put(field, setupDataMap.get(field));
    }

    return fieldValues;
  }

  public List<Map<String, Object>> convertToFilterList(Map<String, Object> fieldValueMap) {
    return fieldValueMap.entrySet().stream()
            .map(entry -> {
              Map<String, Object> filter = new HashMap<>();
              filter.put("field", entry.getKey());
              filter.put("operator", "EQUALS");
              filter.put("value", entry.getValue());
              return filter;
            })
            .collect(Collectors.toList());
  }


  public Map<String, Object> getResolvedUniqueKVMap(String functionalUnit, JsonNode setupDataJson,
                                                    String enterpriseId, String storeId, boolean useFallback) throws IOException {
    Map<String, Object> uniqueCriteriaMap = objectMapper.readValue(
            automationUtility.readFile("classpath:/UniqueCriteria.json"),
            HashMap.class
    );

    List<String> uniqueFields = resolveUniqueFields(functionalUnit, setupDataJson, uniqueCriteriaMap);

    Map<String, Object> fieldValueMap;

    if (uniqueFields != null && !uniqueFields.isEmpty()) {
      fieldValueMap = extractFieldValues(setupDataJson, uniqueFields);
    } else if (useFallback) {
      fieldValueMap = Map.of("enterprise_id", enterpriseId, "store_id", storeId);
    } else {
      fieldValueMap = new HashMap<>();
    }

    return fieldValueMap;
  }

  public String fetchAccessToken() {
    final JsonNode block = WebClient
        .create(getAuthUrl())
        .put().contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", getRefreshToken())
        .body(BodyInserters.fromValue(
            "{\"tokenRefresh\":{\"refreshToken\":\"" + getRefreshToken() + "\"}}")
        ).accept(MediaType.ALL).retrieve()
        .bodyToMono(JsonNode.class).block();
    return block.get("accessToken") != null ? block.get("accessToken").textValue() : null;
  }

  public void clearSetUpsTestData(Response response) {
    HashMap<String, Integer> data = response.jsonPath().get(); //(HashMap<String, Integer>)
    log.info("Data response is " + data);
    log.info(response.getBody().prettyPrint());
  }

  private Object getData(Response response) {
    HashMap<String, Object> setupsResponse = response.jsonPath().get();
    softAssert.assertNotNull(setupsResponse.get(MESSAGE));
    softAssert.assertAll();
    return setupsResponse.get(DATA);
  }

}
