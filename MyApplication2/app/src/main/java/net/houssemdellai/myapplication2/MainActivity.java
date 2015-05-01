package net.houssemdellai.myapplication2;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TableLayout;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import android.widget.TextView;


public class MainActivity extends Activity implements ActionBar.TabListener {

    private PodcastsBaseAdapter podcastsBaseAdapter;

    List<XmlParser.Podcast> podcasts;

    MediaPlayer mediaPlayer = new MediaPlayer();
    private ImageButton playImageButton;
    private ImageButton pauseImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_layout);

        setTitleColor(Color.BLUE);

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

//        this.setTitleColor(R.drawable.background_selector);

        final ActionBar ab = getActionBar();

        // set defaults for logo & home up
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayUseLogoEnabled(true);

        // set up tabs nav
        for (int i = 1; i < 4; i++) {
            ab.addTab(ab.newTab().setText("Tab " + i).setTabListener(this));
        }

        // default to tab navigation
        //showTabsNav();

        podcastsBaseAdapter = new PodcastsBaseAdapter(new ArrayList<XmlParser.Podcast>(), this);

        SetupEventHandlers();

        AsyncTask<String, String, String> execute = new XmlDownloader(this).execute("");
    }

    private void SetupEventHandlers() {

        final ListView podcastsListView = (ListView) findViewById(R.id.podcastsListView);

        podcastsListView.setAdapter(podcastsBaseAdapter);

        podcastsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (podcasts == null) return;

                String url = podcasts.get(i).podcastUrl; // your URL here

                LinearLayout playerBoard = (LinearLayout) findViewById(R.id.playerBoard);
                playerBoard.setVisibility(View.VISIBLE);
//                podcastsListView. // android:layout_marginBottom="60dp"

                TextView titleTextView = (TextView) findViewById(R.id.titleTextView);
                titleTextView.setText(podcasts.get(i).description);

                try {

                    mediaPlayer.setDataSource(url);
                    mediaPlayer.prepare(); // might take long! (for buffering, etc)
                    mediaPlayer.start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        playImageButton = (ImageButton) findViewById(R.id.playImageButton);

        playImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mediaPlayer.start();

                pauseImageButton.setVisibility(View.VISIBLE);
                playImageButton.setVisibility(View.GONE);
            }
        });

        pauseImageButton = (ImageButton) findViewById(R.id.pauseImageButton);

        pauseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mediaPlayer.pause();

                pauseImageButton.setVisibility(View.GONE);
                playImageButton.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showTabsNav() {
        ActionBar ab = getActionBar();
        if (ab.getNavigationMode() != ActionBar.NAVIGATION_MODE_TABS) {
            ab.setDisplayShowTitleEnabled(false);
            ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);

        menu.add(1, 1, 0, "Item1");
        menu.add(1, 2, 1, "Item2");
        menu.add(1, 3, 2, "Item3");
        menu.add(1, 4, 3, "Item4");
        menu.add(1, 5, 4, "Item5");

        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {



        return true;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }


    public class XmlDownloader extends AsyncTask<String, String, String> {

        public String xml;
        private MainActivity myActivity;
        PodcastsBaseAdapter mAdapter;
        ProgressBar progressBar;

        public XmlDownloader(MainActivity myActivity) {

            this.myActivity = myActivity;
        }

        @Override
        protected void onPreExecute() {

            // Create a progress bar to display while the list loads
            progressBar = new ProgressBar(myActivity);
            progressBar.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                    TableLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
            progressBar.setIndeterminate(true);
//            getListView().setEmptyView(progressBar);

            // Must add the progress bar to the root of the layout
            ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
            root.addView(progressBar);
        }

        @Override
        protected String doInBackground(String... strings) {
            //xml = getXmlFromUrl("http://www.mosaiquefm.net/smart/podcast.xml?Cat=64");

            xml =   "<podcasts>\n" +
                    "<podcast id=\"102478\" channel=\"\">\n" +
                    "<title><![CDATA[Seyeskhouk]]></title>\n" +
                    "<description><![CDATA[الزادات في الأسعار: الباجي لازموMise à Jour]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYESKHOUK-050914[1].mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"102301\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[سايس خوك...زابينغ]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYES KHOUK ZAPPING 030914.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"102142\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[كاميرا الحق معاك في اعتصام الكيافة ]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/MOSAIQUEFM-SEYESSKHOUK-010914.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"102477\" channel=\"\">\n" +
                    "<title><![CDATA[Seyeskhouk]]></title>\n" +
                    "<description><![CDATA[إعتصام الكيافة قدام دار عبد الباقي نيكوتين ]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYES-KHOUK-010914[1].mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"101885\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[بهتة ومنظمة الدفاع عن المستهلك ]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/MOSAIQUEFM-SEYESSKHOUK-280814.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"101824\" channel=\"\">\n" +
                    "<title><![CDATA[Seyes Khouk]]></title>\n" +
                    "<description><![CDATA[هزان الفرش من ''البوسطة'']]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYESKHOUK-270714.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"101465\" channel=\"\">\n" +
                    "<title><![CDATA[Seyeskhouk]]></title>\n" +
                    "<description><![CDATA[جورج وسوف يكتشف السياسة الجديدة لمهرجان قرطاج]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYS-KHOUK-220814[1].mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"101381\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[كاميرا الحق معاك في المجلس التأسيسي]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/MOSAIQUEFM-SEYESSKHOUK-210814.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"101065\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[Homoscope مع الحبيب ميقالو ]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/MOSAIQUEFM-SEYESSKHOUK-150814.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"100947\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[جماعة الحي اللوطاني يحتجو على الناموس ]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/MOSAIQUEFM-SEYESSKHOUK-14H-140814.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"100878\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[السخانة عاملة في ميغالو]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYES KHOUK -130814 R.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"100800\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[اكتشف حظك مع الحبيب ميغالو]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYES KHOUK 120814 HOMOSCOPE -rEDIFF.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"100741\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[Belikass]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYESKHOUK-110814.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"100614\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[جاكلين مرت كريم خايفة تجي لتونس]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/MOSAIQUEFM-SEYESSKHOUK-080814.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"100538\" channel=\"\">\n" +
                    "<title><![CDATA[Seyes khouk]]></title>\n" +
                    "<description><![CDATA[Samira Awtar parle de son exclusion par l'ancien régime]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/MOSAIQUEFM-SEYESSKHOUK-070814.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"100472\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[عندي حزيّب يا محلاه...يا محلاه سميتو حزب النداء]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/MOSAIQUEFM-SEYESSKHOUK-060814.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"100427\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[أحدث بورطابل في سوق ليبيا ]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/MOSAIQUEFM-SEYESSKHOUK-050814.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"100362\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[التبحيرة وين ؟]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYESKHOUK-040814.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"100205\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[توجيه ولاد الحي الوطاني ]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/seyes-khouk-orientation-0180814[1].mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"100066\" channel=\"\">\n" +
                    "<title><![CDATA[Seyes Khouk]]></title>\n" +
                    "<description><![CDATA[La Fête de la République]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYES-300714.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"99819\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[La fête de la République ]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/seyes-khouk-250714[1].mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"99661\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[ميغالو ودليلك ملك]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYES KHOUK 230714 DLILEK MLAK VS KANAWITA.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"99582\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[السبسي يعرف جدّ ياني كان اسمو فرجاني]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/MOSAIQUEFM-SEYESSKHOUK-220714.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"99489\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[Hmed Erriadhi: Mercato intensif pour Slim Riahi ]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYESKHOUK-210714.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"99086\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[مسلسل ''ستيكتي لعزيزة'']]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYES KHOUK 160714 STIKTI LA3ZIZA.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"99016\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[المرزوقي والسبسي والغنوشي وتبحيرة رمضان  ]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/MOSAIQUEFM-SEYESSKHOUK-150714.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"98917\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[Hmed Erriadhi: Retour aux sources après le Mondial]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYESKHOUK-140714.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"98697\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[البحري الجلاصي بش يولي رئيس ]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYESSKHOUK-110714[1].mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"98621\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[ميغالو وجماعتو يحكيوو على مهرجان قرطاج ]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYES-KHOUK-CARTHAGE.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"98568\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[الألمان طلعو أزلامن]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYESKHOUK-090714.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"98469\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[دربي الأطباق بين السبسي والغنوشي]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYS KHOUK DERBY LES PLATS 080714.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"98356\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[Hmed Erriadhi: Merci pour le timbre du mariage ]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYESKHOUK-070714.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"98200\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[اللحم طلع فاسد ]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYES-KHOUK-040714[1].mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"98009\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[اعلان ضياع بطاقة تعريف المرزوقي ]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYES KHOUK 020714.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"97938\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[سبيسيال مباراة الجزائر وألمانيا ]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/MOSAIQUEFM-SEYESSKHOUK-010714.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"97856\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[Hmed Erriadhi spécial programmes TV du début de Ramadan ]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYESKHOUK-300614.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"97624\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[المخّ يفرّك فيهم على غنايات نور شيبة]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/MOSAIQUEFM-SEYESSKHOUK-270614.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"97524\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[مسلسل رمضان للبيع  ... شكون يشري ؟]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/MOSAIQUEFM-SEYESSKHOUK-260614.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"97470\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[السخانة في الحي اللوطاني ]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/MOSAIQUEFM-SEYESSKHOUK-250514.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"97383\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[بهتة فرحان بالزيادة 6% في الشهرية أما مازالت ناقصتو الخدمة ]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYES KHOUK ZYEDA 240614 ZYEDA.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"97300\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[Hmed Erriadhi: Bravo aux algériens]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYESKHOUK-230614.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"97118\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[المستراس قبل نتيجة الباكالوريا ]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYES- KHOUK 200614 RES BAC.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"97008\" channel=\"\">\n" +
                    "<title><![CDATA[Seyes khouk]]></title>\n" +
                    "<description><![CDATA[Migalo dévoile le programme du Festival de Carthage]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/MOSAIQUEFM-SEYES-KHOUK-190614.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"96910\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[حفيظ الدراجي متغشش على خسارة الجزائر في المونديال]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/MOSAIQUEFM-SEYESSKHOUK-180614.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"96833\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[توقعات غراب مختار السحار لنتائج المباريات في البرازيل ]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYES KHOUK 170614 MOKHTAR- AU -BRESIL.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"96761\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk ]]></title>\n" +
                    "<description><![CDATA[Hmed Erriadhi spécial &quot;Coupe du Monde&quot;]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYSEKHOUK-160614.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"96509\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[رئيس البلديّة يحسب في الناموس\n" +
                    "]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/MOSAIQUEFM-SEYESSKHOUK-12062014.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"96452\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[بهتة في البرازيل ]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/MOSAIQUEFM-SEYESSKHOUK-110514.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"96389\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[بهتة طيحوه يفسكي في الباك ]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYES KHOUK 100614 BAC 5 ANS.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"96328\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[Hmed Erriadhi: Wadii Jarii avait raison?]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYESKHOUK-090614.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"96193\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[السبسي باش يشجع البرازيل في المونديال والسبب ...]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYES KHOUK COUPE DU MONDE.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"96115\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[حلقة جديدة من برنامج آخ تاك]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/MOSAIQUEFM-SEYESSKHOUK-050614.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"96010\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[سبيسيال باك]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/MOSAIQUEFM-SEYESSKHOUK-040614.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"95930\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[حظك اليوم مع الحبيب ميغالو]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYES KHOUK 030614 HOMOSCOPE.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"95850\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[حمد الرياضي: قطر وكأس العالم]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/MOSAIQUEFM-SEYESSKHOUK-020614.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"95703\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[احمد ابراهيم يهنئ عبد الفتاح السيسي بالفوز في الرئاسة المصرية]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYES- KHOUK- HMED BRAHIM FELICITE SISI.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"95637\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[المرزوقي بلبزها مع البلدان الكلّ ودار على الشعب متاعو]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/MOSAIQUEFM-SEYESSKHOUK-290514.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"95467\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[الجبالي والمرزوقي يحضرو في الباكالوريا  ]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYES KHOUK 270514 BAC.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"95377\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[Hmed Erriadhi: Match historique de l'espérance contre Ahly Benghazi]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYES KHOUK-260514.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"95176\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[الحبيب ميغالو وبية الذيبة في مهرجان كان ]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYES- KHOUK- CANNES 230514.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"95092\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[بهتة يحلّل في الزيادات في الأسعار]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/MOSAIQUEFM-SEYESSKHOUK-220514.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"95001\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[مكالمة بين المرزوقي وحفتر ]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/MOSAIQUEFM-SEYESSKHOUK-210514.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"94924\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[النمس والرتيلة في قلبو ]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYES KHOUK 200514 NEMS- ET- RETILA- FI- GALBOU.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"94866\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[Hmed Erriadhi: Souriez pour le Selfie ]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYESKHOUK-190514.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"94676\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[إنجازات حكومة بو جمعة ]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYES KHOUK 160514 ENJAZAET-HKOUMA.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"94587\" channel=\"\">\n" +
                    "<title><![CDATA[Seyes khouk]]></title>\n" +
                    "<description><![CDATA[La souscription au Hay Loutani]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/MOSAIQUEFM-SEYESSKHOUK-150514.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"94494\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[ Spécial 'بقلاوة ']]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/MOSAIQUEFM-SEYESSKHOUK-140514.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"94401\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[الرايس مح الدين المرشح الأول لتولي وزارة البحر ]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYESKHOUK-130514.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"94302\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess khouk]]></title>\n" +
                    "<description><![CDATA[Hmed Erriadhi: L'ESS fête ses 89 ans ]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/seyeskhouk-120514.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"94082\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[بهتة ربح مليار في البرومسبور ]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYES-khouk-090514.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"94001\" channel=\"\">\n" +
                    "<title><![CDATA[Seyes Khouk]]></title>\n" +
                    "<description><![CDATA[Retour sur le discours de Marzouki]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/MOSAIQUEFM-SEYESSKHOUK-080514.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"93921\" channel=\"\">\n" +
                    "<title><![CDATA[Seyes khouk]]></title>\n" +
                    "<description><![CDATA[Hssan Samantha effectue une opération de recensement]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/MOSAIQUEFM-SEYES-KHOUK-070514.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"93868\" channel=\"\">\n" +
                    "<title><![CDATA[Seyes khouk]]></title>\n" +
                    "<description><![CDATA[Un match de boxe entre Ben Gharbia et Gassas]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/MOSAIQUEFM-SEYES-KHOUK-060514.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"93803\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[Hmed Erriadhi spécial EST]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYESKHOUK-050414.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"93622\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[السبسي ينصح في السيسي بمناسبة الانتخابات الرئاسية المصرية]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYES KHOUK 020514 SEBSI ET SISSI.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"93522\" channel=\"\">\n" +
                    "<title><![CDATA[Seyes khouk]]></title>\n" +
                    "<description><![CDATA[Séance ''bélicasses'']]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/MOSAIQUEFM-SEYES-KHOUK-010514.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"93428\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[' selfie'  آمال كربول ]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/MOSAIQUEFM-SEYESSKHOUK-300414.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"93302\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[مختار التليلي يبيع في اشتراكات بهيم سبور]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYES KHOUK BHEIMSPORT 29041420.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"93199\" channel=\"\">\n" +
                    "<title><![CDATA[Seyes Khouk]]></title>\n" +
                    "<description><![CDATA[Hmed Erriadhi: 2 réserves sur le tirage au sort de la CAN?]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYESKHOUK-280414.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"93006\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[نبيل القروي يهدد بإيقاف يث حريم السلطان بسبب كراس شروط الهايكا]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/seyeskhouk-250414.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"92886\" channel=\"\">\n" +
                    "<title><![CDATA[Seyes khouk]]></title>\n" +
                    "<description><![CDATA[ZDF transmettra la Coupe du Monde gratuitement]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/MOSAIQUEFM-SEYESSKHOUK-240414.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"92801\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[مكالمة هاتفية بين ميقالو وبوتفليقة ]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/MOSAIQUEFM-SEYESSKHOUK-230414.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"92713\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[فوز الحبيب ميغالو في انتخابات نقابة الصحفيين واحتجاج بية الذيبة]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYES KHOUK -MAKTEB-TANFIDHI NAKABA 220414.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"92623\" channel=\"\">\n" +
                    "<title><![CDATA[Seyes Khouk]]></title>\n" +
                    "<description><![CDATA[Hmed Erriadhi: Izikel en vente au négociateur!]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYESKHOUK-210414.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"92336\" channel=\"\">\n" +
                    "<title><![CDATA[Seyes khouk]]></title>\n" +
                    "<description><![CDATA[Baya Dhiba cambriolée]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/MOSAIQUEFM-SEYESKHOUK-170414.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"92244\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[ مع الحبيب ميقالو 'homoscope' الـ]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/MOSAIQUEFM-SEYESSKHOUK-160414.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"92151\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[جولة حسن سمانتا لطلب المساعدة من الدول الشقيقة والصديقة]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYES KHOUK 150414.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"92023\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[Hmed Erriadhi: Championnat du gaz!]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYESKHOUK-140414.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"91795\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[بيّة الذيبة أنت الـ miss  ما دام المرزوقي شدّ رئيس]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYES KHOUK 110414.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"91709\" channel=\"\">\n" +
                    "<title><![CDATA[Seyes khouk]]></title>\n" +
                    "<description><![CDATA[Akh Tech]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/MOSAIQUEFM-SEYES-KHOUK-100414.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"91587\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[ميقالو مهدد بالاغتيال ]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/MOSAIQUEFM-SEUESSKHOUK-090414.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"91510\" channel=\"\">\n" +
                    "<title><![CDATA[SeyeS Khouk]]></title>\n" +
                    "<description><![CDATA[مهدي جمعة يعود من أمريكا محمّلا بالهدايا]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYESKHOUK-80414.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"91394\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[الحمد الرياضي: المخ ما خلصش الفينيات ]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/MOSAIQUEFM-SEYESSKHOUK-070414.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"91065\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[لقاء المرزوقي بأمير قطر ]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYES KHOUK 040414 3CHE FEL 9SAR.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"90975\" channel=\"\">\n" +
                    "<title><![CDATA[Seyes khouk]]></title>\n" +
                    "<description><![CDATA[Le prince Qatari en visite en Tunisie]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/MOSAIQUEFM-SEYES-KHOUK-030414.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"90878\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[انقلاب عسكري بقيادة حمد ابراهيم واولاد الحي اللوطاني ]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/MOSAIQUEFM-SEYESSKHOUK-020414.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"90781\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[A chacun son poisson d'Avril]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/MOSAIQUEFM-SEYESKHOUK-010414.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"90732\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[حمد الرياضي]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/MOSAIQUEFM-SEYESSKHOUK-310314.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"90474\" channel=\"\">\n" +
                    "<title><![CDATA[Seyess Khouk]]></title>\n" +
                    "<description><![CDATA[تصوروا أحمد نجيب الشابي الرئيس المقبل ويطلب منا نحجموا كيفو]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYES KHOUK -COUPE -CHEVEUX.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "<podcast id=\"90353\" channel=\"\">\n" +
                    "<title><![CDATA[Seyes Khouk]]></title>\n" +
                    "<description><![CDATA[بجبوج يخاف مالسحر .. ما ياكل كان في دارو]]></description>\n" +
                    "<url><![CDATA[http://www.mosaiquefm.net/assets/content/mp3/SEYESKHOUK-270314.mp3?Source=SMART]]></url>\n" +
                    "<!--<thumbnail><![CDATA[VIGNETTE DU PODCAST]]></thumbnail>-->\n" +
                    "</podcast>\n" +
                    "</podcasts>";

            return xml;
        }

        public String getXmlFromUrl(String url) {
            String xml = null;

            try {
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);

                HttpUriRequest httpUriRequest = new HttpGet(url);

                HttpResponse httpResponse = httpClient.execute(httpUriRequest);
                HttpEntity httpEntity = httpResponse.getEntity();
                xml = EntityUtils.toString(httpEntity);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();

            }
            // return XML
            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {

            super.onPostExecute(xml);

            InputStream stream = new ByteArrayInputStream(xml.getBytes());//xml.getBytes(StandardCharsets.UTF_8)

            XmlParser xmlParser = new XmlParser();

            try {

                podcasts = xmlParser.parse(stream);

                podcastsBaseAdapter.addAll(podcasts);

                podcastsBaseAdapter.notifyDataSetChanged();

                progressBar.setVisibility(View.GONE);

//                podcastsBaseAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//
//                    }
//                });

//                displayData(podcasts);

            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void displayData(List<XmlParser.Podcast> podcasts) {

            /* // Create a progress bar to display while the list loads
            ProgressBar progressBar = new ProgressBar(this);
            progressBar.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                    TableLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
            progressBar.setIndeterminate(true);
            getListView().setEmptyView(progressBar);

            // Must add the progress bar to the root of the layout
            ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
            root.addView(progressBar);
            */

            ArrayList<XmlParser.Podcast> list = new ArrayList<XmlParser.Podcast>();
            for (int i = 0; i < podcasts.size(); ++i) {
                list.add(podcasts.get(i));
            }

            ListView podcastsListView = (ListView) findViewById(R.id.podcastsListView);

            mAdapter = new PodcastsBaseAdapter(list, MainActivity.this);

            podcastsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

//                    myActivity.getme
//                    Menu menu = (Menu) findViewById(R.menu.my);
                }
            });

            podcastsListView.setAdapter(mAdapter);
        }
    }
}
