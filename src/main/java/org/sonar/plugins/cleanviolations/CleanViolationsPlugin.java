package org.sonar.plugins.cleanviolations;

import com.google.common.collect.ImmutableList;
import org.sonar.api.BatchExtension;
import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.PropertyField;
import org.sonar.api.PropertyType;
import org.sonar.api.SonarPlugin;
import org.sonar.plugins.cleanviolations.CleanViolationsWidget;

import java.util.List;


@Properties({
  @Property(
    key = "PROPERTY1",
    name = "Property 1 Name",
    description = "Property 1 is used to define the plugin behavior",
    project = true,
    global = true,
    fields = {
      @PropertyField(
        key = "PROPERTY2",
        name = "Property 2",
        description = "Property 2 description.",
        type = PropertyType.STRING,
        indicativeSize = CleanViolationsPlugin.LARGE_SIZE)})
})
public final class CleanViolationsPlugin extends SonarPlugin {

  static final int LARGE_SIZE = 20;
  static final int SMALL_SIZE = 10;

  @SuppressWarnings("unchecked")
  public List getExtensions() {
    return ImmutableList.of(
		CleanViolationsWidget.class
		);
  }

}
