package com.naskoni.library.exporter;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.naskoni.library.entity.Book;

import java.io.IOException;
import java.util.List;

/**
 * This implementation of Exporter is exemplary to demonstrate the ability of the application to
 * expand. The compiled class is placed in the 'pluginClasses' directory, whose location is defined
 * in application.properties and will be loaded via classloader when the application is started.
 *
 * @author Atanas Atanasov
 * @version 1.0.0
 */
public class XmlFileExporter implements Exporter {

  private static final String XML = "xml";

  @Override
  public String getType() {
    return XML;
  }

  @Override
  public byte[] export(List<Book> books) throws IOException {
    XmlMapper xmlMapper = new XmlMapper();
    xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);

    return xmlMapper.writeValueAsBytes(books);
  }
}
