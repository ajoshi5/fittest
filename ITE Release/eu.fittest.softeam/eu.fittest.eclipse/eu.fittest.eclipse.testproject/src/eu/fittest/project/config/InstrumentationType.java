//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.02.10 at 12:51:31 PM CET 
//


package eu.fittest.project.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for InstrumentationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InstrumentationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ghcrtOption" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InstrumentationType", propOrder = {
    "ghcrtOption"
})
public class InstrumentationType {

    @XmlElement(required = true)
    protected String ghcrtOption;

    /**
     * Gets the value of the ghcrtOption property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGhcrtOption() {
        return ghcrtOption;
    }

    /**
     * Sets the value of the ghcrtOption property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGhcrtOption(String value) {
        this.ghcrtOption = value;
    }

}
