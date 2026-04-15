package com.cdk.dms.constants;


import com.cdk.dms.setups.common.domain.PartsSetupCode;
import com.cdk.dms.tests.BaseSteps;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Constants extends BaseSteps {


  //test data files path
  public static final String JSON_PATH = "classpath:/testData/";
  public static final String VALID_JSON = "/valid.json";

  public static final String CORRECT_STATUS_CODE_MESSAGE = "Response Status Code is : ";

  public static final String SEARCH_PAYLOAD = "{\"filters\":[{\"field\":\"active_status\",\"operator\":\"EQUALS\",\"value\":\"active\"}]}";

  public static final String SEARCH_SETUPS_BY_SPECIFIC_RESPONSE_FIELDS_NO_SEARCH_PAYLOAD = "{\"filters\":[{\"field\":\"enterprise_id\",\"operator\":\"EQUALS\",\"value\":\"E339440\"}],\"responseFields\":[\"enterprise_id\"]}";

  public static final String SEARCH_SETUPS_BY_SPECIFIC_SEARCH_CRITERIA_PAYLOAD = "{\"filters\":";

  public static final String SEARCH_SETUPS_BY_INVALID_RESPONSE_FIELDS_PAYLOAD ="{\"filters\":[{\"field\":\"store\",\"operator\":\"EQUALS\",\"value\":\"S100119367\"}],\"responseFields\":[\"store\"]}";

  public static final String SEARCH_SETUPS_BY_MULTIPLE_INVALID_RESPONSE_FIELDS_PAYLOAD ="{\"filters\":[{\"field\":\"store\",\"operator\":\"EQUALS\",\"value\":\"S100119367\"}],\"responseFields\":[\"store\",\"enterprise\"]}";

  public static final String SEARCH_SETUPS_BY_VALIDATION_ERROR_FIELDS_PAYLOAD = "{\"filters\":[{\"field\":\"active_status\",\"operator\":\"EQUALS\",\"value\":\"wrong\"}]}";

  public static final String BAD_REQUEST_INVALID_FIELDS = "{\"message\":\"Following Field(s) are invalid\",\"data\":[\"store\"]}";

  public static final String BAD_REQUEST_MULTIPLE_INVALID_FIELDS = "{\"message\":\"Following Field(s) are invalid\",\"data\":[\"enterprise\",\"store\"]}";

  public static final String VALIDATION_ERROR = "{\"message\":\"Validation Error\",\"data\":[";

  public static final String SEARCH_SETUPS_BY_EMPTY_FILTERS_PAYLOAD = "{\"filters\":[]}";
  public static final String FILTERS_MISSING = "{\"message\":\"filters are missing, please provide valid 'filters'\",\"data\":null}";
  public static Set<String> FIELDS_TO_IGNORE = new HashSet<>(Arrays
      .asList("created_datetime","updated_datetime", "lastupdated_by", "lastupdated_datetime",
          "serial_no", "record_type", "tenant_id", "enterprise_id", "store_id", "department_id" , "department_name" , "id", "created_at",
          "updated_at", "created_by", "updated_by"));
  public static final String MESSAGE = "message";
  public static final String DATA = "data";
  public static final String ERROR_WHILE_CALLING_NEUTRON_API_MESSAGE = "Error while calling Neutron API, message: {}";
  public static final String DELETE_BY_CRITERIA_PAYLOAD = "{\"id\":\"";
  public static final String SERIAL_NO = "serial_no";
  public static final String ID = "id";
  public static final String RANDOM_STRING="test";

  public static final String SCHEMA_NOT_FOUND = "{\"message\":\"Schema not found for given Functional unit\",\"data\":null}";

  public static final String DEPARTMENT_ID_MISSING ="{\"message\":\"Department ID is required\",\"data\":null}";
  public static final Set<PartsSetupCode> FUS_Implemented = Set

      .of(
//              PartsSetupCode.DictPartNo,
//              PartsSetupCode.InventoryBin,
//              PartsSetupCode.PartsMasterUpdateMap,
//              PartsSetupCode.SaleFile,
//              PartsSetupCode.PriorityCodes,
//              PartsSetupCode.TaxAgencies,
//              PartsSetupCode.TaxAgencyInfo,
//              PartsSetupCode.TaxGroup,
              PartsSetupCode.PartsPriceCode
//              PartsSetupCode.ROPriceCode,
//              PartsSetupCode.PartsFeeCodes,
//              PartsSetupCode.SOSOptions,
//              PartsSetupCode.OrderPads,
//              PartsSetupCode.CreditMemoOptions,
//              PartsSetupCode.CreditMemoReasons

      );
}

