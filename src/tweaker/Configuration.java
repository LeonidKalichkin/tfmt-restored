package tweaker;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Locale;
import java.util.Properties;

import tweaker.Updater.UpdateCheckStrategy;

public class Configuration extends Properties
{
  private static final long serialVersionUID = 1L;

  private int proxyServerPort;
  private Updater.UpdateCheckStrategy updateCheckStrategy;
  private boolean justUpdated;
  private boolean updateAutomatically;
  private boolean guiEnabled;
  private boolean cuiEnabled;
  private String languageCode;
  private String filterListUrl;
  private boolean antiSpamEnabled;
  private boolean autoLoginEnabled;
  private String lastNickname;
  private String lastPasswordHash;
  private String lastLoginUrl;
  private String lastLoginBase64;
  private String userFilterList;
  private int controlPanelHotkey;

  private final File propertiesFile;

  public Configuration(File file)
  {
    propertiesFile = file;
  }

  public void load() throws IOException
  {
    if (!propertiesFile.exists())
    {
      propertiesFile.createNewFile();
    }

    try (FileInputStream is = new FileInputStream(propertiesFile);
        InputStreamReader reader = new InputStreamReader(is, "UTF-8"))
    {
      super.load(reader);
    }

    proxyServerPort = getInt("Port", 4848);
    try
    {
      updateCheckStrategy = Updater.UpdateCheckStrategy.valueOf(getString(
          "UpdateCheckStrategy", "CHECK_AT_STARTUP").toUpperCase());
    } catch (final IllegalArgumentException e)
    {
      updateCheckStrategy = UpdateCheckStrategy.CHECK_AT_STARTUP;
    }
    justUpdated = getBoolean("JustUpdated", false);
    updateAutomatically = getBoolean("UpdateAutomatically", true);
    guiEnabled = getBoolean("GUIEnabled", true);
    cuiEnabled = getBoolean("CUIEnabled", true);
    languageCode = getString("LanguageCode", Locale.getDefault().getLanguage());
    filterListUrl = getString("FilterListURL",
        "https://raw.github.com/agath/tfmt/master/dist/filter.list");
    antiSpamEnabled = getBoolean("AntiSpamEnabled", true);
    autoLoginEnabled = getBoolean("AutoLoginEnabled", true);
    lastNickname = getString("LastNickname", null);
    lastPasswordHash = getString("LastPasswordHash", null);
    lastLoginUrl = getString("LastLoginUrl", null);
    lastLoginBase64 = getString("LastLoginBase64", null);
    userFilterList = getString("UserFilterList", "");
    controlPanelHotkey = getInt("ControlPanelHotkey", KeyEvent.VK_P);
  }

  public void save() throws IOException
  {
    setInt("Port", proxyServerPort);
    setString("UpdateCheckStrategy", updateCheckStrategy.name());
    setBoolean("JustUpdated", justUpdated);
    setBoolean("UpdateAutomatically", updateAutomatically);
    setBoolean("GUIEnabled", guiEnabled);
    setBoolean("CUIEnabled", cuiEnabled);
    setString("LanguageCode", languageCode);
    setString("FilterListURL", filterListUrl);
    setBoolean("AntiSpamEnabled", antiSpamEnabled);
    setBoolean("AutoLoginEnabled", autoLoginEnabled);
    setString("LastNickname", lastNickname);
    setString("LastPasswordHash", lastPasswordHash);
    setString("LastLoginUrl", lastLoginUrl);
    setString("LastLoginBase64", lastLoginBase64);
    setString("UserFilterList", userFilterList);
    setInt("ControlPanelHotkey", controlPanelHotkey);

    try (FileOutputStream out = new FileOutputStream(propertiesFile);
        OutputStreamWriter writer = new OutputStreamWriter(out, "UTF-8"))
    {
      super.store(writer, null);
    }
  }

  private int getInt(String key, int defaultValue)
  {
    final String value = super.getProperty(key);
    return value == null ? defaultValue : Integer.parseInt(value);
  }

  private boolean getBoolean(String key, boolean defaultValue)
  {
    final String value = super.getProperty(key);
    return value == null ? defaultValue : "yes".equals(value.toLowerCase());
  }

  private String getString(String key, String defaultValue)
  {
    final String value = super.getProperty(key);
    return value == null ? defaultValue : value;
  }

  private void setInt(String key, int value)
  {
    super.setProperty(key, Integer.toString(value));
  }

  private void setBoolean(String key, boolean value)
  {
    super.setProperty(key, value ? "yes" : "no");
  }

  private void setString(String key, String value)
  {
    if (value != null)
    {
      super.setProperty(key, value);
    }
  }

  public int getProxyServerPort()
  {
    return proxyServerPort;
  }

  public void setProxyServerPort(int proxyServerPort)
  {
    this.proxyServerPort = proxyServerPort;
  }

  public boolean isUpdateAutomatically()
  {
    return updateAutomatically;
  }

  public void setUpdateAutomatically(boolean updateAutomatically)
  {
    this.updateAutomatically = updateAutomatically;
  }

  public boolean isGuiEnabled()
  {
    return guiEnabled;
  }

  public void setGuiEnabled(boolean guiEnabled)
  {
    this.guiEnabled = guiEnabled;
  }

  public boolean isCuiEnabled()
  {
    return cuiEnabled;
  }

  public void setCuiEnabled(boolean cuiEnabled)
  {
    this.cuiEnabled = cuiEnabled;
  }

  public Updater.UpdateCheckStrategy getUpdateCheckStrategy()
  {
    return updateCheckStrategy;
  }

  public void setUpdateCheckStrategy(
      Updater.UpdateCheckStrategy updateCheckStrategy)
  {
    this.updateCheckStrategy = updateCheckStrategy;
  }

  public boolean isJustUpdated()
  {
    return justUpdated;
  }

  public void setJustUpdated(boolean justUpdated)
  {
    this.justUpdated = justUpdated;
  }

  public String getLanguageCode()
  {
    return languageCode;
  }

  public void setLanguageCode(String languageCode)
  {
    this.languageCode = languageCode;
  }

  public String getFilterListUrl()
  {
    return filterListUrl;
  }

  public void setFilterListUrl(String filterListUrl)
  {
    this.filterListUrl = filterListUrl;
  }

  public boolean isAntiSpamEnabled()
  {
    return antiSpamEnabled;
  }

  public void setAntiSpamEnabled(boolean antiSpamEnabled)
  {
    this.antiSpamEnabled = antiSpamEnabled;
  }

  public boolean isAutoLoginEnabled()
  {
    return autoLoginEnabled;
  }

  public void setAutoLoginEnabled(boolean autoLoginEnabled)
  {
    this.autoLoginEnabled = autoLoginEnabled;
  }

  public String getLastNickname()
  {
    return lastNickname;
  }

  public void setLastNickname(String lastNickname)
  {
    this.lastNickname = lastNickname;
  }

  public String getLastPasswordHash()
  {
    return lastPasswordHash;
  }

  public void setLastPasswordHash(String lastPasswordHash)
  {
    this.lastPasswordHash = lastPasswordHash;
  }

  public String getUserFilterList()
  {
    return userFilterList;
  }

  public void setUserFilterList(String userFilterList)
  {
    this.userFilterList = userFilterList;
  }

  public int getControlPanelHotkey()
  {
    return controlPanelHotkey;
  }

  public void setControlPanelHotkey(int controlPanelHotkey)
  {
    this.controlPanelHotkey = controlPanelHotkey;
  }

  public String getLastLoginUrl()
  {
    return lastLoginUrl;
  }

  public void setLastLoginUrl(String lastLoginUrl)
  {
    this.lastLoginUrl = lastLoginUrl;
  }

  public String getLastLoginBase64()
  {
    return lastLoginBase64;
  }

  public void setLastLoginBase64(String lastLoginBase64)
  {
    this.lastLoginBase64 = lastLoginBase64;
  }
}
