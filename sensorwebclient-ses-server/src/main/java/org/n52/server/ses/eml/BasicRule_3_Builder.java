/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */
package org.n52.server.ses.eml;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.n52.client.view.gui.elements.layouts.SimpleRuleType;
import org.n52.server.ses.SesConfig;
import org.n52.server.ses.hibernate.HibernateUtil;
import org.n52.server.ses.util.RulesUtil;
import org.n52.server.ses.util.SESUnitConverter;
import org.n52.shared.serializable.pojos.BasicRule;
import org.n52.shared.serializable.pojos.Rule;
import org.n52.shared.serializable.pojos.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author <a href="mailto:osmanov@52north.org">Artur Osmanov</a>
 *
 */
public class BasicRule_3_Builder {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicRule_3_Builder.class);

    // TAG NAMES
    final static String propertyValue = "value";
    final static String simplePattern = "SimplePattern";
    final static String complexPattern = "ComplexPattern";
    final static String selectFunction = "SelectFunction";
    final static String patternReference = "PatternReference";
    final static String selectEvent = "SelectEvent";
    final static String eventCount = "EventCount";
    final static String fesFilter = "fes:Filter";
    final static String valuereference = "fes:ValueReference";
    final static String fesLiteral = "fes:Literal";

    // ATTRIBUTE NAMES
    final static String patternID = "patternID";
    final static String newEventName = "newEventName";
    final static String eventName = "eventName";
    final static String outputName = "outputName";

    /**
     * Sum over Time
     * 
     * This method builds the rule type "Sum over Time" by loading and filling a template file.
     * The location of this file is defined in /properties/ses-client.properties 
     * in the variable "resLocation". File name must be BR_3.xml.
     * 
     * @param rule
     * @return {@link BasicRule}
     * @throws Exception
     */
    public static BasicRule create_BR_3(Rule rule) throws Exception{
    	
    	// Get current user. This user is also the owner of the new rule
        User user = HibernateUtil.getUserByID(rule.getUserID());

        // VARIABLES
        String eml;
        String finalEml;
        String title = rule.getTitle();

        // Pre-defined pattern IDs and event names. All names start with the title of the rule.
        // This is important to have unique names.
        ArrayList<String> simplePatternID = new ArrayList<String>();
        simplePatternID.add(title + "_sum_over_time_stream");

        ArrayList<String> simpleNewEventName = new ArrayList<String>();
        simpleNewEventName.add(title + "_sum_over_time");

        ArrayList<String> complexPatternID = new ArrayList<String>();
        complexPatternID.add(title + "_sum_overshoot_stream");
        complexPatternID.add(title + "_sum_undershoot_stream");
        complexPatternID.add(title + "_overshoot_notification_stream");
        complexPatternID.add(title + "_undershoot_notification_stream");

        ArrayList<String> complexNewEventName = new ArrayList<String>();
        complexNewEventName.add(title + "_sum_overshoot");
        complexNewEventName.add(title + "_sum_undershoot");
        complexNewEventName.add(title + "_overshoot_notification");
        complexNewEventName.add(title + "_undershoot_notification");

        ArrayList<String> complexOutputname = new ArrayList<String>();
        complexOutputname.add(title + "_overshoot_output");
        complexOutputname.add(title + "_undershoot_output");

        // This ArrayList defines the references in the PatternReference tags. 
        // The references are ordered from first to the last pattern.
        ArrayList<String> patternReferenceText = new ArrayList<String>();
        patternReferenceText.add(simplePatternID.get(0));
        patternReferenceText.add(simplePatternID.get(0));
        patternReferenceText.add(simplePatternID.get(0));
        patternReferenceText.add(simplePatternID.get(0));
        patternReferenceText.add(complexPatternID.get(1));
        patternReferenceText.add(complexPatternID.get(0));
        patternReferenceText.add(complexPatternID.get(0));
        patternReferenceText.add(complexPatternID.get(1));

        // URL adress of the BR_3.xml file
        URL url = new URL(SesConfig.resLocation_3);

        // build document
        DocumentBuilderFactory docFac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFac.newDocumentBuilder();
        Document doc = docBuilder.parse(url.openStream());

        // transformer for final output
        Transformer transormer = TransformerFactory.newInstance().newTransformer();
        transormer.setOutputProperty(OutputKeys.INDENT, "yes");

        // parse <SimplePattern>
        NodeList simplePatternList = doc.getElementsByTagName(simplePattern);
        for (int i = 0; i < simplePatternList.getLength(); i++) {
            Node fstNode = simplePatternList.item(i);
            Element fstElement = (Element) fstNode;

            // set patternID
            Node patternIdSimple = simplePatternList.item(i);
            patternIdSimple.getAttributes().getNamedItem(patternID).setTextContent(simplePatternID.get(i));

            // set newEventName of SelectFunction
            NodeList selectFunctionList = fstElement.getElementsByTagName(selectFunction);
            Node selectFunctionNode = selectFunctionList.item(0);
            selectFunctionNode.getAttributes().getNamedItem(newEventName).setTextContent(simpleNewEventName.get(i));

            // set propertyRestrictions
            NodeList propertyRestrictiosnList = fstElement.getElementsByTagName(propertyValue);
            Node value_1 = propertyRestrictiosnList.item(0);
            value_1.setTextContent(rule.getPhenomenon());
            Node value_2 = propertyRestrictiosnList.item(1);
            value_2.setTextContent(rule.getStation());

            // set EventCount
            NodeList eventCountList = fstElement.getElementsByTagName(eventCount);
            Node eventCountNode = eventCountList.item(0);
            eventCountNode.setTextContent(rule.getEntryTime());
        }

        //parse <ComplexPatterns>
        NodeList complexPatternList = doc.getElementsByTagName(complexPattern);
        for (int i = 0; i < complexPatternList.getLength(); i++) {
            Node fstNode = complexPatternList.item(i);
            Element fstElement = (Element) fstNode;

            // set patternID
            Node patternIdNode = complexPatternList.item(i);
            patternIdNode.getAttributes().getNamedItem(patternID).setTextContent(complexPatternID.get(i));

            // set newEventName of SelectFunction
            NodeList selectFunctionList = fstElement.getElementsByTagName(selectFunction);
            Node selectFunctionNode = selectFunctionList.item(0);
            selectFunctionNode.getAttributes().getNamedItem(newEventName).setTextContent(complexNewEventName.get(i));
            if (selectFunctionNode.getAttributes().getNamedItem(outputName) != null) {
                selectFunctionNode.getAttributes().getNamedItem(outputName).setTextContent(complexOutputname.get(i-2));
            }

            // set <PatternReference> in the right order
            NodeList patterReferenceList = fstElement.getElementsByTagName(patternReference);
            Node patterReferenceNode = patterReferenceList.item(0);
            patterReferenceNode.setTextContent(patternReferenceText.get(2 * i));
            Node patterReferenceNode2 = patterReferenceList.item(1);
            patterReferenceNode2.setTextContent(patternReferenceText.get((2 * i) + 1));

            // set eventName of selectEvent
            NodeList selectEventList = fstElement.getElementsByTagName(selectEvent);
            if (selectEventList.getLength() != 0) {
                Node selectEventNode = selectEventList.item(0);
                selectEventNode.getAttributes().getNamedItem(eventName).setTextContent(simpleNewEventName.get(0));
            }

            // set <fes:Filter>
            NodeList filterList = doc.getElementsByTagName(fesFilter);
            for (int j = 0; j < filterList.getLength(); j++) {

                Node n = filterList.item(j);

                Node filterNode = null;
                Node filterNode2 = null;

                // first filter
                if (rule.getEntryOperatorIndex() == Rule.LESS_THAN) {
                    filterNode = doc.createElement("fes:PropertyIsLessThan");
                } else if (rule.getEntryOperatorIndex() == Rule.GREATER_THAN) {
                    filterNode = doc.createElement("fes:PropertyIsGreaterThan");
                } else if (rule.getEntryOperatorIndex() == Rule.EQUAL_TO) {
                    filterNode = doc.createElement("fes:PropertyIsEqualTo");
                } else if (rule.getEntryOperatorIndex() == Rule.GREATER_THAN_OR_EQUAL_TO) {
                    filterNode = doc.createElement("fes:PropertyIsGreaterThanOrEqualTo");
                } else if (rule.getEntryOperatorIndex() == Rule.LESS_THAN_OR_EQUAL_TO) {
                    filterNode = doc.createElement("fes:PropertyIsLessThanOrEqualTo");
                } else if (rule.getEntryOperatorIndex() == Rule.NOT_EQUAL_TO) {
                    filterNode = doc.createElement("fes:PropertyIsNotEqualTo");
                }

                // second filter
                if (rule.getExitOperatorIndex() == Rule.LESS_THAN) {
                    filterNode2 = doc.createElement("fes:PropertyIsLessThan");
                } else if (rule.getExitOperatorIndex() == Rule.GREATER_THAN) {
                    filterNode2 = doc.createElement("fes:PropertyIsGreaterThan");
                } else if (rule.getExitOperatorIndex() == Rule.EQUAL_TO) {
                    filterNode2 = doc.createElement("fes:PropertyIsEqualTo");
                } else if (rule.getExitOperatorIndex() == Rule.GREATER_THAN_OR_EQUAL_TO) {
                    filterNode2 = doc.createElement("fes:PropertyIsGreaterThanOrEqualTo");
                } else if (rule.getExitOperatorIndex() == Rule.LESS_THAN_OR_EQUAL_TO) {
                    filterNode2 = doc.createElement("fes:PropertyIsLessThanOrEqualTo");
                } else if (rule.getExitOperatorIndex() == Rule.NOT_EQUAL_TO) {
                    filterNode2 = doc.createElement("fes:PropertyIsNotEqualTo");
                }

                Node valueReferenceNode = doc.createElement(valuereference);
                valueReferenceNode.setTextContent(simpleNewEventName.get(0) + "/doubleValue");

                // Unit Conversion
                SESUnitConverter converter = new SESUnitConverter();
                Object[] resultrUnit = converter.convert(rule.getEntryUnit(), Double.valueOf(rule.getEntryValue()));
                Object[] resultcUnit = converter.convert(rule.getExitUnit(), Double.valueOf(rule.getExitValue()));

                Node fesLiteralNode = doc.createElement(fesLiteral);

                // add first filter to document
                if ((j == 0) && (i == 0)) {
                    fesLiteralNode.setTextContent(resultrUnit[1].toString());

                    if (filterNode != null) {
                        n.appendChild(filterNode);
                        filterNode.appendChild(valueReferenceNode);
                        filterNode.appendChild(fesLiteralNode);
                    }
                // add second filter to document
                } else if ((j == 1) && (i == 0)) {
                    fesLiteralNode.setTextContent(resultcUnit[1].toString());

                    if (filterNode2 != null) {
                        n.appendChild(filterNode2);
                        filterNode2.appendChild(valueReferenceNode);
                        filterNode2.appendChild(fesLiteralNode);
                    }
                }
            }
        }

        // final EML document. Convert document to string for saving in DB
        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(doc);
        transormer.transform(source, result);

        eml = result.getWriter().toString();
        finalEml = eml;
        finalEml = finalEml.substring(finalEml.indexOf("<EML"));

        return new BasicRule(rule.getTitle(), "B", "BR3", rule.getDescription(), rule.isPublish(), user.getId(),
                finalEml, false);
    }

    /**
     * 
     * This method is used to parse an EML file and return a Rule class with rule specific attributes. 
     * The method is called if user want to edit this rule type.
     * 
     * @param eml
     * @return {@link Rule}
     */
    public static Rule getRuleByEml(String eml) {
        Rule rule = new Rule();
        
        try {
        	// build document
            DocumentBuilderFactory docFac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFac.newDocumentBuilder();
            Document doc = docBuilder.parse(new ByteArrayInputStream(eml.getBytes()));

            // get phenomenon
            NodeList propertyRestrictiosnList = doc.getElementsByTagName(propertyValue);
            Node value_1 = propertyRestrictiosnList.item(0);
            rule.setPhenomenon(value_1.getTextContent());

            // get station
            Node value_2 = propertyRestrictiosnList.item(1);
            rule.setStation(value_2.getTextContent());

            NodeList filterList = doc.getElementsByTagName(fesFilter);
            Node filterNode = filterList.item(0);
            String property = filterNode.getChildNodes().item(1).getNodeName();

            // rOperatorIndex: first filter operator
            if (property.equals("fes:PropertyIsLessThan")) {
                rule.setEntryOperatorIndex(Rule.LESS_THAN);
            } else if (property.equals("fes:PropertyIsGreaterThan")) {
                rule.setEntryOperatorIndex(Rule.GREATER_THAN);
            } else if (property.equals("fes:PropertyIsEqualTo")) {
                rule.setEntryOperatorIndex(Rule.EQUAL_TO);
            } else if (property.equals("fes:PropertyIsGreaterThanOrEqualTo")) {
                rule.setEntryOperatorIndex(Rule.GREATER_THAN_OR_EQUAL_TO);
            } else if (property.equals("fes:PropertyIsLessThanOrEqualTo")) {
                rule.setEntryOperatorIndex(Rule.LESS_THAN_OR_EQUAL_TO);
            } else if (property.equals("fes:PropertyIsNotEqualTo")) {
                rule.setEntryOperatorIndex(Rule.NOT_EQUAL_TO);
            }

            filterNode = filterList.item(1);
            property = filterNode.getChildNodes().item(1).getNodeName();
            // cOperatorIndex: second filter operator
            if (property.equals("fes:PropertyIsLessThan")) {
                rule.setExitOperatorIndex(Rule.LESS_THAN);
            } else if (property.equals("fes:PropertyIsGreaterThan")) {
                rule.setExitOperatorIndex(Rule.GREATER_THAN);
            } else if (property.equals("fes:PropertyIsEqualTo")) {
                rule.setExitOperatorIndex(Rule.EQUAL_TO);
            } else if (property.equals("fes:PropertyIsGreaterThanOrEqualTo")) {
                rule.setExitOperatorIndex(Rule.GREATER_THAN_OR_EQUAL_TO);
            } else if (property.equals("fes:PropertyIsLessThanOrEqualTo")) {
                rule.setExitOperatorIndex(Rule.LESS_THAN_OR_EQUAL_TO);
            } else if (property.equals("fes:PropertyIsNotEqualTo")) {
                rule.setExitOperatorIndex(Rule.NOT_EQUAL_TO);
            }

            NodeList literalList = doc.getElementsByTagName(fesLiteral);
            Node literalNode = literalList.item(0);
            
            // rTime: 
            NodeList eventCountList = doc.getElementsByTagName(eventCount);
            Node eventCountNode = eventCountList.item(0);
            rule.setrTime(eventCountNode.getTextContent());
            
            // rValue: Sum value
            rule.setEntryValue(literalNode.getFirstChild().getNodeValue()); 

            // rUnit: Sum value unit. Default value is meter
            rule.setEntryUnit("m");

            // exit condition != enter condition?
            if (RulesUtil.reverseOperator(rule.getEntryOperatorIndex(), rule.getExitOperatorIndex())  && rule.getEntryValue().equals(rule.getExitValue())) {
                rule.setEnterEqualsExitCondition(true);
            } else {
                rule.setEnterEqualsExitCondition(false);
            }
            
            // set rule type
            rule.setRuleType(SimpleRuleType.SUM_OVER_TIME);

        } catch (Exception e) {
            LOGGER.error("Error parsing EML rule", e);
        }
        
        return rule;
    }
}