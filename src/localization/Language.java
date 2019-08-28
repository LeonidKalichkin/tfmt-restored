package localization;

public enum Language
{
  RU("Russian", "ru"), EN("English", "en");

  private final String name;
  private final String code;

  private Language(String name, String code)
  {
    this.name = name;
    this.code = code;
  }

  public String getCode()
  {
    return code;
  }

  public String getName()
  {
    return name;
  }
}
