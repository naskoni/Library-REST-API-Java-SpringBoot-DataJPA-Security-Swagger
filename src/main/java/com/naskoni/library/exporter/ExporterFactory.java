package com.naskoni.library.exporter;

import com.naskoni.library.exception.NotFoundException;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExporterFactory {

  public static final String NO_EXPORTER_IS_REGISTERED_FOR_THIS_TYPE = "No exporter is registered for this type: %s";
  private static final String PACKAGE_NAME = "com.naskoni.library.exporter.";
  private final Map<String, Class<? extends Exporter>> classMap = new HashMap<>();

  private URLClassLoader loader;

  @Value("${dir.classes}")
  private String pluginPath;

  /**
   * Creates an {@link Exporter} instance for the specified type.
   *
   * @param type the exporter type
   * @return the exporter instance
   * @throws NotFoundException if the exporter type is not supported
   */
  public Exporter newInstance(String type) {
    Class<? extends Exporter> exporterClass = classMap.get(type);

    if (exporterClass != null) {
      try {
        return exporterClass.getDeclaredConstructor().newInstance();
      } catch (InstantiationException
               | IllegalAccessException
               | NoSuchMethodException
               | InvocationTargetException e) {

        log.error("Could not create exporter for type: {}", type, e);
      }
    }

    throw new NotFoundException(NO_EXPORTER_IS_REGISTERED_FOR_THIS_TYPE.formatted(type));
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
      loader = URLClassLoader.newInstance(new URL[]{url});

      try {
        List<Class<? extends Exporter>> pluginClasses =
            getExporterClasses(directory);

        fillClassMap(pluginClasses);
      } finally {
        closeLoader();
      }
    } catch (MalformedURLException e) {
      log.error("Could not create URL for plugin directory: {}", pluginPath, e);
    }
  }

  private void fillMapWithDefaultExporterClasses() {
    List<Class<? extends Exporter>> classes =
        new ArrayList<>(List.of(CsvFileExporter.class));

    fillClassMap(classes);
  }

  private List<Class<? extends Exporter>> getExporterClasses(File directory) {
    List<Class<? extends Exporter>> classes = new ArrayList<>();

    if (directory.exists() && directory.isDirectory()) {

      File[] files = directory.listFiles((dir, name) -> name.endsWith(".class"));

      if (files == null) {
        return classes;
      }

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
          log.error("Could not load exporter class: {}", className, e);
        }
      }

    } else {
      log.warn(
          "The provided directory configured by <dir.classes> in <application.properties>: <{}> does not exist",
          pluginPath);
    }

    return classes;
  }

  private void fillClassMap(List<Class<? extends Exporter>> classes) {
    for (Class<? extends Exporter> clazz : classes) {
      try {
        Exporter exporter =
            clazz.getDeclaredConstructor().newInstance();

        classMap.put(exporter.getType(), clazz);

      } catch (InstantiationException
               | IllegalAccessException
               | NoSuchMethodException
               | InvocationTargetException e) {

        log.error("Could not instantiate exporter class: {}", clazz.getName(), e);
      }
    }
  }

  private void closeLoader() {
    if (loader == null) {
      return;
    }

    try {
      loader.close();
    } catch (IOException e) {
      log.error("Could not close exporter class loader", e);
    }
  }
}