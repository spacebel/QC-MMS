package be.spacebel.eoportal.client.business.data;

import be.spacebel.eoportal.client.model.SearchException;
import be.spacebel.eoportal.client.util.Utility;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.model.SelectItem;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * This class represents an OpenSearch parameter extension
 *
 * @author mng
 */
public class OpenSearchParameter implements Serializable {

    private static final long serialVersionUID = 1L;
    private String name;
    private String value;
    private String namespace;
    private String label;
    private String type;
    private String index;
    private String formValue;
    private String help;
    private int order;
    private ParameterOption selectedOption;
    private Map<String, String> options;
    private boolean show;
    private boolean required;
    private String pattern;
    private double minInclusive;
    private double maxInclusive;
    private boolean hasMinInclusive;
    private boolean hasMaxInclusive;
    private String minDate;
    private String maxDate;
    private String pMinInclusive;
    private String pMaxInclusive;
    private String spacePosition;

    private static final Logger log = Logger.getLogger(OpenSearchParameter.class);

    public OpenSearchParameter() {
        this.show = true;
        this.type = "text";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setName(String newName, String newValue) {
        if (StringUtils.isNotEmpty(newName)) {
            this.name = newName;
        } else {
            if (StringUtils.isNotEmpty(newValue)) {
                this.name = newValue.replaceAll(":", "_");
            }
        }
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getLabel() {
        return label;
    }

    public String getShortLabel() {
        String shortLabel = label;
        if ((help != null && help.length() > 0)) {
            if (required) {
                if (label != null && label.length() > 18) {
                    shortLabel = label.substring(0, 15) + "...";
                }
            } else {
                if (label != null && label.length() > 23) {
                    shortLabel = label.substring(0, 20) + "...";
                }
            }

        } else {
            if (required) {
                if (label != null && label.length() > 20) {
                    shortLabel = label.substring(0, 17) + "...";
                }
            } else {
                if (label != null && label.length() > 25) {
                    shortLabel = label.substring(0, 22) + "...";
                }
            }
        }

        return shortLabel;
    }

    public String getLabelTooltip() {
        String labelTooltip = "";
        if (help != null && help.length() > 0) {
            if (label != null && label.length() > 23) {
                labelTooltip = label;
            }
        } else {
            if (label != null && label.length() > 25) {
                labelTooltip = label;
            }
        }

        return labelTooltip;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setLabel(String newLabel, String newValue) {
        if (StringUtils.isNotEmpty(newLabel)) {
            this.label = newLabel;
        } else {
            if (StringUtils.isNotEmpty(newValue)) {
                this.label = newValue;
            }
        }
    }

    public String getType() {
        if (isList()) {
            if (StringUtils.isNotEmpty(this.pattern)) {
                return Constants.AUTO_COMPLETE_LIST;
            } else {
                return "list";
            }
        }

        if ("list".equals(type) && !isList()) {
            return "text";
        }

        return type;
    }

    public void setType(String newType) {
        if (StringUtils.isNotEmpty(newType)) {
            this.type = newType;
        } else {
            if (Utility.matchParameter(this.getNamespace(), this.value, Constants.TIME_NAMESPACE, Constants.TIME_START)
                    || Utility.matchParameter(this.getNamespace(), this.value, Constants.TIME_NAMESPACE, Constants.TIME_END)) {
                this.type = Constants.DATE_TYPE;
            } else {
                this.type = "text";
            }
        }
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public Map<String, String> getOptions() {
        return this.options;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }

    public boolean isList() {
        boolean ok = false;
        if (this.options != null && this.options.size() > 0) {
            ok = true;
        }
        return ok;
    }

    public List<SelectItem> getOptionsAsSelectItems() {
        if (isList()) {
            List<SelectItem> list = new ArrayList<>();
            for (Map.Entry<String, String> entry : options.entrySet()) {
                list.add(new SelectItem(entry.getKey(), entry.getValue()));
            }
            SelectItemComparator.sort(list);
            return list;
        }
        return null;
    }

    /*
     public List<Map.Entry<String, String>> getAutoCompleteOptions() {
     if (isList() && this.options != null) {
     List<Map.Entry<String, String>> autoCompleteOptions = new ArrayList<Map.Entry<String, String>>();
     for (Map.Entry<String, String> entry : options.entrySet()) {
     autoCompleteOptions.add(entry);
     }
     return autoCompleteOptions;
     }
     return null;
     }
     */
    public String getFormValue() {
        if (Constants.AUTO_COMPLETE_LIST.equals(getType())) {
            if (getSelectedOption() != null) {
                return StringUtils.substringBefore(getSelectedOption().getValue(), Constants.AUTO_COMPLETE_OPTION_SEPARATOR);
            } else {
                return null;
            }
        } else {
            return formValue;
        }
    }

    public ParameterOption getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(ParameterOption selectedOption) {
        this.selectedOption = selectedOption;
    }

    public int distanceLevenshtein(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        /*
         // i == 0
         int[] costs = new int[b.length() + 1];
         for (int j = 0; j < costs.length; j++) {
         costs[j] = j;
         }
         for (int i = 1; i <= a.length(); i++) {
         // j == 0; nw = lev(i - 1, j)
         costs[0] = i;
         int nw = i - 1;
         for (int j = 1; j <= b.length(); j++) {
         int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
         nw = costs[j];
         costs[j] = cj;
         }
         }
         return costs[b.length()];
         */
        return StringUtils.getLevenshteinDistance(a, b);
    }

    public List<ParameterOption> completeFormValue(String query) {
        log.debug("completeFormValue( query = " + query + ")");
        List<ParameterOption> filteredValues = new ArrayList<>();
        for (Map.Entry<String, String> entry : options.entrySet()) {
            if (entry.getKey().toLowerCase().startsWith(query.toLowerCase())
                    || distanceLevenshtein(entry.getKey().toLowerCase(), query.toLowerCase()) <= 2) {
                filteredValues.add(new ParameterOption((entry.getKey() + Constants.AUTO_COMPLETE_OPTION_SEPARATOR + entry.getValue()), entry.getValue()));
            }
        }
        ParameterOptionComparator.sort(filteredValues);
        return filteredValues;

    }

    /*
     public List<String> completeFormValue(String query) {
     log.debug("completeFormValue( query = " + query + ")");
     List<String> filteredValues = new ArrayList<String>();

     for (Map.Entry<String, String> entry : options.entrySet()) {

     if (entry.getKey().toLowerCase().startsWith(query.toLowerCase())
     || distanceLevenshtein(entry.getKey().toLowerCase(), query.toLowerCase()) <= 2) {
     filteredValues.add(entry.getKey() + Constants.AUTO_COMPLETE_OPTION_SEPARATOR + entry.getValue());
     }
     }
     return filteredValues;

     }

     */
    public void setFormValue(String formValue) {
        this.formValue = formValue;
    }

    public String getHelp() {
        return help;
    }

    public void setHelp(String help) {
        this.help = help;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public double getMinInclusive() {
        return minInclusive;
    }

    public void setMinInclusive(double minInclusive) {
        this.minInclusive = minInclusive;
    }

    public double getMaxInclusive() {
        return maxInclusive;
    }

    public void setMaxInclusive(double maxInclusive) {
        this.maxInclusive = maxInclusive;
    }

    public boolean isHasMinInclusive() {
        return hasMinInclusive;
    }

    public void setHasMinInclusive(boolean hasMinInclusive) {
        this.hasMinInclusive = hasMinInclusive;
    }

    public boolean isHasMaxInclusive() {
        return hasMaxInclusive;
    }

    public void setHasMaxInclusive(boolean hasMaxInclusive) {
        this.hasMaxInclusive = hasMaxInclusive;
    }

    public String getMinDate() {
        return minDate;
    }

    public void setMinDate(String minDate) {
        this.minDate = minDate;
    }

    public String getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(String maxDate) {
        this.maxDate = maxDate;
    }

    public String getpMinInclusive() {
        return pMinInclusive;
    }

    public void setpMinInclusive(String pMinInclusive) {
        this.pMinInclusive = pMinInclusive;
    }

    public String getpMaxInclusive() {
        return pMaxInclusive;
    }

    public void setpMaxInclusive(String pMaxInclusive) {
        this.pMaxInclusive = pMaxInclusive;
    }

    public void validate() throws SearchException {
        log.debug("validate input parameter......");
        if (this.required && StringUtils.isEmpty(this.formValue)) {
            throw new SearchException(this.label + " is required.", "");
        }

        if (StringUtils.isNotEmpty(this.formValue)) {
            String strValue = this.formValue.trim();
            if (Constants.DATE_TYPE.equals(this.type)) {
                log.debug("Validate date:" + strValue);
                if (StringUtils.isNotEmpty(strValue)) {
                    SimpleDateFormat formatter = new SimpleDateFormat(Constants.DATEFORMAT);
                    try {
                        Date currentDate = formatter.parse(strValue);

                        if (StringUtils.isNotEmpty(this.pattern)) {
                            log.debug("Pattern = " + this.pattern);
                            Pattern p = Pattern.compile(this.pattern);
                            String dateValue = strValue;
                            if (Utility.matchParameter(this.getNamespace(), this.value, Constants.TIME_NAMESPACE, Constants.TIME_END)) {
                                dateValue = strValue + "T23:59:59Z";
                            } else {
                                dateValue = strValue + "T00:00:00Z";
                            }
                            Matcher matcher = p.matcher(dateValue);
                            if (!matcher.matches()) {
                                throw new SearchException(this.label + " value " + dateValue + " should match the pattern: " + this.pattern, "");
                            }
                        }

                        if (StringUtils.isNotEmpty(this.maxDate)) {
                            try {
                                log.debug("Check max date: " + this.maxDate);
                                Date dMaxDate = formatter.parse(this.maxDate);
                                if (currentDate.after(dMaxDate)) {
                                    throw new SearchException(this.label + " should be less than: " + this.maxDate, "");
                                }
                            } catch (ParseException e) {
                                log.debug("Max date parser error: " + e.getMessage());
                            }
                        }
                        if (StringUtils.isNotEmpty(this.minDate)) {
                            try {
                                log.debug("Check min date: " + this.minDate);
                                Date dMinDate = formatter.parse(this.minDate);
                                if (currentDate.before(dMinDate)) {
                                    throw new SearchException(this.label + " should be greater than: " + this.minDate, "");
                                }
                            } catch (ParseException e) {
                                log.debug("Min date parser error: " + e.getMessage());
                            }
                        }
                    } catch (ParseException e) {
                        throw new SearchException(this.label + " should be in the format: " + Constants.DATEFORMAT, "");
                    }
                }
            } else {
                if (StringUtils.isNotEmpty(this.pattern)) {
                    log.debug("Pattern = " + this.pattern);
                    Pattern p = Pattern.compile(this.pattern);
                    Matcher matcher = p.matcher(strValue);
                    if (!matcher.matches()) {
                        throw new SearchException(this.label + " should match the pattern: " + this.pattern, "");
                    }
                }

                if (isHasMinInclusive() && isHasMaxInclusive()) {
                    double dblValue = toDouble();
                    if (dblValue > this.maxInclusive || dblValue < this.minInclusive) {
                        throw new SearchException(this.label + " should be in range [" + this.minInclusive + "," + this.maxInclusive + "].", "");
                    }
                } else {
                    if (isHasMaxInclusive()) {
                        if (toDouble() > this.maxInclusive) {
                            throw new SearchException(this.label + " should be less than or equal: " + this.maxInclusive, "");
                        }
                    }

                    if (isHasMinInclusive()) {
                        if (toDouble() < this.minInclusive) {
                            throw new SearchException(this.label + " should be greater than or equal: " + this.minInclusive, "");
                        }
                    }
                }
            }
        }
    }

    private double toDouble() throws SearchException {
        try {
            return Double.parseDouble(this.formValue.trim());
        } catch (NumberFormatException e) {
            throw new SearchException(this.label + " should be a numeric.", "");
        }
    }

    public String getSpacePosition() {
        return spacePosition;
    }

    public void setSpacePosition(String spacePosition) {
        this.spacePosition = spacePosition;
    }

    /*
     public void validate(FacesContext context, UIComponent component, Object value)
     throws ValidatorException {
     log.debug("validate: " + this.name + " = " + value);
     if ("os_searchTerms".equals(this.getName())) {
     log.debug("searchTerms param. Skip validate");
     } else {
     if (this.required && StringUtils.isEmpty((String) value)) {
     throw createValidatorException("Required parameter", this.title + " value is required.");
     }

     if (Constants.DATE_TYPE.equals(this.type)) {
     log.debug("Validate date:");
     if (value != null) {
     String strValue = (String) value;
     strValue = strValue.trim();
     if (StringUtils.isNotEmpty(strValue)) {
     SimpleDateFormat formatter = new SimpleDateFormat(Constants.DATEFORMAT);
     try {
     Date currentDate = formatter.parse(strValue);
     if (StringUtils.isNotEmpty(this.maxDate)) {
     Date dMaxDate = formatter.parse(this.maxDate);
     if (currentDate.after(dMaxDate)) {
     throw createValidatorException("Validation Error", ("Value of " + this.title + " should be less than: " + this.maxDate));
     }
     }
     if (StringUtils.isNotEmpty(this.minDate)) {
     Date dMinDate = formatter.parse(this.minDate);
     if (currentDate.before(dMinDate)) {
     throw createValidatorException("Validation Error", ("Value of " + this.title + " should be greater than: " + this.minDate));
     }
     }
     } catch (ParseException e) {
     throw createValidatorException("Validation Error", ("Value of " + this.title + " should follow the format: " + Constants.DATEFORMAT));
     }
     }
     }
     } else {
     if (StringUtils.isNotEmpty(this.pattern)) {
     log.debug("Pattern = " + this.pattern);
     try {
     RegexValidator regexValidator = new RegexValidator();
     regexValidator.setPattern(this.pattern);
     regexValidator.validate(context, component, value);
     } catch (ValidatorException e) {
     log.debug("RegexValidator exception: " + e);
     throw createValidatorException("Validation Error", ("Value of " + this.title + " should match the pattern: " + this.pattern));
     }
     }

     if (isHasMinInclusive() || isHasMaxInclusive()) {
     log.debug("hasMinInclusive = " + this.hasMinInclusive);
     log.debug("hasMaxInclusive = " + this.hasMaxInclusive);
     DoubleRangeValidator dbRangeValidator = new DoubleRangeValidator();
     if (isHasMinInclusive()) {
     log.debug("minInclusive = " + this.minInclusive);
     dbRangeValidator.setMinimum(this.getMinInclusive());
     }
     if (isHasMaxInclusive()) {
     log.debug("maxInclusive = " + this.maxInclusive);
     dbRangeValidator.setMaximum(this.getMaxInclusive());
     }
     dbRangeValidator.validate(context, component, value);
     }
     }
     }
     }

     private ValidatorException createValidatorException(String summaryMsg, String detail) {
     return new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, summaryMsg, detail));
     }

     */
    @Override
    public String toString() {
        String optionValues = "options[";
        if (options != null) {
            for (Map.Entry<String, String> entry : options.entrySet()) {
                optionValues += "value = " + entry.getKey() + ", label = " + entry.getValue() + ",";
            }
        }
        optionValues += "]";

        return "OpenSearchParameter [name = " + name + ", value = " + value + ", namespace = "
                + namespace + ", label = " + label + ", help = " + help + ", order = " + order
                + ", type = " + type + ", isList = " + isList()
                + ", index = " + index + ", formValue = " + formValue + ", show = " + show
                + ", required = " + required + ", pattern = " + pattern + ", minInclusive = "
                + minInclusive + ", maxInclusive = " + maxInclusive + ", hasMinInclusive = "
                + hasMinInclusive + ", hasMaxInclusive = " + hasMaxInclusive + ", mindate = "
                + minDate + ", maxdate = " + maxDate + ", options = " + optionValues + "]";
    }

}
