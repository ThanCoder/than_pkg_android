class LocaleInfo {
  final String language;
  final String country;
  final String locale;
  final String displayLanguage;
  final String displayCountry;
  const LocaleInfo({
    required this.language,
    required this.country,
    required this.locale,
    required this.displayLanguage,
    required this.displayCountry,
  });

  factory LocaleInfo.fromMap(Map<String, dynamic> map) {
    return LocaleInfo(
      language: map['language'] ?? '',
      country: map['country'] ?? '',
      locale: map['locale'] ?? '',
      displayLanguage: map['displayLanguage'] ?? '',
      displayCountry: map['displayCountry'] ?? '',
    );
  }

  @override
  String toString() {
    return 'LocaleInfo(language: $language, country: $country, locale: $locale, displayLanguage: $displayLanguage, displayCountry: $displayCountry)';
  }
}
