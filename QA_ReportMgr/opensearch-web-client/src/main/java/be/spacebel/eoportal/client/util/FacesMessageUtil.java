package be.spacebel.eoportal.client.util;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Utility class to display error and info messages on the client, or retrieve
 * localized strings from the bundle.
 *
 * @author mng
 *
 */
public class FacesMessageUtil implements Serializable {

    private ResourceBundle bundle;
    private final static Logger log = Logger.getLogger(FacesMessageUtil.class);

    private FacesMessageUtil(FacesContext fc, Locale locale) {
        bundle = ResourceBundle.getBundle(fc.getApplication().getMessageBundle(), locale);
    }

    public static void addInfoMessage(String bundleKey, String... params) {
        addInfoMessageWithDetails(bundleKey, "", params);
    }

    public static void addInfoMessageWithDetails(String bundleKey, String details, String... params) {
        getInstance().addMessage(bundleKey, details, FacesMessage.SEVERITY_INFO, params);
    }

    public static void addInfoMessage(Throwable e) {
        log.error(e.getMessage(), e);
        getInstance().addMessage(e.getMessage(), getStackTraceAsString(e),
                FacesMessage.SEVERITY_INFO);
    }

    public static void addWarningMessage(String bundleKey, String... params) {
        addWarningMessageWithDetails(bundleKey, "", params);
    }

    public static void addWarningMessageWithDetails(String bundleKey, String details,
            String... params) {
        getInstance().addMessage(bundleKey, details, FacesMessage.SEVERITY_WARN, params);
    }

    public static void addWarningMessage(Throwable e) {
        log.error(e.getMessage(), e);
        getInstance().addMessage(e.getMessage(), getStackTraceAsString(e),
                FacesMessage.SEVERITY_WARN);
    }

    public static void addErrorMessage(String bundleKey, String... params) {
        addErrorMessageWithDetails(bundleKey, "", params);
    }

    public static void addErrorMessageWithDetails(String bundleKey, String details,
            String... params) {
        getInstance().addMessage(bundleKey, details, FacesMessage.SEVERITY_ERROR, params);
    }

    public static void addErrorMessage(Throwable e) {
        log.error(e.getMessage(), e);
        getInstance().addMessage(e.getMessage(), "", FacesMessage.SEVERITY_ERROR);
    }

    public static String getLocalizedString(String bundleKey, String... params) {
        return getInstance().getStringFromBundle(bundleKey, params);
    }

    private static FacesMessageUtil getInstance() {
        FacesContext fc = FacesContext.getCurrentInstance();
        Locale locale = null;
        try {
            locale = fc.getViewRoot().getLocale();

        } catch (NullPointerException npe) {
            locale = Locale.US;
        }
        return new FacesMessageUtil(fc, locale);
    }

    private void addMessage(String bundleKey, String details, FacesMessage.Severity severity,
            String... params) {
        String text = "null";
        if (bundleKey != null) {
            text = getStringFromBundle(bundleKey, params);
        }
        if (StringUtils.isNotEmpty(details)) {
            text += ": " + details;
        }
        String summaryText = "Info";
        if (severity == FacesMessage.SEVERITY_ERROR) {
            summaryText = "Error";
        }
        if (severity == FacesMessage.SEVERITY_WARN) {
            summaryText = "Warning";
        }
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, summaryText, text));
    }

    private String getStringFromBundle(String bundleKey, String... params) {
        String text = null;
        try {
            text = bundle.getString(bundleKey);
        } catch (MissingResourceException e) {
            // property not found, we leave the text as is
            text = bundleKey;
        }

        if (params != null) {
            try {
                MessageFormat mf = new MessageFormat(text);
                text = mf.format(params, new StringBuffer(), null).toString();
            } catch (Exception e) {
                // message parsing failed, leave the text untouched
            }
        }
        return text;
    }

    public static String getStackTraceAsString(Throwable t) {
        return t.getMessage();
    }

}
