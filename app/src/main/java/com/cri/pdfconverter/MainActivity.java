package com.cri.pdfconverter;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import android.os.Bundle;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
	WebView webView;
	ProgressBar progressBar;
	String CriUrl = "https://www.github.com/kuttahaitu";
	SearchView searchView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		webView = findViewById(R.id.webview);
		progressBar = findViewById(R.id.pb);
		webView.loadUrl(CriUrl);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap fav) {
				progressBar.setVisibility(View.VISIBLE);
				super.onPageStarted(view, url, fav);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				progressBar.setVisibility(View.GONE);
				searchView.setQuery(url, false);
				super.onPageFinished(view, url);
			}
		});
	}

	@Override
	public void onBackPressed() {
		if (webView.canGoBack()) {
			webView.goBack();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu arg0) {
		getMenuInflater().inflate(R.menu.main, arg0);
		arg0.add(0, 1, 0, "Convert");
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		searchView = (SearchView) arg0.findItem(R.id.search).getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		searchView.setQueryHint("Search or Paste link here");
		searchView.setIconified(true);
		searchView.setIconifiedByDefault(true);
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				if (query.contains("http")) {
					searchView.clearFocus();
					webView.loadUrl(query);
				} else if (query.contains(".")) {
					searchView.clearFocus();
					webView.loadUrl("https://www." + query);
				} else {
					String url = "https://www.google.com/search?q=" + query;
					webView.loadUrl(url);
					searchView.clearFocus();
				}
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {

				return true;
			}
		});
		return super.onCreateOptionsMenu(arg0);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem arg0) {
		if (arg0.getItemId() == 1) {
			printPDF();
		}
		return super.onOptionsItemSelected(arg0);
	}

	public String fileName(String url) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault());
		String currentTime = sdf.format(new Date());
		String domain = Objects.requireNonNull(Uri.parse(url).getHost()).replace("www.", "").trim();
		return domain.replace(".", "_").trim() + "_" + currentTime.trim();
	}

	private void printPDF() {
		String title = fileName(webView.getUrl());
		PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
		PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(title);
		Objects.requireNonNull(printManager).print(title, printAdapter, new PrintAttributes.Builder().build());
	}

}