
package com.mhwirth.riglogger.proasenseadapter;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.mhwirth.riglogger.proasenseadapter package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.mhwirth.riglogger.proasenseadapter
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetArchivedValues }
     * 
     */
    public GetArchivedValues createGetArchivedValues() {
        return new GetArchivedValues();
    }

    /**
     * Create an instance of {@link GetArchivedValuesResponse }
     * 
     */
    public GetArchivedValuesResponse createGetArchivedValuesResponse() {
        return new GetArchivedValuesResponse();
    }

    /**
     * Create an instance of {@link ArrayOfMeasurement }
     * 
     */
    public ArrayOfMeasurement createArrayOfMeasurement() {
        return new ArrayOfMeasurement();
    }

    /**
     * Create an instance of {@link Measurement }
     * 
     */
    public Measurement createMeasurement() {
        return new Measurement();
    }

}
