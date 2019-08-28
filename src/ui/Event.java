package ui;

public interface Event
{
  void accept(EventProcessor processor);
}
