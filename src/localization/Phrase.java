package localization;

import java.util.ResourceBundle;

public enum Phrase
{
  StartingProxyServer, JustUpdated, CheckingForUpdates, UpdateAvailable,
  Downloading, UpdateFinished, UpdateAvailableButDisabled, NoUpdates,
  ErrorReloading, ErrorCheckingForUpdates, ErrorUpdating,
  ErrorLoadingFilterList, ErrorReadingClientSocket, ErrorListening,
  ErrorProcessingConnection, UpdateNotification, UpdateResult, ChatGreetings,
  ErrorLoadingConfiguration, ErrorCreatingEmptyPropertiesFile, Language,
  CommunityId, ErrorSavingConfiguration, Changes, UpdateFailed, UpdateStarted,
  UpdateStopped, Unpacking, WaitingForResponse, VersionAvailable, UpToDate,
  Error, CurrentVersion, ConfirmReloadingAfterLanguageChange, ConfirmReloading,
  Stop, Start, Running, NotRunning, StoppingProxyServer,
  ErrorInvalidPortFormat, AddedFilter, ConfirmExit, ConfirmExitConnectionsAlive;

  private static ResourceBundle bundle = ResourceBundle
      .getBundle("localization.phrases");

  @Override
  public String toString()
  {
    return bundle.getString(name());
  }

  public static void setBundle(ResourceBundle bundle)
  {
    Phrase.bundle = bundle;
  }
}
