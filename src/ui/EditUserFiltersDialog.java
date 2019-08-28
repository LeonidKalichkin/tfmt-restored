package ui;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import localization.Phrase;
import net.miginfocom.swing.MigLayout;
import tweaker.Configuration;

public class EditUserFiltersDialog extends JDialog
{
  private static final ResourceBundle BUNDLE = ResourceBundle
      .getBundle("localization.phrases"); //$NON-NLS-1$
  private static final long serialVersionUID = 1L;

  private final JDialog dialog = this;
  private final JScrollPane scrollPane = new JScrollPane();
  private final JTextArea textArea = new JTextArea();
  private final JLabel lblNewLabel = new JLabel(
      BUNDLE.getString("EditUserFiltersDialog.lblNewLabel.text")); //$NON-NLS-1$
  private final JLabel lblNewLabel_1 = new JLabel(
      BUNDLE.getString("EditUserFiltersDialog.lblNewLabel_1.text")); //$NON-NLS-1$
  private final JLabel lblNewLabel_2 = new JLabel(
      BUNDLE.getString("EditUserFiltersDialog.lblNewLabel_2.text")); //$NON-NLS-1$

  /**
   * Launch the application.
   */
  public static void main(String[] args)
  {
    try
    {
      final Configuration config = new Configuration(new File("tfmt.conf"));
      config.load();
      final EditUserFiltersDialog dialog = new EditUserFiltersDialog(config);
      dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      dialog.setVisible(true);
    } catch (final Exception e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Create the dialog.
   */
  public EditUserFiltersDialog(final Configuration config)
  {
    setTitle(BUNDLE.getString("EditUserFiltersDialog.title")); //$NON-NLS-1$
    setModalityType(ModalityType.APPLICATION_MODAL);
    setBounds(100, 100, 477, 336);
    getContentPane().setLayout(new MigLayout("", "[grow][]", "[][][grow][]"));

    getContentPane().add(lblNewLabel, "flowx,cell 0 0");
    lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
    lblNewLabel_1.addMouseListener(new MouseAdapter()
    {
      @Override
      public void mouseEntered(MouseEvent e)
      {
        dialog.setCursor(new Cursor(Cursor.HAND_CURSOR));
      }

      @Override
      public void mouseExited(MouseEvent e)
      {
        dialog.setCursor(Cursor.getDefaultCursor());
      }

      @Override
      public void mouseClicked(MouseEvent e)
      {
        try
        {
          Desktop.getDesktop().browse(new URI(BUNDLE.getString("Regexp.URL")));
        } catch (IOException | URISyntaxException e1)
        {
          JOptionPane.showMessageDialog(dialog, new UnableToOpenBrowserPanel(
              BUNDLE.getString("Regexp.URL")), Phrase.Error.toString(),
              JOptionPane.WARNING_MESSAGE);
        }
      }
    });

    getContentPane().add(lblNewLabel_1, "cell 1 0 1 2");

    getContentPane().add(lblNewLabel_2, "cell 0 1");

    textArea.setText(config.getUserFilterList());
    getContentPane().add(scrollPane, "cell 0 2 2 1,grow");

    scrollPane.setViewportView(textArea);
    final JButton okButton = new JButton(
        BUNDLE.getString("EditUserFiltersDialog.okButton.text"));
    getContentPane().add(okButton, "cell 0 3,alignx right");
    okButton.setActionCommand(BUNDLE
        .getString("EditUserFiltersDialog.okButton.actionCommand"));
    okButton.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        config.setUserFilterList(textArea.getText());
        dialog
            .dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
      }
    });

    final ActionListener closeListener = new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        dialog
            .dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
      }
    };
    getRootPane().setDefaultButton(okButton);
    {
      final JButton cancelButton = new JButton(
          BUNDLE.getString("EditUserFiltersDialog.cancelButton.text"));
      getContentPane().add(cancelButton, "cell 1 3");
      cancelButton.addActionListener(closeListener);
      cancelButton.setActionCommand("Cancel");
    }

    getRootPane().registerKeyboardAction(closeListener,
        KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
        JComponent.WHEN_IN_FOCUSED_WINDOW);
  }

}
