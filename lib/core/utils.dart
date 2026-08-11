class Utils {
  static String formatSizeLable(int bytes) {
    final labs = ['bytes', 'KB', 'MB', 'GB', 'TB'];
    int i = 0;
    double size = bytes.toDouble();
    while (size >= 1024 && i < 4) {
      size /= 1024;
      i++;
    }
    return '${size.toStringAsFixed(2)} ${labs[i]}';
  }
}
