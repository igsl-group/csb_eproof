package hk.gov.ogcio.eproof.controller.pdf.utils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import static hk.gov.ogcio.eproof.controller.pdf.Constants.logger;

/**
 * Entry point to internationalization. Resource bundles has base "translation/messages".
 */
public final class ResourceProvider {

    private ResourceBundle bundle;

    /**
     * Constructor which takes a not-<code>null</code> {@link ResourceBundle} as an argument.
     *
     * @param bundle
     */
    public ResourceProvider(ResourceBundle bundle) {
        if (bundle == null) {
            throw new IllegalArgumentException("ResourceBundle must be not-null.");
        }
        this.bundle = bundle;
    }

    /**
     * Sets translations and mnemonics for labels and different kind of buttons
     *
     * @param aComponent component in which should be label set
     * @param aKey message key
     */
    public void setLabelAndMnemonic(final JComponent aComponent, final String aKey) {
        final String tmpLabelText = get(aKey);
        final int tmpMnemIndex = getMnemonicIndex(aKey);
        if (aComponent instanceof JLabel) {
            final JLabel tmpLabel = (JLabel) aComponent;
            tmpLabel.setText(tmpLabelText);
            if (tmpMnemIndex > -1) {
                tmpLabel.setDisplayedMnemonic(tmpLabelText.toLowerCase().charAt(tmpMnemIndex));
                tmpLabel.setDisplayedMnemonicIndex(tmpMnemIndex);
            }
        } else if (aComponent instanceof AbstractButton) {
            // handles Buttons, Checkboxes and Radiobuttons
            final AbstractButton tmpBtn = (AbstractButton) aComponent;
            tmpBtn.setText(tmpLabelText);
            if (tmpMnemIndex > -1) {
                tmpBtn.setMnemonic(tmpLabelText.toLowerCase().charAt(tmpMnemIndex));
            }
        } else if (aComponent instanceof JPanel) {
            final JPanel panel = (JPanel) aComponent;
            if (panel.getBorder() instanceof TitledBorder) {
                final TitledBorder titledBorder = (TitledBorder) panel.getBorder();
                titledBorder.setTitle(tmpLabelText);
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Returns message for given key from active ResourceBundle
     *
     * @param aKey name of key in resource bundle
     * @return message for given key
     */
    public String get(final String aKey) {
        String tmpMessage = null;
        try {
            tmpMessage = bundle.getString(aKey);
        } catch (MissingResourceException e) {
            logger.warn("Missing resource " + aKey, e);
        }
        if (tmpMessage == null) {
            tmpMessage = aKey;
        } else {
            tmpMessage = tmpMessage.replaceAll("&([^&])", "$1");
        }
        return tmpMessage;
    }

    /**
     * Returns index of character which should be used as a mnemonic. It returns -1 if such an character doesn't exist.
     *
     * @param aKey resource key
     * @return index (position) of character in translated message
     */
    public int getMnemonicIndex(final String aKey) {
        String tmpMessage = null;
        try {
            tmpMessage = bundle.getString(aKey);
        } catch (MissingResourceException e) {
            logger.warn("Missing resource " + aKey, e);
        }

        int tmpResult = -1;
        if (tmpMessage != null) {
            int searchFrom = 0;
            int tmpDoubles = 0;
            int tmpPos;
            final int tmpLen = tmpMessage.length();
            do {
                tmpPos = tmpMessage.indexOf('&', searchFrom);
                if (tmpPos == tmpLen - 1)
                    tmpPos = -1;
                if (tmpPos > -1) {
                    if (tmpMessage.charAt(tmpPos + 1) != '&') {
                        tmpResult = tmpPos - tmpDoubles;
                    } else {
                        searchFrom = tmpPos + 2;
                        tmpDoubles++;
                    }
                }
            } while (tmpPos != -1 && tmpResult == -1 && searchFrom < tmpLen);
        }
        return tmpResult;
    }

    /**
     * Returns message for given key from active ResourceBundle and replaces parameters with values given in array.
     *
     * @param aKey key in resource bundle
     * @param anArgs array of parameters to replace in message
     * @return message for given key with given arguments
     */
    public String get(String aKey, String... anArgs) {
        String tmpMessage = get(aKey);
        if (aKey == tmpMessage || anArgs == null || anArgs.length == 0) {
            return tmpMessage;
        }
        final MessageFormat tmpFormat = new MessageFormat(tmpMessage);
        return tmpFormat.format(anArgs);
    }

}