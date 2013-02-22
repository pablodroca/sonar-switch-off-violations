package org.sonar.plugins.cleanviolations;
 
import org.sonar.api.web.*;
@WidgetCategory("Globant")
@WidgetProperties({
  @WidgetProperty(key = "max", type = WidgetPropertyType.INTEGER, defaultValue = "80")
})
public final class CleanViolationsWidget extends AbstractRubyTemplate implements RubyRailsWidget {
  public String getId() {
    return "clean_violations_widget";
  }
  public String getTitle() {
    return "Clean Violations Widget";
  }
  protected String getTemplatePath() {
    return "/org/sonar/plugins/cleanviolations/clean_violations_widget.html.erb";
  }
}