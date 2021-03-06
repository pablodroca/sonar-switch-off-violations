/*
 * Sonar Switch Off Violations Plugin
 * Copyright (C) 2011 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.switchoffviolations.pattern;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.sonar.api.utils.SonarException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PatternDecoder {

  private static final int THREE_FIELDS_PER_LINE = 3;
  private static final String LINE_RANGE_REGEXP = "\\[((\\d+|\\d+-\\d+),?)*\\]";

  public List<Pattern> decode(String patternsList) {
    List<Pattern> patterns = Lists.newLinkedList();
    String[] patternsLines = StringUtils.split(patternsList, "\n");
    for (String patternLine : patternsLines) {
      Pattern pattern = decodeLine(patternLine.trim());
      if (pattern != null) {
        patterns.add(pattern);
      }
    }
    return patterns;
  }

  public List<Pattern> decode(File file) {
    try {
      List<String> lines = FileUtils.readLines(file);
      List<Pattern> patterns = Lists.newLinkedList();
      for (String line : lines) {
        Pattern pattern = decodeLine(line);
        if (pattern != null) {
          patterns.add(pattern);
        }
      }
      return patterns;

    } catch (IOException e) {
      throw new SonarException("Fail to load the file: " + file.getAbsolutePath(), e);
    }
  }

  /**
   * Main method that decodes a line which defines a pattern
   */
  public Pattern decodeLine(String line) {
    if (isBlankOrComment(line)) {
      return null;
    }

    String[] fields = StringUtils.splitPreserveAllTokens(line, ';');
    if (fields.length > THREE_FIELDS_PER_LINE) {
      throw new SonarException("Invalid format. The following line has more than 3 fields separated by comma: " + line);
    }

    Pattern pattern;
    if (fields.length == THREE_FIELDS_PER_LINE) {
      checkRegularLineConstraints(line, fields);
      pattern = new Pattern(StringUtils.trim(fields[0]), StringUtils.trim(fields[1]));
      decodeRangeOfLines(pattern, fields[2]);
    } else if (fields.length == 2) {
      checkDoubleRegexpLineConstraints(line, fields);
      pattern = new Pattern().setBeginBlockRegexp(fields[0]).setEndBlockRegexp(fields[1]);
    } else {
      pattern = new Pattern().setAllFileRegexp(fields[0]);
    }

    return pattern;
  }

  private void checkRegularLineConstraints(String line, String[] fields) {
    if (!isResource(fields[0])) {
      throw new SonarException("Invalid format. The first field does not define a resource pattern: " + line);
    }
    if (!isRule(fields[1])) {
      throw new SonarException("Invalid format. The second field does not define a rule pattern: " + line);
    }
    if (!isLinesRange(fields[2])) {
      throw new SonarException("Invalid format. The third field does not define a range of lines: " + line);
    }
  }

  private void checkDoubleRegexpLineConstraints(String line, String[] fields) {
    if (!isRegexp(fields[0])) {
      throw new SonarException("Invalid format. The first field does not define a regular expression: " + line);
    }
    if (!isRegexp(fields[1])) {
      throw new SonarException("Invalid format. The second field does not define a regular expression: " + line);
    }
  }

  public static void decodeRangeOfLines(Pattern pattern, String field) {
    if (StringUtils.equals(field, "*")) {
      pattern.setCheckLines(false);
    } else {
      pattern.setCheckLines(true);
      String s = StringUtils.substringBetween(StringUtils.trim(field), "[", "]");
      String[] parts = StringUtils.split(s, ',');
      for (String part : parts) {
        if (StringUtils.contains(part, '-')) {
          String[] range = StringUtils.split(part, '-');
          pattern.addLineRange(Integer.valueOf(range[0]), Integer.valueOf(range[1]));
        } else {
          pattern.addLine(Integer.valueOf(part));
        }
      }
    }
  }

  @VisibleForTesting
  boolean isLinesRange(String field) {
    return StringUtils.equals(field, "*") || java.util.regex.Pattern.matches(LINE_RANGE_REGEXP, field);
  }

  @VisibleForTesting
  boolean isBlankOrComment(String line) {
    return StringUtils.isBlank(line) || StringUtils.startsWith(line, "#");
  }

  @VisibleForTesting
  boolean isResource(String field) {
    return StringUtils.isNotBlank(field);
  }

  @VisibleForTesting
  boolean isRule(String field) {
    return StringUtils.isNotBlank(field);
  }

  @VisibleForTesting
  boolean isRegexp(String field) {
    return StringUtils.isNotBlank(field);
  }
}
