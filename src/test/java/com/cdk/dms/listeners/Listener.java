package com.cdk.dms.listeners;

import java.util.List;
import org.testng.IAlterSuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.xml.XmlSuite;

public class Listener implements IAlterSuiteListener, ITestListener {

  @Override
  public void alter(List<XmlSuite> suites) {
    String groupName = System.getProperty("groups");
    String suiteName = "";
    if (groupName.equalsIgnoreCase("functional")) {
      suiteName = "Functional";
    } else if (groupName.equalsIgnoreCase("regression")) {
      suiteName = "Regression";
    } else {
      suiteName = "Integration";
    }
    suites.get(0).setName(suiteName + "-API-TestSuite");
  }

  @Override
  public void onStart(ITestContext context) {
    String groupName = System.getProperty("groups");
    String suiteName = "";
    if (groupName.equalsIgnoreCase("functional")) {
      suiteName = "Functional";
    } else if (groupName.equalsIgnoreCase("regression")) {
      suiteName = "Regression";
    } else {
      suiteName = "Integration";
    }
    context.getCurrentXmlTest().setName(suiteName + "-API-TestCases");
  }

}

