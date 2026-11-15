# Vallo Squalo Simulator

Il gioco e l'app Android che lo contiene.

- **`gioco/`** — il gioco: `index.html` (regole, testi e stile; tutti i valori
  modificabili stanno nel blocco `CONFIG`), `art/` (immagini e musica),
  `fonts/` (caratteri). È la stessa cartella che si pubblica come sito.
- **`android/`** — l'app Android. Non contiene una copia del gioco: quando si
  compila, prende i file da `gioco/` e li mette dentro l'APK, che così
  funziona senza internet.

## Modificare il gioco

Si cambia quello che serve dentro `gioco/` e si ricompila l'APK. Per
aggiungere un seme, un cliente o un poliziotto basta la riga in `CONFIG` e
l'immagine in `gioco/art/` con il nome indicato nei commenti.

## Compilare l'APK

Servono Java 17 o più recente, Gradle 8.13+ e l'Android SDK (piattaforma 35,
build-tools 35.0.0), con `ANDROID_HOME` impostata.

```
cd android
gradle assembleRelease
```

L'APK esce in `android/app/build/outputs/apk/release/app-release.apk`.

A ogni nuova versione vanno aumentati `versionCode` e `versionName` in
`android/app/build.gradle`.

## Chiave di firma

L'APK va firmato sempre con la stessa chiave, altrimenti il telefono rifiuta
l'aggiornamento sopra la versione già installata. La chiave **non sta nel
repository**: chi compila mette in `android/` i due file `vallo-squalo.jks` e
`keystore.properties`, ricevuti in privato.
