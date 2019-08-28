package ui;

import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import net.miginfocom.swing.MigLayout;

public class UnableToOpenBrowserPanel extends JPanel
{
  private static final long serialVersionUID = 1L;
  private static final ResourceBundle BUNDLE = ResourceBundle
      .getBundle("localization.phrases"); //$NON-NLS-1$
  private final JLabel lblUnableToOpen = new JLabel(
      BUNDLE.getString("UnableToOpenBrowserPanel.lblUnableToOpen.text")); //$NON-NLS-1$
  private final JTextArea textArea = new JTextArea();

  /**
   * Create the panel.
   * 
   * @param string
   */
  public UnableToOpenBrowserPanel(String url)
  {
    setLayout(new MigLayout("", "[pref!]", "[pref!][pref!]"));
    add(lblUnableToOpen, "cell 0 0");
    textArea.setEditable(false);
    textArea.setLineWrap(true);
    textArea.setText(url);

    add(textArea, "flowx,cell 0 1,growx");
    setPreferredSize(((MigLayout) getLayout()).preferredLayoutSize(this));
  }

}
