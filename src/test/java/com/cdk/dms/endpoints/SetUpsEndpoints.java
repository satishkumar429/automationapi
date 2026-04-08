package com.cdk.dms.endpoints;

public class SetUpsEndpoints {


  public static String createSetupsURL(String functionalUnit) {
    return functionalUnit;


  public static String genericReadSetups(String functionalUnit) {
    return functionalUnit;
  }

  public static String getSearchSetupsURL(String functionalUnit) {
    return "/search/" + functionalUnit;
  }

  public static String getRecordExistsURL(String functionalUnit) {
    return "/exists/" + functionalUnit;
  }

  public static String deleteSetupsByCriteria(String functionalUnit) {
    return "/delete-by-criteria/"+functionalUnit;
  }

  public static String deleteAllSetupsTestData(String functionalUnit) {
    return functionalUnit;
  }
}
