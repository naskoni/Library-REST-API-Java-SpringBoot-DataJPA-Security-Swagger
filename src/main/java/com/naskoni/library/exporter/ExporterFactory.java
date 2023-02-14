package com.naskoni.library.exporter;

import com.naskoni.library.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * @author Atanas Atanasov
 * @version 1.0.0
 */
@Slf4j
@Component
public class ExporterFactory {

  private static final String PACKAGE_NAME = "com.naskoni.library.exporter.";
  private Map<String, Class<? extends Exporter>> classMap = new HashMap<>();
  private URLClassLoader loader;

  @Value("${dir.classes}")
  private String pluginPath;

  /**
   * Creates a {@link Exporter} child instance for the specified type.
   *
   * @param type the Exporter type value corresponding to one of Exporter children
   * @return the Exporter child instance for the corresponding type
   * @throws {@link NotFoundException} if the Exporter type is not supported
   */
  public Exporter newInstance(String type) {
    if (classMap.containsKey(type)) {
      try {
        return classMap.get(type).getDeclaredConstructor().newInstance();
      } catch (InstantiationException
          | IllegalAccessException
          | NoSuchMethodException
          | InvocationTargetException e) {
        log.error(e.getMessage(), e);
      }
    }

    throw new NotFoundException(String.format("No exporter is registered for this type: %s", type));
  }

  public Set<String> getTypes() {
    return classMap.keySet();
  }

  @PostConstruct
  void init() {
    fillMapWithDefaultExporterClasses();

    File directory = new File(pluginPath);
    URI uri = directory.toURI();
    try {
      URL url = uri.toURL();
      loader = URLClassLoader.newInstance(new URL[] {url});
      try {
        List<Class<? extends Exporter>> pluginClasses = getExporterClasses(directory);
        fillClassMap(pluginClasses);
      } finally {
        closeLoader();
      }
    } catch (MalformedURLException e) {
      log.error(e.getMessage(), e);
    }
  }

  private void fillMapWithDefaultExporterClasses() {
    List<Class<? extends Exporter>> classes = new ArrayList<>(Arrays.asList(CsvFileExporter.class));
    fillClassMap(classes);
  }

  private List<Class<? extends Exporter>> getExporterClasses(File directory) {
    List<Class<? extends Exporter>> classes = new ArrayList<>();
    if (directory.exists() && directory.isDirectory()) {
      File[] files = directory.listFiles((d, n) -> n.endsWith("class"));
      for (File file : files) {
        String className = FilenameUtils.removeExtension(file.getName());
        try {
          Class<?> clazz = loader.loadClass(PACKAGE_NAME + className);
          if (Exporter.class.isAssignableFrom(clazz)) {
            // This cast is correct because we have already proved it
            Class<Exporter> exporterClass = (Class<Exporter>) clazz;
            classes.add(exporterClass);
          }
        } catch (ClassNotFoundException e) {
          log.error(e.getMessage(), e);
        }
      }
    } else {
      log.warn(
          "The provided directory for key <dir.classes> in <application.properties>: <{}> does not exist",
          pluginPath);
    }

    return classes;
  }

  private void fillClassMap(List<Class<? extends Exporter>> classes) {
    for (Class<? extends Exporter> clazz : classes) {
      try {
        Exporter exporter = clazz.getDeclaredConstructor().newInstance();
        classMap.put(exporter.getType(), clazz);
      } catch (InstantiationException
          | IllegalAccessException
          | NoSuchMethodException
          | InvocationTargetException e) {
        log.error(e.getMessage(), e);
      }
    }
  }

  private void closeLoader() {
    try {
      loader.close();
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
  }
}
