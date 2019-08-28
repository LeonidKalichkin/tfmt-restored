package ui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import localization.Language;
import localization.Phrase;
import net.miginfocom.swing.MigLayout;
import proxy.Socks4ProxyServer;
import transformice.connection.MasterConnectionProcessor;
import tweaker.Configuration;
import tweaker.Main;
import tweaker.Updater;
import tweaker.Updater.UpdateCheckStrategy;
import ui.events.AntiSpamExtensionStatusEvent;
import ui.events.ControlPanelExtensionStatusEvent;
import ui.events.ErrorEvent;
import ui.events.MasterConnectionStatusEvent;
import ui.events.ProxyServerStatusEvent;
import ui.events.UpdateProgressEvent;
import ui.events.UpdateProgressEvent.Status;
import ui.events.UpdateProgressEvent.UpdateProgress;

public class MainWindow extends JFrame implements Observer, EventProcessor
{
  private static final ResourceBundle BUNDLE = ResourceBundle
      .getBundle("localization.phrases"); //$NON-NLS-1$
  private static final long serialVersionUID = 1L;

  private final JPanel contentPane;

  private final Configuration config;
  private final UserInterface ui;
  private Socks4ProxyServer proxyServer;
  private ProxyServerStatusEvent.Status proxyStatus;

  private final JPanel aboutCard;
  private final JButton btnCheckForUpdates;
  private final JButton btnUpdate;
  private final JLabel lblUpdateProgress;
  private final JProgressBar progressBarUpdate;
  private final JButton btnCancelUpdate;
  private final JLabel lblChanges;
  private final JPanel settingsCard;
  private final JPanel updatePanel;
  private final JPanel proxyServerPanel;

  private Thread progressBar;
  private Thread updaterThread;
  private final ArrayList<MasterConnectionProcessor> connections =
      new ArrayList<>();
  private final HashMap<MasterConnectionProcessor, ControlPanelDialog> controlPanels =
      new HashMap<>();

  private final JTextArea textAreaChanges = new JTextArea();
  private final JPanel extensionsCard = new JPanel();
  private final JPanel helpCard = new JPanel();
  private final JLabel lblNewLabel = new JLabel(BUNDLE
      .getString("MainWindow.lblNewLabel.text_5")); //$NON-NLS-1$
  private final JTextField textField = new JTextField();
  private final JLabel lblNewLabel_1 = new JLabel(BUNDLE
      .getString("MainWindow.lblNewLabel_1.text_4")); //$NON-NLS-1$
  private final JLabel lblNewLabel_2 = new JLabel(BUNDLE
      .getString("MainWindow.lblNewLabel_2.text")); //$NON-NLS-1$
  private final JButton btnNewButton = new JButton(BUNDLE
      .getString("MainWindow.btnNewButton.text_4")); //$NON-NLS-1$
  private final JLabel lblNewLabel_3 = new JLabel(BUNDLE
      .getString("MainWindow.lblNewLabel_3.text")); //$NON-NLS-1$
  private final JScrollPane scrollPane = new JScrollPane();
  private final JTable table = new JTable();
  private final JButton btnControlPanel = new JButton(BUNDLE
      .getString("MainWindow.btnNewButton_1.text")); //$NON-NLS-1$
  private final JScrollPane scrollPane_1 = new JScrollPane();
  private final JScrollPane scrollPane_2 = new JScrollPane();
  private final JPanel panel = new JPanel();
  private final JCheckBox chckbxAntiSpamEnabled = new JCheckBox(BUNDLE
      .getString("MainWindow.chckbxAntispam.text")); //$NON-NLS-1$
  private final JScrollPane scrollPane_3 = new JScrollPane();
  private final JPanel panel_1 = new JPanel();
  private final JLabel lblUpdateCheckStrategy = new JLabel(BUNDLE
      .getString("MainWindow.lblUpdatePolicy.text")); //$NON-NLS-1$
  private final JComboBox<Object> comboBoxUpdateCheckStrategy =
      new JComboBox<Object>();
  private final JCheckBox chckbxUpdateAutomatically = new JCheckBox(BUNDLE
      .getString("MainWindow.chckbxNewCheckBox.text_2")); //$NON-NLS-1$
  private final JPanel panel_2 = new JPanel();
  private final JPanel panel_3 = new JPanel();
  private final JLabel lblLanguage = new JLabel(BUNDLE
      .getString("MainWindow.lblLanguage.text")); //$NON-NLS-1$
  private final JComboBox<Object> comboBoxLanguage = new JComboBox<Object>();
  private final JLabel lblFilterUrl = new JLabel(BUNDLE
      .getString("MainWindow.lblFilterUrl.text")); //$NON-NLS-1$
  private final JTextField textFieldFilterListUrl = new JTextField();
  private final JButton btnEditUserFilters = new JButton(BUNDLE
      .getString("MainWindow.btnEditUserFilters.text")); //$NON-NLS-1$
  private final JCheckBox chckbxAutoLoginEnabled = new JCheckBox(BUNDLE
      .getString("MainWindow.chckbxNewCheckBox.text_1")); //$NON-NLS-1$
  private final JPanel cardPanel = new JPanel();
  private final JPanel switchPanel = new JPanel();
  private final JToggleButton btnProxyServerSwitch = new JToggleButton(BUNDLE
      .getString("MainWindow.btnNewButton_4.text")); //$NON-NLS-1$
  private final JToggleButton btnExtensionsSwitch = new JToggleButton(BUNDLE
      .getString("MainWindow.btnNewButton_1.text_3")); //$NON-NLS-1$
  private final JToggleButton btnSettingsSwitch = new JToggleButton(BUNDLE
      .getString("MainWindow.btnNewButton_2.text_1")); //$NON-NLS-1$
  private final JToggleButton btnUpdateSwitch = new JToggleButton(BUNDLE
      .getString("MainWindow.btnNewButton_3.text_1")); //$NON-NLS-1$
  private final ButtonGroup switchGroup = new ButtonGroup();
  private final JPanel proxyServerCard = new JPanel();
  private final JScrollPane scrollPane_4 = new JScrollPane();
  private final JPanel panel_7 = new JPanel();
  private final JScrollPane scrollPane_5 = new JScrollPane();
  private final JPanel panel_6 = new JPanel();
  private final JScrollPane scrollPane_6 = new JScrollPane();
  private final JPanel panel_8 = new JPanel();
  private final JPanel updateCard = new JPanel();
  private final JScrollPane scrollPane_7 = new JScrollPane();
  private final JPanel panel_4 = new JPanel();
  private final JLabel lblNewLabel_4 = new JLabel(BUNDLE
      .getString("MainWindow.lblNewLabel_4.text_3")); //$NON-NLS-1$
  private final JTextField textField_1 = new JTextField();
  private final JPanel panel_5 = new JPanel();
  protected boolean reloading;
  private UpdateProgress progress;
  protected UpdateProgress checkResult;
  private final JLabel lblCheckResult = new JLabel(BUNDLE.getString("MainWindow.lblNewLabel_5.text_3")); //$NON-NLS-1$
  private final JCheckBox chckbxTrayIcon = new JCheckBox(BUNDLE.getString("MainWindow.chckbxNewCheckBox.text")); //$NON-NLS-1$
  private final JCheckBox chckbxStartMinimized = new JCheckBox(BUNDLE.getString("MainWindow.chckbxNewCheckBox.text_3")); //$NON-NLS-1$

  /**
   * Launch the application.
   */
  public static void main(String[] args)
  {
    try
    {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (final Throwable e)
    {
      e.printStackTrace();
    }
    EventQueue.invokeLater(new Runnable()
    {
      @Override
      public void run()
      {
        try
        {
          final Configuration config =
              new Configuration(new File(Main.APPLICATION_DATA,
                  Main.CONFIG_NAME));
          config.load();
          final MainWindow frame = new MainWindow(null, config);
          frame.setVisible(true);
        } catch (final Exception e)
        {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the frame.
   * 
   * @wbp.parser.constructor
   */
  public MainWindow(final UserInterface ui, final Configuration config)
  {
    this.ui = ui;
    this.config = config;

    manualInit();
    textField_1.setHorizontalAlignment(SwingConstants.CENTER);

    textField_1.setColumns(7);
    textField_1.addKeyListener(new KeyAdapter()
    {
      @Override
      public void keyPressed(KeyEvent e)
      {
        final int keyCode = e.getKeyCode();
        textField_1.setText(KeyEvent.getKeyText(keyCode));
        config.setControlPanelHotkey(keyCode);
        for (final MasterConnectionProcessor connection : connections)
        {
          connection.getControlPanelExtension().setHotkey(keyCode);
        }
        e.consume();
      }

      @Override
      public void keyTyped(KeyEvent e)
      {
        e.consume();
      }
    });

    btnSettingsSwitch.setActionCommand("settingsCard");

    btnSettingsSwitch.setFocusable(false);
    btnUpdateSwitch.setActionCommand("updateCard");
    btnUpdateSwitch.setFocusable(false);
    btnExtensionsSwitch.setActionCommand("extensionsCard");
    btnExtensionsSwitch.setFocusable(false);
    btnProxyServerSwitch.setActionCommand("proxyServerCard");
    btnProxyServerSwitch.setSelected(true);
    btnProxyServerSwitch.setFocusable(false);

    setTitle(BUNDLE.getString("MainWindow.this.title"));
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    setBounds(100, 100, 461, 402);
    contentPane = new JPanel();
    contentPane.setOpaque(false);
    setContentPane(contentPane);
    contentPane.setLayout(new MigLayout("", "[][grow]", "[grow]"));

    contentPane.add(switchPanel, "cell 0 0,grow");
    final GroupLayout gl_switchPanel = new GroupLayout(switchPanel);
    gl_switchPanel.setHorizontalGroup(gl_switchPanel.createParallelGroup(
        Alignment.LEADING).addComponent(btnProxyServerSwitch,
        GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(btnExtensionsSwitch, GroupLayout.DEFAULT_SIZE,
            GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(
            btnSettingsSwitch, GroupLayout.DEFAULT_SIZE,
            GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(
            btnUpdateSwitch, GroupLayout.DEFAULT_SIZE,
            GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
    gl_switchPanel.setVerticalGroup(gl_switchPanel.createParallelGroup(
        Alignment.LEADING).addGroup(
        gl_switchPanel.createSequentialGroup().addComponent(
            btnProxyServerSwitch).addComponent(btnExtensionsSwitch)
            .addComponent(btnSettingsSwitch).addComponent(btnUpdateSwitch)));
    switchPanel.setLayout(gl_switchPanel);

    contentPane.add(cardPanel, "cell 1 0,grow");
    cardPanel.setLayout(new CardLayout(0, 0));

    textField.setColumns(5);

    proxyServerPanel = new JPanel();
    proxyServerPanel.setBorder(null);
    proxyServerPanel.setLayout(new MigLayout("", "[][grow][]",
        "[][][][][grow][]"));

    proxyServerPanel.add(lblNewLabel, "cell 0 0,alignx trailing");
    proxyServerPanel.add(textField, "cell 1 0");
    btnNewButton.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        if (proxyStatus == ProxyServerStatusEvent.Status.CREATED
            || proxyStatus == ProxyServerStatusEvent.Status.STOPPED
            || proxyStatus == ProxyServerStatusEvent.Status.FAILED)
        {
          try
          {
            final int port = Integer.parseInt(textField.getText());
            config.setProxyServerPort(port);
            btnNewButton.setEnabled(false);
            new Thread(proxyServer).start();
          } catch (final NumberFormatException e1)
          {
            textField.setText(Integer.toString(config.getProxyServerPort()));
            process(new ErrorEvent(ErrorEvent.Error.InvalidPortFormat, null));
          }
        }
        else
        {
          proxyServer.stop();
        }
      }
    });

    proxyServerPanel.add(btnNewButton, "cell 2 0 1 2,growx");

    proxyServerPanel.add(lblNewLabel_1, "cell 0 1,alignx trailing");

    proxyServerPanel.add(lblNewLabel_2, "cell 1 1");

    proxyServerPanel.add(lblNewLabel_3, "cell 0 3 3 1,alignx center");

    proxyServerPanel.add(scrollPane, "cell 0 4 3 1,grow");
    table.setPreferredScrollableViewportSize(new Dimension(100, 100));

    scrollPane.setViewportView(table);
    btnControlPanel.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        final int index = table.getSelectedRow();
        controlPanels.get(connections.get(index)).setVisible(true);
      }
    });
    btnControlPanel.setEnabled(false);

    proxyServerPanel.add(btnControlPanel, "cell 1 5 2 1,alignx right");

    cardPanel.add(proxyServerCard, "proxyServerCard");
    final GroupLayout gl_proxyServerCard = new GroupLayout(proxyServerCard);
    gl_proxyServerCard.setHorizontalGroup(gl_proxyServerCard
        .createParallelGroup(Alignment.LEADING).addComponent(scrollPane_4,
            GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE));
    gl_proxyServerCard.setVerticalGroup(gl_proxyServerCard.createParallelGroup(
        Alignment.LEADING).addComponent(scrollPane_4, GroupLayout.DEFAULT_SIZE,
        339, Short.MAX_VALUE));

    scrollPane_4.setViewportView(proxyServerPanel);
    proxyServerCard.setLayout(gl_proxyServerCard);
    extensionsCard.setBorder(null);
    cardPanel.add(extensionsCard, "extensionsCard");

    scrollPane_2.setViewportView(panel);
    panel.setLayout(new MigLayout("", "[310px,grow]", "[grow]"));
    panel_5.setBorder(new TitledBorder(null, BUNDLE
        .getString("Border.Title.Extensions"), TitledBorder.LEADING,
        TitledBorder.TOP, null, null));

    panel.add(panel_5, "cell 0 0,growx,aligny top");
    panel_5.setLayout(new MigLayout("", "[][]", "[][][][][]"));
    panel_5.add(chckbxAntiSpamEnabled, "cell 0 0 2 1");
    chckbxAntiSpamEnabled.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        config.setAntiSpamEnabled(chckbxAntiSpamEnabled.isSelected());
      }
    });
    panel_5.add(lblFilterUrl, "cell 0 1");
    panel_5.add(textFieldFilterListUrl, "cell 0 2 2 1,growx");

    textFieldFilterListUrl.addFocusListener(new FocusAdapter()
    {
      @Override
      public void focusLost(FocusEvent e)
      {
        config.setFilterListUrl(textFieldFilterListUrl.getText());
        for (final MasterConnectionProcessor connection : connections)
        {
          connection.getAntiSpamExtension().loadFilters();
        }
      }
    });
    textFieldFilterListUrl.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        config.setFilterListUrl(textFieldFilterListUrl.getText());
        for (final MasterConnectionProcessor connection : connections)
        {
          connection.getAntiSpamExtension().loadFilters();
        }
      }
    });
    textFieldFilterListUrl.setColumns(10);
    panel_5.add(btnEditUserFilters, "cell 0 3 2 1,growx");
    btnEditUserFilters.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        final EditUserFiltersDialog dialog = new EditUserFiltersDialog(config);
        dialog.setLocationRelativeTo(MainWindow.this);
        dialog.setVisible(true);
      }
    });
    panel_5.add(chckbxAutoLoginEnabled, "cell 0 4 2 1");
    chckbxAutoLoginEnabled.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        config.setAutoLoginEnabled(chckbxAutoLoginEnabled.isSelected());
      }
    });
    final GroupLayout gl_extensionsCard = new GroupLayout(extensionsCard);
    gl_extensionsCard.setHorizontalGroup(gl_extensionsCard.createParallelGroup(
        Alignment.LEADING).addComponent(scrollPane_2, GroupLayout.DEFAULT_SIZE,
        313, Short.MAX_VALUE));
    gl_extensionsCard.setVerticalGroup(gl_extensionsCard.createParallelGroup(
        Alignment.LEADING).addComponent(scrollPane_2, GroupLayout.DEFAULT_SIZE,
        339, Short.MAX_VALUE));
    extensionsCard.setLayout(gl_extensionsCard);

    settingsCard = new JPanel();
    settingsCard.setBorder(null);
    cardPanel.add(settingsCard, "settingsCard");

    scrollPane_3.setViewportView(panel_1);
    panel_1.setLayout(new MigLayout("", "[310px,grow]", "[-51.00][][grow]"));
    panel_3.setBorder(new TitledBorder(null, BUNDLE
        .getString("Border.Title.Appearance"), TitledBorder.LEADING,
        TitledBorder.TOP, null, null));

    panel_1.add(panel_3, "cell 0 0,growx");
    panel_3.setLayout(new MigLayout("", "[][grow]", "[][][][][]"));

    panel_3.add(lblLanguage, "cell 0 0,alignx trailing");
    comboBoxLanguage.addItemListener(new ItemListener()
    {
      @Override
      public void itemStateChanged(ItemEvent e)
      {
        if (e.getStateChange() == ItemEvent.SELECTED)
        {
          final int index = comboBoxLanguage.getSelectedIndex();
          config.setLanguageCode(Language.values()[index].getCode());
          final int result =
              JOptionPane
                  .showConfirmDialog(MainWindow.this,
                      Phrase.ConfirmReloadingAfterLanguageChange.toString(),
                      Phrase.ConfirmReloading.toString(),
                      JOptionPane.YES_NO_OPTION);
          if (result == JOptionPane.YES_OPTION)
          {
            ui.deleteObserver(MainWindow.this);
            reloading = true;
            MainWindow.this.dispatchEvent(new WindowEvent(MainWindow.this,
                WindowEvent.WINDOW_CLOSING));
            Updater.reload(ui, config);
          }
        }
      }
    });

    panel_3.add(comboBoxLanguage, "cell 1 0,alignx left");
    
    panel_3.add(chckbxTrayIcon, "cell 0 2 2 1");
    
    panel_3.add(chckbxStartMinimized, "cell 0 3 2 1");
    panel_2.setBorder(new TitledBorder(
        new LineBorder(new Color(184, 207, 229)), BUNDLE
            .getString("Border.Title.UpdateSettings"), TitledBorder.LEADING,
        TitledBorder.TOP, null, null));

    panel_1.add(panel_2, "cell 0 1,growx");
    panel_2.setLayout(new MigLayout("", "[][]", "[][]"));
    panel_2.add(lblUpdateCheckStrategy, "cell 0 0,alignx left");
    comboBoxUpdateCheckStrategy.addItemListener(new ItemListener()
    {
      @Override
      public void itemStateChanged(ItemEvent e)
      {
        if (e.getStateChange() == ItemEvent.SELECTED)
        {
          final int index = comboBoxUpdateCheckStrategy.getSelectedIndex();
          config
              .setUpdateCheckStrategy(Updater.UpdateCheckStrategy.values()[index]);

          Updater.setUpdateCheckStrategy(ui, config);
        }
      }
    });
    panel_2.add(comboBoxUpdateCheckStrategy, "cell 1 0,alignx left");
    chckbxUpdateAutomatically.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        config.setUpdateAutomatically(chckbxUpdateAutomatically.isSelected());
      }
    });
    panel_2.add(chckbxUpdateAutomatically, "cell 0 1 2 1");
    panel_4.setBorder(new TitledBorder(null, BUNDLE
        .getString("Border.Title.ShortcutPanel"), TitledBorder.LEADING,
        TitledBorder.TOP, null, null));

    panel_1.add(panel_4, "cell 0 2,growx,aligny top");
    panel_4.setLayout(new MigLayout("", "[][grow,shrinkprio 0]", "[]"));

    panel_4.add(lblNewLabel_4, "cell 0 0,alignx trailing");

    panel_4.add(textField_1, "cell 1 0,alignx left");
    final GroupLayout gl_settingsCard = new GroupLayout(settingsCard);
    gl_settingsCard.setHorizontalGroup(gl_settingsCard.createParallelGroup(
        Alignment.LEADING).addComponent(scrollPane_3, GroupLayout.DEFAULT_SIZE,
        313, Short.MAX_VALUE));
    gl_settingsCard.setVerticalGroup(gl_settingsCard.createParallelGroup(
        Alignment.LEADING).addGroup(
        gl_settingsCard.createSequentialGroup().addComponent(scrollPane_3,
            GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE).addGap(4)));
    settingsCard.setLayout(gl_settingsCard);

    updatePanel = new JPanel();
    updatePanel.setBorder(null);
    updatePanel.setLayout(new MigLayout("", "[0,grow][165.00]", "[][center][][][][][grow]"));

    btnCheckForUpdates =
        new JButton(BUNDLE.getString("MainWindow.btnCheckForUpdates.text")); //$NON-NLS-1$
    btnCheckForUpdates.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        btnCheckForUpdates.setEnabled(false);
        updaterThread = new Thread()
        {
          @Override
          public void run()
          {
            MainWindow.this.progress = new UpdateProgress(Status.CHECKING_FOR_UPDATES);
            ui.process(new UpdateProgressEvent(progress));
            
            MainWindow.this.checkResult = Updater.checkForUpdates(progress);
            ui.process(new UpdateProgressEvent(checkResult));
          }
        };
        updaterThread.start();
      }
    });
    
    updatePanel.add(lblCheckResult, "cell 0 0 1 2");
    updatePanel.add(btnCheckForUpdates, "cell 1 0,grow");

    btnUpdate = new JButton(BUNDLE.getString("MainWindow.btnUpdate.text")); //$NON-NLS-1$
    btnUpdate.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        updaterThread = new Thread()
        {
          @Override
          public void run()
          {
            progress = new UpdateProgress(Status.PERFORMING);
            Updater.update(progress, checkResult);
            
            if (progress.getStatus() == Status.FINISHED)
            {
              Updater.reload(ui, config);
            }
          }
        };
        updaterThread.start();
      }
    });
    btnUpdate.setEnabled(false);
    updatePanel.add(btnUpdate, "cell 1 1,grow");

    lblUpdateProgress =
        new JLabel(BUNDLE.getString("MainWindow.lblNewLabel_1.text"));
    lblUpdateProgress.setEnabled(false);
    lblUpdateProgress.setFont(new Font("Dialog", Font.PLAIN, 10));
    updatePanel.add(lblUpdateProgress, "cell 0 2 2 1,growx,aligny bottom");

    progressBarUpdate = new JProgressBar();
    progressBarUpdate.setEnabled(false);
    progressBarUpdate.setStringPainted(true);
    updatePanel.add(progressBarUpdate, "cell 0 3 2 1,growx");

    btnCancelUpdate =
        new JButton(BUNDLE.getString("MainWindow.btnCancel.text"));
    btnCancelUpdate.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent arg0)
      {
        if (progress.getStatus() == Status.PERFORMING || progress.getStatus() == Status.CHECKING_FOR_UPDATES);
        {
          btnCancelUpdate.setEnabled(false);
          progress.setWork(false);
        }
      }
    });
    btnCancelUpdate.setEnabled(false);
    updatePanel.add(btnCancelUpdate, "cell 1 4,alignx right");

    lblChanges = new JLabel(BUNDLE.getString("MainWindow.lblNewLabel.text_1"));
    lblChanges.setEnabled(false);
    updatePanel.add(lblChanges, "cell 0 5 2 1,alignx center");

    updatePanel.add(scrollPane_1, "cell 0 6 2 1,grow");
    textAreaChanges.setLineWrap(true);
    scrollPane_1.setViewportView(textAreaChanges);
    textAreaChanges.setEnabled(false);
    textAreaChanges.setEditable(false);
    textAreaChanges.setText("");
    helpCard.setBorder(null);
    cardPanel.add(helpCard, "helpCard");
    final GroupLayout gl_helpCard = new GroupLayout(helpCard);
    gl_helpCard.setHorizontalGroup(gl_helpCard.createParallelGroup(
        Alignment.LEADING).addComponent(scrollPane_5, Alignment.TRAILING,
        GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE));
    gl_helpCard.setVerticalGroup(gl_helpCard.createParallelGroup(
        Alignment.LEADING).addComponent(scrollPane_5, Alignment.TRAILING,
        GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE));

    scrollPane_5.setViewportView(panel_6);
    panel_6.setLayout(new MigLayout("", "[]", "[]"));
    helpCard.setLayout(gl_helpCard);

    aboutCard = new JPanel();
    aboutCard.setBorder(null);
    cardPanel.add(aboutCard, "aboutCard");
    final GroupLayout gl_aboutCard = new GroupLayout(aboutCard);
    gl_aboutCard.setHorizontalGroup(gl_aboutCard.createParallelGroup(
        Alignment.LEADING).addComponent(scrollPane_6, GroupLayout.DEFAULT_SIZE,
        313, Short.MAX_VALUE));
    gl_aboutCard.setVerticalGroup(gl_aboutCard.createParallelGroup(
        Alignment.LEADING).addComponent(scrollPane_6, GroupLayout.DEFAULT_SIZE,
        339, Short.MAX_VALUE));

    scrollPane_6.setViewportView(panel_8);
    panel_8.setLayout(new MigLayout("", "[]", "[]"));
    aboutCard.setLayout(gl_aboutCard);

    cardPanel.add(updateCard, "updateCard");
    final GroupLayout gl_updateCard = new GroupLayout(updateCard);
    gl_updateCard.setHorizontalGroup(gl_updateCard.createParallelGroup(
        Alignment.LEADING).addComponent(scrollPane_7, GroupLayout.DEFAULT_SIZE,
        313, Short.MAX_VALUE));
    gl_updateCard.setVerticalGroup(gl_updateCard.createParallelGroup(
        Alignment.LEADING).addComponent(scrollPane_7, GroupLayout.DEFAULT_SIZE,
        339, Short.MAX_VALUE));
    updateCard.setLayout(gl_updateCard);
    panel_7.setLayout(new MigLayout("", "[]", "[]"));
    scrollPane_7.setViewportView(updatePanel);

    setSize(new Dimension(520, 364));
  }

  private void manualInit()
  {
    UIManager.getDefaults().put("TextArea.font",
        UIManager.getFont("TextField.font"));

    addWindowListener(new WindowAdapter()
    {
      @Override
      public void windowClosing(WindowEvent e)
      {
        if (reloading)
        {
          MainWindow.this.dispose();
        }
        else if (connections.isEmpty())
        {
          System.exit(0);
        }
        else
        {
          final int result =
              JOptionPane.showConfirmDialog(MainWindow.this,
                  Phrase.ConfirmExitConnectionsAlive.toString(),
                  Phrase.ConfirmExit.toString(), JOptionPane.YES_NO_OPTION);
          if (result == JOptionPane.YES_OPTION)
          {
            System.exit(0);
          }
        }
      }
    });

    textField.setText(Integer.toString(config.getProxyServerPort()));
    chckbxUpdateAutomatically.setSelected(config.isCuiEnabled());
    chckbxAntiSpamEnabled.setSelected(config.isAntiSpamEnabled());

    final ArrayList<String> languages = new ArrayList<>();
    String selectedLanguage = null;
    for (final Language language : Language.values())
    {
      languages.add(BUNDLE.getString(language.getName()));
      if (language.getCode().equals(config.getLanguageCode()))
      {
        selectedLanguage = BUNDLE.getString(language.getName());
      }
    }
    comboBoxLanguage.setModel(new DefaultComboBoxModel<Object>(languages
        .toArray()));
    comboBoxLanguage.setSelectedItem(selectedLanguage);

    final ArrayList<String> strategies = new ArrayList<>();
    for (final UpdateCheckStrategy strategy : UpdateCheckStrategy.values())
    {
      strategies.add(BUNDLE.getString(strategy.getName()));
    }
    comboBoxUpdateCheckStrategy.setModel(new DefaultComboBoxModel<Object>(
        strategies.toArray()));
    comboBoxUpdateCheckStrategy.setSelectedIndex(config
        .getUpdateCheckStrategy().ordinal());

    textFieldFilterListUrl.setText(config.getFilterListUrl());

    table.setModel(new DefaultTableModel(new Object[][] {}, new String[] {
        BUNDLE.getString("Server"), BUNDLE.getString("Nickname"),
        BUNDLE.getString("Room")})
    {
      private static final long serialVersionUID = 1L;
      Class<?>[] columnTypes = new Class[] {String.class, String.class,
          String.class};

      @Override
      public Class<?> getColumnClass(int columnIndex)
      {
        return columnTypes[columnIndex];
      }

      @Override
      public boolean isCellEditable(int row, int column)
      {
        return false;
      }
    });

    chckbxAutoLoginEnabled.setSelected(config.isAutoLoginEnabled());

    final ActionListener switchListener = new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        ((CardLayout) cardPanel.getLayout()).show(cardPanel, e
            .getActionCommand());
      }
    };

    final JToggleButton[] switches =
        {btnProxyServerSwitch, btnExtensionsSwitch, btnSettingsSwitch,
            btnUpdateSwitch};
    for (final JToggleButton button : switches)
    {
      button.addActionListener(switchListener);
      switchGroup.add(button);
    }

    table.getSelectionModel().addListSelectionListener(
        new ListSelectionListener()
        {

          @Override
          public void valueChanged(ListSelectionEvent e)
          {
            final int index = table.getSelectedRow();
            if (index == -1)
            {
              btnControlPanel.setEnabled(false);
            }
            else
            {
              btnControlPanel.setEnabled(true);
            }
          }
        });

    textField_1.setText(KeyEvent.getKeyText(config.getControlPanelHotkey()));
  }

  @Override
  public void update(Observable o, Object arg)
  {
    ((Event) arg).accept(this);
  }

  @Override
  public void process(final ProxyServerStatusEvent event)
  {
    proxyServer = event.getProxyServer();
    proxyStatus = event.getStatus();
    EventQueue.invokeLater(new Runnable()
    {

      @Override
      public void run()
      {
        switch (event.getStatus())
        {
        case STARTED:
          btnNewButton.setEnabled(true);
          textField.setEditable(false);
          btnNewButton.setText(Phrase.Stop.toString());
          lblNewLabel_2.setText(Phrase.Running.toString());
        break;
        case FAILED:
        case STOPPED:
          btnNewButton.setEnabled(true);
          textField.setEditable(true);
          btnNewButton.setText(Phrase.Start.toString());
          lblNewLabel_2.setText(Phrase.NotRunning.toString());
        break;
        default:
        break;
        }
      }

    });
  }

  @Override
  public void process(final UpdateProgressEvent event)
  {
    this.progress = event.getProgress();
    final UpdateProgressEvent.Status status = progress.getStatus();

    EventQueue.invokeLater(new Runnable()
    {

      @Override
      public void run()
      {
        switch (status)
        {
        case CHECKING_FOR_UPDATES:
          btnCancelUpdate.setEnabled(true);
          btnUpdateSwitch.setSelected(true);
          btnCheckForUpdates.setEnabled(false);
          lblCheckResult.setText("");
          lblUpdateProgress.setEnabled(true);
          lblUpdateProgress.setText(Phrase.CheckingForUpdates.toString());
          progressBarUpdate.setEnabled(true);
          progressBarUpdate.setIndeterminate(true);
        break;
        case PERFORMING:
          btnCancelUpdate.setEnabled(true);
          lblUpdateProgress.setText(Phrase.Downloading.toString());

          progressBar = new Thread()
          {
            @Override
            public void run()
            {
              while (!Thread.currentThread().isInterrupted())
              {
                progressBarUpdate.setValue((int) Math.floor(progress
                    .getProgress()
                    * 100 / progress.getLength()));
                try
                {
                  Thread.sleep(50);
                } catch (final InterruptedException e)
                {
                  break;
                }
              }
            }
          };
          progressBar.start();
        break;
        case FAILED:
          if (progressBar != null)
          {
            progressBar.interrupt();
          }
          progressBar = null;
          progressBarUpdate.setValue(0);
          progressBarUpdate.setEnabled(false);
          btnUpdate.setEnabled(true);
          btnCancelUpdate.setEnabled(false);
        break;
        case FINISHED:
          progressBar.interrupt();
          progressBar = null;
          reloading = true;
          ui.deleteObserver(MainWindow.this);
          MainWindow.this.dispatchEvent(new WindowEvent(MainWindow.this,
              WindowEvent.WINDOW_CLOSING));
        break;
        case JUST_UPDATED:
          btnUpdateSwitch.setSelected(true);
          lblChanges.setEnabled(true);
          textAreaChanges.setEnabled(true);
          textAreaChanges.setText(Phrase.Changes.toString());
          progressBarUpdate.setValue(100);
          progressBarUpdate.setEnabled(false);
          lblCheckResult.setText(Phrase.UpdateFinished.toString());
        break;
        case NO_UPDATES:
          lblCheckResult.setText(Phrase.UpToDate.toString());
          lblUpdateProgress.setText("");
          progressBarUpdate.setIndeterminate(false);
          progressBarUpdate.setEnabled(false);
          btnCheckForUpdates.setEnabled(true);
        break;
        case STOPPED:
          progressBar.interrupt();
          progressBar = null;
          progressBarUpdate.setValue(0);
          progressBarUpdate.setEnabled(false);
          lblUpdateProgress.setText(Phrase.UpdateStopped.toString());
          btnUpdate.setEnabled(true);
        break;
        case UPDATE_AVAILABLE:
          btnCheckForUpdates.setEnabled(false);
          progressBarUpdate.setIndeterminate(false);
          lblUpdateProgress.setText("");
          lblCheckResult.setText(Phrase.UpdateAvailable.toString());
          btnUpdate.setEnabled(true);
        break;
        case UPDATE_CHECK_FAILED:
          btnCheckForUpdates.setEnabled(true);
          progressBarUpdate.setIndeterminate(false);
          progressBarUpdate.setEnabled(false);
          lblUpdateProgress.setText("");
          lblCheckResult.setText(Phrase.ErrorCheckingForUpdates.toString());
        break;
        case CHECK_STOPPED:
          progressBarUpdate.setValue(0);
          progressBarUpdate.setEnabled(false);
          lblUpdateProgress.setText(Phrase.UpdateStopped.toString());
          btnUpdate.setEnabled(true);          
        default:
          break;
        }
      }
    });
  }

  @Override
  public void process(ErrorEvent event)
  {
    switch (event.getError())
    {
    case InvalidPortFormat:
      JOptionPane.showMessageDialog(this, Phrase.ErrorInvalidPortFormat.toString(), Phrase.Error
          .toString(), JOptionPane.ERROR_MESSAGE);      
      break;
    case Listening:
      JOptionPane.showMessageDialog(this, Phrase.ErrorListening.toString(), Phrase.Error
          .toString(), JOptionPane.ERROR_MESSAGE);  
      break;
    case ProcessingConnection:
      //forgive
      break;
    case ReadingClientSocket:
      JOptionPane.showMessageDialog(this, Phrase.ErrorReadingClientSocket.toString(), Phrase.Error
          .toString(), JOptionPane.ERROR_MESSAGE);      
      break;
    default:
      break;
    
    }
  }

  @Override
  public void process(AntiSpamExtensionStatusEvent event)
  {
    controlPanels.get(event.getAntiSpamExtension().getConnection()).process(
        event);
  }

  @Override
  public void process(final MasterConnectionStatusEvent event)
  {
    final MasterConnectionProcessor connection = event.getConnection();

    switch (event.getStatus())
    {
    case CLOSED:
    {
      final int index = connections.indexOf(connection);
      controlPanels.get(connections.get(index)).dispose();
      controlPanels.remove(connections.get(index));
      connections.remove(index);
      EventQueue.invokeLater(new Runnable()
      {

        @Override
        public void run()
        {
          if (table.getSelectedRow() == index)
          {
            btnControlPanel.setEnabled(false);
          }
          final DefaultTableModel model = (DefaultTableModel) table.getModel();
          model.removeRow(index);
        }
      });
      break;
    }
    case ESTABLISHED:
      connections.add(connection);
      controlPanels.put(connection, new ControlPanelDialog(connection, config));
      EventQueue.invokeLater(new Runnable()
      {

        @Override
        public void run()
        {
          final DefaultTableModel model = (DefaultTableModel) table.getModel();
          model.addRow(new Object[] {
              connection.getServerSocket().getInetAddress().getHostName(),
              null, null});
        }
      });
    break;
    case ENTERED_ROOM:
    {
      final int index = connections.indexOf(connection);
      table.setValueAt(connection.getRoomName(), index, 2);
      break;
    }
    case LOGGED_IN:
    {
      final int index = connections.indexOf(connection);
      table.setValueAt(connection.getPlayerName(), index, 1);
      break;
    }
    default:
    break;

    }
  }

  @Override
  public void process(ControlPanelExtensionStatusEvent event)
  {
    controlPanels.get(event.getControlPanelExtension().getConnection())
        .process(event);
  }
}
