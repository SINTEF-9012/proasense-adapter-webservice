package com.mhwirth.riglogger.proasenseadapter;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 3.1.2
 * 2015-09-16T17:28:23.152+02:00
 * Generated source version: 3.1.2
 * 
 */
@WebService(targetNamespace = "http://riglogger.mhwirth.com/ProasenseAdapter/", name = "Service1Soap")
@XmlSeeAlso({ObjectFactory.class})
public interface Service1Soap {

    @WebMethod(operationName = "GetArchivedValues", action = "http://riglogger.mhwirth.com/ProasenseAdapter/GetArchivedValues")
    @RequestWrapper(localName = "GetArchivedValues", targetNamespace = "http://riglogger.mhwirth.com/ProasenseAdapter/", className = "com.mhwirth.riglogger.proasenseadapter.GetArchivedValues")
    @ResponseWrapper(localName = "GetArchivedValuesResponse", targetNamespace = "http://riglogger.mhwirth.com/ProasenseAdapter/", className = "com.mhwirth.riglogger.proasenseadapter.GetArchivedValuesResponse")
    @WebResult(name = "GetArchivedValuesResult", targetNamespace = "http://riglogger.mhwirth.com/ProasenseAdapter/")
    public ArrayOfMeasurement getArchivedValues(
            @WebParam(name = "signalId", targetNamespace = "http://riglogger.mhwirth.com/ProasenseAdapter/")
            String signalId,
            @WebParam(name = "from", targetNamespace = "http://riglogger.mhwirth.com/ProasenseAdapter/")
            javax.xml.datatype.XMLGregorianCalendar from,
            @WebParam(name = "to", targetNamespace = "http://riglogger.mhwirth.com/ProasenseAdapter/")
            javax.xml.datatype.XMLGregorianCalendar to
    );
}
