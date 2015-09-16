
package com.mhwirth.riglogger.proasenseadapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="GetArchivedValuesResult" type="{http://riglogger.mhwirth.com/ProasenseAdapter/}ArrayOfMeasurement" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "getArchivedValuesResult"
})
@XmlRootElement(name = "GetArchivedValuesResponse")
public class GetArchivedValuesResponse {

    @XmlElement(name = "GetArchivedValuesResult")
    protected ArrayOfMeasurement getArchivedValuesResult;

    /**
     * Gets the value of the getArchivedValuesResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfMeasurement }
     *     
     */
    public ArrayOfMeasurement getGetArchivedValuesResult() {
        return getArchivedValuesResult;
    }

    /**
     * Sets the value of the getArchivedValuesResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfMeasurement }
     *     
     */
    public void setGetArchivedValuesResult(ArrayOfMeasurement value) {
        this.getArchivedValuesResult = value;
    }

}
