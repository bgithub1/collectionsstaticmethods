package com.billybyte.commonstaticmethods;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * 
 * @author bperlman1
 *
 */
public class JaxbStuff {
	/**
	 * Get object of a class that was generated using the xjc tool (see the TrangXjc project) which
	 *   has JaxB annotations.
	 * @param clazz - top level class of xml file
	 * @param inputXmlFilePath 
	 * @return
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 */
	public static <V> V getFromJaxb(Class<V> clazz, String inputXmlFilePath ) throws JAXBException, FileNotFoundException{
		Unmarshaller u;
		JAXBContext jc = JAXBContext.newInstance(clazz.getPackage().getName());
		u = jc.createUnmarshaller();
		FileInputStream ins;
		ins = new FileInputStream(inputXmlFilePath);
		V topLevel = (V)u.unmarshal(ins);
		return topLevel;
	}

	public static <V> V getFromJaxb(String xmlContent,Class<V> clazz ) throws JAXBException, FileNotFoundException{
		Unmarshaller u;
		JAXBContext jc = JAXBContext.newInstance(clazz.getPackage().getName());
		u = jc.createUnmarshaller();
		InputStream ins;
		ins = new  ByteArrayInputStream(xmlContent.getBytes());
		V topLevel = (V)u.unmarshal(ins);
		return topLevel;
	}

}
