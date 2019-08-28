package ui;

import ui.events.AntiSpamExtensionStatusEvent;
import ui.events.ControlPanelExtensionStatusEvent;
import ui.events.ErrorEvent;
import ui.events.MasterConnectionStatusEvent;
import ui.events.ProxyServerStatusEvent;
import ui.events.UpdateProgressEvent;

public interface EventProcessor
{
  void process(ProxyServerStatusEvent event);

  void process(UpdateProgressEvent event);

  void process(ErrorEvent event);

  void process(AntiSpamExtensionStatusEvent event);

  void process(MasterConnectionStatusEvent event);

  void process(ControlPanelExtensionStatusEvent event);
}
