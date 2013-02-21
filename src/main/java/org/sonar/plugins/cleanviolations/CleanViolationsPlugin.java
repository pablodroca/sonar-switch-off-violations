package org.sonar.plugins.cleanviolations;

import com.google.common.collect.ImmutableList;
import org.sonar.api.BatchExtension;
import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.PropertyField;
import org.sonar.api.PropertyType;
import org.sonar.api.SonarPlugin;

import java.util.List;

public final class CleanViolationsPlugin extends SonarPlugin {

  static final int LARGE_SIZE = 20;
  static final int SMALL_SIZE = 10;

  public List<Class<? extends BatchExtension>> getExtensions() {
    return null;
  }

}
