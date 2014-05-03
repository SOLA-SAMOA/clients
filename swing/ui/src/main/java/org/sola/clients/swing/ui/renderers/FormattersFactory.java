/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO). All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this list of conditions
 * and the following disclaimer. 2. Redistributions in binary form must reproduce the above
 * copyright notice,this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
package org.sola.clients.swing.ui.renderers;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFormattedTextField;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import org.sola.common.NumberUtility;
import org.sola.common.logging.LogUtility;

/**
 * Provides formatters for {@link JFormattedTextField}
 */
public class FormattersFactory {

 private Map<String, DefaultFormatterFactory> factoryMap = new HashMap<String, DefaultFormatterFactory>();
    private static final String DECIMAL_FACTORY = "decimal";
    private static final String INTEGER_FACTORY = "integer";
    private static final String SHORT_FACTORY = "short";
    private static final String DATE_FACTORY = "date";
    private static final String DATETIME_FACTORY = "datetime";
    private static final String IMPERIAL_FACTORY = "imperial";
    private static final String METRIC_AREA_FACTORY = "metricArea";
    private static final String MONEY_FACTORY = "money";

    private FormattersFactory() {
    }

    public static FormattersFactory getInstance() {
        return FormattersFactoryHolder.INSTANCE;
    }

    private static class FormattersFactoryHolder {

        private static final FormattersFactory INSTANCE = new FormattersFactory();
    }

    /**
     * Returns a custom formatter factory for displaying and editing a
     * BigDecimal value. This default method will display the BigDecimal up to 5
     * decimal points To use this factory, set the formatterFactory property of
     * the JFormattedTextField to
     * {@code FormattersFactory.getInstance().getDecimalFormatterFactory()}
     */
    public DefaultFormatterFactory getDecimalFormatterFactory() {
        return getDecimalFormatterFactory(5);
    }

    /**
     * Returns a custom formatter factory for displaying and editing a
     * BigDecimal value. The precision parameter can be used to indicate the
     * number of decimal places to display. Must be a value from 0 (no decimal
     * places) to up 12. To use this factory, set the formatterFactory property
     * of the JFormattedTextField to
     * {@code FormattersFactory.getInstance().getDecimalFormatterFactory(int)}
     *
     * @param precision The number of decimal places to display. From 0 to 12
     * @return
     */
    public DefaultFormatterFactory getDecimalFormatterFactory(int precision) {
        if (!factoryMap.containsKey(DECIMAL_FACTORY + Integer.toString(precision))) {
            // Create a format mask to use for displaying the data value
            String formatMask = "";
            if (precision > 0) {
                formatMask += ".";
                for (int x = 0; x < precision; x++) {
                    formatMask += "#";
                }
            }
            DefaultFormatter displayFormat = new NumberFormatter(new DecimalFormat("#,###" + formatMask));
            DefaultFormatter editFormat = new NumberFormatter(new DecimalFormat("#" + formatMask)) {
                // Accept null or emtpy string values entered by the user as null.
                @Override
                public Object stringToValue(String userInput) throws ParseException {
                    Object result = null;
                    if (userInput != null && !userInput.trim().isEmpty()) {
                        result = super.stringToValue(userInput);
                    }
                    return result;
                }
            };
            displayFormat.setValueClass(BigDecimal.class);
            editFormat.setValueClass(BigDecimal.class);
            factoryMap.put(DECIMAL_FACTORY + Integer.toString(precision),
                    new DefaultFormatterFactory(displayFormat, displayFormat, editFormat));
        }
        return factoryMap.get(DECIMAL_FACTORY + Integer.toString(precision));
    }

    public DefaultFormatterFactory getIntegerFormatterFactory() {
        if (!factoryMap.containsKey(INTEGER_FACTORY)) {
            DefaultFormatter fmt = new NumberFormatter(NumberFormat.getIntegerInstance()) {
                // Accept null or emtpy string values entered by the user as null.
                @Override
                public Object stringToValue(String userInput) throws ParseException {
                    Object result = null;
                    if (userInput != null && !userInput.trim().isEmpty()) {
                        result = super.stringToValue(userInput);
                    }
                    return result;
                }
            };
            fmt.setValueClass(Integer.class);
            factoryMap.put(INTEGER_FACTORY, new DefaultFormatterFactory(fmt, fmt, fmt));
        }
        return factoryMap.get(INTEGER_FACTORY);
    }

    public DefaultFormatterFactory getShortFormatterFactory() {
        if (!factoryMap.containsKey(SHORT_FACTORY)) {
            DefaultFormatter fmt = new NumberFormatter() {
                // Accept null or emtpy string values entered by the user as null.
                @Override
                public Object stringToValue(String userInput) throws ParseException {
                    Object result = null;
                    if (userInput != null && !userInput.trim().isEmpty()) {
                        result = super.stringToValue(userInput);
                    }
                    return result;
                }
            };
            fmt.setValueClass(Short.class);
            factoryMap.put(SHORT_FACTORY, new DefaultFormatterFactory(fmt, fmt, fmt));
        }
        return factoryMap.get(SHORT_FACTORY);
    }

    /**
     * Issue #247 Unable to enter null date into formatted text field. Returns a
     * custom formatter factory that can be used as the formatter factory for a
     * JFormattedTextField when displaying and/or editing dates. This factory
     * overrides the default stringToValue functions so that the user can enter
     * an empty date value. To use this factory, set the formatterFactory
     * property of the JFormattedTextField to
     * {@code FormattersFactory.getInstance().getDateFormatterFactory()}
     */
    public DefaultFormatterFactory getDateFormatterFactory() {
        if (!factoryMap.containsKey(DATE_FACTORY)) {
            //DateFormatter displayFormat = new DateFormatter(DateFormat.getDateInstance(DateFormat.MEDIUM));
            // Use custom display format instead of International MEDIUM format
            SimpleDateFormat f = new SimpleDateFormat("d MMM yyyy");
            DateFormatter displayFormat = new DateFormatter(f);

            //DateFormatter editFormat = new DateFormatter(DateFormat.getDateInstance(DateFormat.SHORT))
            //SimpleDateFormat e = new SimpleDateFormat("d/M/yy");
            DateFormatter editFormat = new DateFormatter(DateFormat.getDateInstance(DateFormat.SHORT)) {
                // Accept null or emtpy string values entered by the user as null.
                @Override
                public Object stringToValue(String userInput) throws ParseException {
                    Object result = null;
                    if (userInput != null && !userInput.trim().isEmpty()) {
                        result = super.stringToValue(userInput);
                    }
                    return result;
                }
            };
            factoryMap.put(DATE_FACTORY,
                    new DefaultFormatterFactory(displayFormat, displayFormat, editFormat));
        }
        return factoryMap.get(DATE_FACTORY);
    }

    /**
     * Returns a custom formatter factory for displaying and editing a date time
     * value. To use this factory, set the formatterFactory property of the
     * JFormattedTextField to
     * {@code FormattersFactory.getInstance().getDateTimeFormatterFactory()}
     */
    public DefaultFormatterFactory getDateTimeFormatterFactory() {
        if (!factoryMap.containsKey(DATETIME_FACTORY)) {
            //DateFormatter displayFormat = new DateFormatter(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT));
            // Use custom display format instead of International MEDIUM format - 12 hour clock with AM/PM
            SimpleDateFormat f = new SimpleDateFormat("d MMM yyyy h:mm a");
            DateFormatter displayFormat = new DateFormatter(f);
            DateFormatter editFormat = new DateFormatter(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)) {
                // Accept null or emtpy string values entered by the user as null.
                @Override
                public Object stringToValue(String userInput) throws ParseException {
                    Object result = null;
                    if (userInput != null && !userInput.trim().isEmpty()) {
                        result = super.stringToValue(userInput);
                    }
                    return result;
                }
            };
            factoryMap.put(DATETIME_FACTORY,
                    new DefaultFormatterFactory(displayFormat, displayFormat, editFormat));
        }
        return factoryMap.get(DATETIME_FACTORY);
    }

    /**
     * Returns a custom formatter factory for displaying and editing an area
     * value in imperial format i.e. acres (a), roods (r) and perches (p). To
     * use this factory, set the formatterFactory property of the
     * JFormattedTextField to
     * {@code FormattersFactory.getInstance().getImperialFormatterFactory()}
     */
    public DefaultFormatterFactory getImperialFormatterFactory() {
        if (!factoryMap.containsKey(IMPERIAL_FACTORY)) {
            DefaultFormatter format = new DefaultFormatter() {
                // Accept null or emtpy string values entered by the user as null.
                @Override
                public Object stringToValue(String userInput) throws ParseException {
                    Object result = null;
                    if (userInput != null && !userInput.trim().isEmpty()) {
                        try {
                            // Try to convert the user input into a metric 
                            // (metres sqrd) value. 
                            result = NumberUtility.convertImperialToMetric(userInput);
                        } catch (Exception ex) {
                            result = null;
                            LogUtility.log("Unable to parse imperial value " + userInput, ex);
                        }
                        if (result == null) {
                            throw new ParseException("Unable to parse imperial value", 0);
                        }
                    }
                    return result;
                }

                @Override
                public String valueToString(Object value) throws ParseException {
                    String result = null;
                    if (value != null && BigDecimal.class.isAssignableFrom(value.getClass())) {
                        // Display the value as an imperial area. 
                        result = NumberUtility.formatAreaImperial((BigDecimal) value);
                    }
                    return result;
                }
            };
            factoryMap.put(IMPERIAL_FACTORY, new DefaultFormatterFactory(format, format, format));
        }
        return factoryMap.get(IMPERIAL_FACTORY);
    }

    /**
     * Returns a custom formatter factory for displaying and editing an area
     * value in metric format. When displayed, the area will be represented as
     * either hectares or meters depending on the size. When editing, the user
     * will enter a meter value up to 1 dp. To use this factory, set the
     * formatterFactory property of the JFormattedTextField to
     * {@code FormattersFactory.getInstance().getMetricAreaFormatterFactory()}
     */
    public DefaultFormatterFactory getMetricAreaFormatterFactory() {
        if (!factoryMap.containsKey(METRIC_AREA_FACTORY)) {

            DefaultFormatter edit = new NumberFormatter(new DecimalFormat("#.#")) {
                // Accept null or emtpy string values entered by the user as null.
                @Override
                public Object stringToValue(String userInput) throws ParseException {
                    Object result = null;
                    if (userInput != null && !userInput.trim().isEmpty()) {
                        result = super.stringToValue(userInput);
                    }
                    return result;
                }
            };
            edit.setValueClass(BigDecimal.class);

            DefaultFormatter displayFormat = new DefaultFormatter() {
                @Override
                public String valueToString(Object value) throws ParseException {
                    String result = null;
                    if (value != null && BigDecimal.class.isAssignableFrom(value.getClass())) {
                        // Display the value as an imperial area. 
                        result = NumberUtility.formatAreaMetric((BigDecimal) value);
                    }
                    return result;
                }
            };

            factoryMap.put(METRIC_AREA_FACTORY, new DefaultFormatterFactory(displayFormat,
                    displayFormat, edit));
        }
        return factoryMap.get(METRIC_AREA_FACTORY);
    }

    /**
     * Returns a custom formatter factory for displaying and editing money
     * values. When displayed, the value will be represented with the
     * appropriate currency symbol (e.g. $,£,€) . When editing, the user will
     * enter a value up to 2 d.p. To use this factory, set the formatterFactory
     * property of the JFormattedTextField to
     * {@code FormattersFactory.getInstance().getMoneyFormatterFactory()}
     */
    public DefaultFormatterFactory getMoneyFormatterFactory() {
        if (!factoryMap.containsKey(MONEY_FACTORY)) {

            DefaultFormatter edit = new NumberFormatter(new DecimalFormat("#.00")) {
                // Accept null or emtpy string values entered by the user as null.
                @Override
                public Object stringToValue(String userInput) throws ParseException {
                    Object result = null;
                    if (userInput != null && !userInput.trim().isEmpty()) {
                        result = super.stringToValue(userInput);
                    }
                    return result;
                }
            };
            edit.setValueClass(BigDecimal.class);

            // Currency Symbol for Tonga is T$
            DefaultFormatter displayFormat = new NumberFormatter(new DecimalFormat("$#,###.00"));
            displayFormat.setValueClass(BigDecimal.class);

            factoryMap.put(MONEY_FACTORY, new DefaultFormatterFactory(displayFormat,
                    displayFormat, edit));
        }
        return factoryMap.get(MONEY_FACTORY);
    }
}
