package ui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import localization.Phrase;
import net.miginfocom.swing.MigLayout;
import transformice.connection.MasterConnectionProcessor;
import tweaker.Configuration;
import ui.events.AntiSpamExtensionStatusEvent;
import ui.events.ControlPanelExtensionStatusEvent;
import extensions.AntiSpamExtension;

public class ControlPanelDialog extends JDialog
{
  private static final long serialVersionUID = 1L;
  private static final ResourceBundle BUNDLE = ResourceBundle
      .getBundle("localization.phrases"); //$NON-NLS-1$

  private final MasterConnectionProcessor connection;
  private final Configuration config;

  private final JPanel switchPanel = new JPanel();
  private final JPanel cardPanel = new JPanel();
  private final JToggleButton btnAntiSpamSwitch = new JToggleButton(
      BUNDLE.getString("ControlPanelDialog.btnAntispam.text")); //$NON-NLS-1$
  private final ButtonGroup switchGroup = new ButtonGroup();
  private final JPanel antiSpamCard = new JPanel();
  private final JScrollPane scrollPane = new JScrollPane();
  private final JPanel panel = new JPanel();
  private final JCheckBox chckbxEnabled = new JCheckBox(
      BUNDLE.getString("ControlPanelDialog.chckbxEnabled.text")); //$NON-NLS-1$
  private final JTextField textField = new JTextField();
  private final JLabel lblFilterMessagesThat = new JLabel(
      BUNDLE.getString("ControlPanelDialog.lblFilterMessagesThat.text")); //$NON-NLS-1$
  private final JButton btnAddFastFilter = new JButton(
      BUNDLE.getString("ControlPanelDialog.btnAddFastFilter.text")); //$NON-NLS-1$
  private final JPanel panel_1 = new JPanel();
  private final JPanel panel_2 = new JPanel();
  private final JTextArea textArea = new JTextArea();
  private final JScrollPane scrollPane_1 = new JScrollPane();
  private final JButton btnUserFilters = new JButton(
      BUNDLE.getString("ControlPanelDialog.btnUserFilters.text")); //$NON-NLS-1$

  /**
   * Launch the application.
   */
  public static void main(String[] args)
  {
    try
    {
      final Configuration config = new Configuration(new File("tfmt.conf"));
      config.load();
      final ControlPanelDialog dialog = new ControlPanelDialog(null, config);
      dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      dialog.setVisible(true);
    } catch (final Exception e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Create the dialog.
   * 
   * @param connection
   */
  public ControlPanelDialog(final MasterConnectionProcessor connection,
      final Configuration config)
  {
    setAlwaysOnTop(true);
    this.connection = connection;
    this.config = config;

    setTitle(BUNDLE.getString("ControlPanelDialog.this.title")); //$NON-NLS-1$
    setBounds(100, 100, 470, 329);
    getContentPane().setLayout(new MigLayout("", "[][grow]", "[273px,grow]"));

    getContentPane().add(switchPanel, "cell 0 0,growy");
    switchPanel.setLayout(new MigLayout("", "0[]0", "0[]0"));
    btnAntiSpamSwitch.setFocusable(false);
    btnAntiSpamSwitch.setSelected(true);

    switchPanel.add(btnAntiSpamSwitch, "cell 0 0,growx,aligny top");

    getContentPane().add(cardPanel, "cell 1 0,grow");
    cardPanel.setLayout(new CardLayout(0, 0));

    cardPanel.add(antiSpamCard, "antiSpam");
    final GroupLayout gl_antiSpamCard = new GroupLayout(antiSpamCard);
    gl_antiSpamCard.setHorizontalGroup(gl_antiSpamCard.createParallelGroup(
        Alignment.LEADING).addComponent(scrollPane, GroupLayout.DEFAULT_SIZE,
        312, Short.MAX_VALUE));
    gl_antiSpamCard.setVerticalGroup(gl_antiSpamCard.createParallelGroup(
        Alignment.LEADING).addComponent(scrollPane, GroupLayout.DEFAULT_SIZE,
        261, Short.MAX_VALUE));

    scrollPane.setViewportView(panel);
    panel.setLayout(new MigLayout("", "[320.00,grow]", "[][][grow]"));
    chckbxEnabled.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        connection.getAntiSpamExtension()
            .setEnabled(chckbxEnabled.isSelected());
      }
    });

    panel.add(chckbxEnabled, "cell 0 0");
    panel_1.setBorder(new TitledBorder(
        new LineBorder(new Color(184, 207, 229)), BUNDLE
            .getString("Border.Title.QuickFiltering"), TitledBorder.LEADING,
        TitledBorder.TOP, null, null));

    panel.add(panel_1, "cell 0 1,grow");
    panel_1.setLayout(new MigLayout("", "[grow][]", "[][][]"));
    panel_1.add(lblFilterMessagesThat, "cell 0 0 2 1");
    panel_1.add(textField, "cell 0 1 2 1,growx");
    textField.setText("");
    textField.setColumns(10);
    btnAddFastFilter.setHorizontalTextPosition(SwingConstants.CENTER);
    btnAddFastFilter.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        final String text = textField.getText();
        if (text.length() > 0)
        {
          final ArrayList<String> filters = new ArrayList<>(Arrays
              .asList(config.getUserFilterList().split("[\r\n]+")));
          filters.add(".*" + text + ".*");
          final StringBuilder sb = new StringBuilder();
          String delimiter = "";
          for (final String filter : filters)
          {
            if (!filter.isEmpty())
            {
              sb.append(delimiter).append(filter);
              delimiter = "\n";
            }
          }
          config.setUserFilterList(sb.toString());

          connection.getAntiSpamExtension().loadFilters();
          textField.setText("");
          textArea.append(String.format(Phrase.AddedFilter.toString(), ".*"
              + text + ".*\n"));
        }
      }
    });
    btnUserFilters.setHorizontalTextPosition(SwingConstants.CENTER);
    btnUserFilters.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        final EditUserFiltersDialog userFiltersDialog = new EditUserFiltersDialog(
            config);
        userFiltersDialog.setLocationRelativeTo(ControlPanelDialog.this);
        ControlPanelDialog.this.setAlwaysOnTop(false);
        userFiltersDialog.setVisible(true);
        ControlPanelDialog.this.setAlwaysOnTop(true);
      }
    });

    panel_1.add(btnUserFilters, "cell 0 2,alignx right");
    btnAddFastFilter.setActionCommand(BUNDLE
        .getString("ControlPanelDialog.btnAddFastFilter.actionCommand")); //$NON-NLS-1$
    panel_1.add(btnAddFastFilter, "cell 1 2,alignx right");
    panel_2.setBorder(new TitledBorder(null, BUNDLE
        .getString("Border.Title.Log"), TitledBorder.LEADING, TitledBorder.TOP,
        null, null));

    panel.add(panel_2, "cell 0 2,grow");
    panel_2.setLayout(new MigLayout("", "[grow]", "[grow]"));

    panel_2.add(scrollPane_1, "cell 0 0,grow");
    scrollPane_1.setViewportView(textArea);
    textArea.setText("");
    antiSpamCard.setLayout(gl_antiSpamCard);

    final ActionListener closeListener = new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        ControlPanelDialog.this.dispatchEvent(new WindowEvent(
            ControlPanelDialog.this, WindowEvent.WINDOW_CLOSING));
      }
    };

    getRootPane().registerKeyboardAction(closeListener,
        KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
        JComponent.WHEN_IN_FOCUSED_WINDOW);

    final ActionListener switchListener = new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        ((CardLayout) cardPanel.getLayout()).show(cardPanel,
            e.getActionCommand());
      }
    };

    final JToggleButton[] switches = {btnAntiSpamSwitch};
    for (final JToggleButton button : switches)
    {
      button.addActionListener(switchListener);
      switchGroup.add(button);
    }

    chckbxEnabled.setSelected(config.isAntiSpamEnabled());
  }

  public void process(AntiSpamExtensionStatusEvent event)
  {
    final AntiSpamExtension antiSpam = event.getAntiSpamExtension();
    switch (event.getStatus())
    {
    case MESSAGE_FILTERED:
      final String playerName = antiSpam.getLastPlayerName();
      final String message = antiSpam.getLastMessage();
      message.replace("\n", "");
      message.replace("\r", "");
      final StringBuilder sb = new StringBuilder();
      sb.append('[').append(playerName).append("] ").append(message)
          .append('\n');
      textArea.append(sb.toString());
    break;
    default:
    break;

    }
  }

  public void process(final ControlPanelExtensionStatusEvent event)
  {
    EventQueue.invokeLater(new Runnable()
    {
      @Override
      public void run()
      {
        switch (event.getStatus())
        {
        case SHOW:
          ControlPanelDialog.this.setVisible(true);
          ControlPanelDialog.this.toFront();
          ControlPanelDialog.this.requestFocusInWindow();
        break;
        default:
        break;
        }
      }
    });

  }
}
