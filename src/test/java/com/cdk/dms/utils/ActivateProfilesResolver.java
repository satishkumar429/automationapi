package com.cdk.dms.utils;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.test.context.ActiveProfilesResolver;

@Slf4j
public class ActivateProfilesResolver implements ActiveProfilesResolver {


  @Override
  public String[] resolve(Class<?> aClass) {
    AbstractEnvironment abstractEnvironment = new StandardEnvironment();
    final String[] activeProfiles = abstractEnvironment.getActiveProfiles();
    log.info("Active profiles are: " + Arrays.toString(activeProfiles));
    return activeProfiles;
  }
}
