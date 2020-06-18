package com.naskoni.library.service.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper {

  private Helper() {}

  public static Matcher getMatcher(String search) {
    Pattern pattern = Pattern.compile("(\\w+?)(:|<|>)(\\w+?),");
    return pattern.matcher(search + ",");
  }
}
