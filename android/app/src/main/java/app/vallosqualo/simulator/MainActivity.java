package app.vallosqualo.simulator;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;

/**
 * Mostra il gioco che sta dentro l'APK (cartella gioco/ del repository): non serve internet.
 * I file vengono dati alla finestra web sotto un indirizzo interno "https", perche' aperti come
 * semplici file il browser ne blocca alcuni (le maschere delle immagini).
 */
public class MainActivity extends Activity {
    private static final String HOST = "appassets.androidplatform.net"; // riservato a quest'uso, non esiste in rete
    private static final String CASA = "https://" + HOST + "/index.html";

    private WebView web;

    @Override
    protected void onCreate(Bundle stato) {
        super.onCreate(stato);
        web = new WebView(this);
        web.setBackgroundColor(0xFF0E1A12);
        web.setOverScrollMode(View.OVER_SCROLL_NEVER);
        setContentView(web);

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true); // i salvataggi del gioco (localStorage)
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setSupportZoom(false);
        s.setTextZoom(100);

        web.setWebChromeClient(new WebChromeClient());
        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r) {
                Uri u = r.getUrl();
                if (HOST.equals(u.getHost())) return false;
                // Un indirizzo esterno si apre nel browser del telefono.
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, u));
                } catch (Exception ignored) {
                }
                return true;
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView v, WebResourceRequest r) {
                Uri u = r.getUrl();
                if (!HOST.equals(u.getHost())) return null;
                String file = u.getPath() == null ? "" : u.getPath().replaceFirst("^/+", "");
                if (file.isEmpty()) file = "index.html";
                // sw.js serve solo alla versione sito (memoria per l'uso senza rete): qui e' inutile.
                if (!file.equals("sw.js") && !file.contains("..")) {
                    try {
                        InputStream in = getAssets().open(file, AssetManager.ACCESS_STREAMING);
                        return new WebResourceResponse(tipo(file), tipo(file).startsWith("text/") ? "utf-8" : null, in);
                    } catch (IOException ignored) {
                    }
                }
                return new WebResourceResponse("text/plain", "utf-8", 404, "Not Found",
                        new HashMap<String, String>(), new ByteArrayInputStream(new byte[0]));
            }
        });

        if (stato == null || web.restoreState(stato) == null) web.loadUrl(CASA);
    }

    private static String tipo(String file) {
        String f = file.toLowerCase(Locale.ROOT);
        if (f.endsWith(".html")) return "text/html";
        if (f.endsWith(".css")) return "text/css";
        if (f.endsWith(".js")) return "text/javascript";
        if (f.endsWith(".json")) return "application/json";
        if (f.endsWith(".png")) return "image/png";
        if (f.endsWith(".jpg") || f.endsWith(".jpeg")) return "image/jpeg";
        if (f.endsWith(".webp")) return "image/webp";
        if (f.endsWith(".gif")) return "image/gif";
        if (f.endsWith(".svg")) return "image/svg+xml";
        if (f.endsWith(".mp3")) return "audio/mpeg";
        if (f.endsWith(".ogg")) return "audio/ogg";
        if (f.endsWith(".wav")) return "audio/wav";
        if (f.endsWith(".woff2")) return "font/woff2";
        return "application/octet-stream";
    }

    @Override
    protected void onSaveInstanceState(Bundle stato) {
        super.onSaveInstanceState(stato);
        web.saveState(stato);
    }

    @Override
    protected void onPause() {
        web.onPause(); // ferma musica e tempo di gioco quando l'app va in secondo piano
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        web.onResume();
    }

    /** Il tasto indietro chiude prima finestre e menu del gioco (o mette in pausa); solo se non c'e' niente da chiudere esce. */
    @Override
    public void onBackPressed() {
        web.evaluateJavascript("(function(){try{return !!(window.VSS_back&&window.VSS_back());}catch(e){return false;}})()",
                risposta -> { if (!"true".equals(risposta)) moveTaskToBack(true); });
    }

    @Override
    protected void onDestroy() {
        web.destroy();
        super.onDestroy();
    }
}
