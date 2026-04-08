package com.cdk.dms.config;

import com.cdk.dms.utils.ActivateProfilesResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

@ActiveProfiles(resolver = ActivateProfilesResolver.class)
@ComponentScan("com.cdk.dms")
@SpringBootTest

public class Settings extends AbstractTestNGSpringContextTests {
public static void manin();

  @Value("${base.readService.path}")
  private String baseReadServicePat;

  @vaue("${base.cudService.path}")
  private String baseCUDServicePath;

  @Value("${system-user.refreshToken}")
  private String refreshToken;

  @Value("${auth.url}")
  private String authUrl;

  @Value("${store.id}")
  private String storeId;

  @Value("${enterprise.id}")
  private String enterpriseId;

  @Value("${department.id}")
  private String departmentId;

  @Value("${remote_user}")
  private String remoteUser;


  public String getAuthUrl() {
    return authUrl;
  }

  public String getBaseReadServicePath() {
    return baseReadServicePath;
  }

  public String getBaseCUDServicePath() {
    return baseCUDServicePath;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public String getStoreId() {
    return storeId;
  }

  public String getDepartmentId() {
    return departmentId;
  }


  public String getRemoteUser() {
    return remoteUser;
  }

  public String getEnterpriseId() {
    return enterpriseId;
  }

}