package com.yuphilip.android.bookdex.controller.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.yuphilip.android.bookdex.R;
import com.yuphilip.android.bookdex.model.Book;

import org.parceler.Parcels;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BookDetailActivity extends AppCompatActivity {

    //region Properties

    private ImageView ivBookCover;
    private TextView tvTitle;
    private TextView tvAuthor;
    private TextView tvPublisher;
    private TextView tvPublished;
    private ShareActionProvider miShareAction;
    private Intent shareIntent;

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_book_detail);

        // Fetch views
        ivBookCover = findViewById(R.id.ivBookCover);
        tvTitle = findViewById(R.id.tvTitle);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvPublisher = findViewById(R.id.tvPublisher);
        tvPublished = findViewById(R.id.tvPublished);

        // Extract book object from intent extras
        Book clickedBook = Parcels.unwrap(getIntent().getParcelableExtra("clickedBook"));

        // Use book object to populate data into views
        tvTitle.setText(clickedBook.getTitle());
        tvAuthor.setText(clickedBook.getAuthor());
        tvPublisher.setText("Published by " + clickedBook.getPublisher());
        tvPublished.setText(clickedBook.getPublishedDate());

        Glide.with(this)
                .load(Uri.parse(clickedBook.getCoverUrl()))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        prepareShareIntent();
                        attachShareIntentAction();
                        // Let Glide handle resource load
                        return false;
                    }
                })
                .placeholder(R.drawable.ic_nocover)
                .into(ivBookCover);

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = findViewById(R.id.toolbar);

        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        toolbar.setTitle(clickedBook.getTitle());
        setSupportActionBar(toolbar);

    }

    // Returns the URI path to the Bitmap displayed in specified ImageView
    public Uri getLocalBitmapUri(ImageView imageView) {

        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;

        if (drawable instanceof BitmapDrawable) {
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }

        // Store image to default external storage directory
        Uri bmpUri = null;

        try {
            // Use methods on Context to access package-specific directories on external storage.
            // This way, you don't need to request external read/write permission.
            // See https://youtu.be/5xVh-7ywKpE?t=25m25s
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            // **Warning:** This will fail for API >= 24, use a FileProvider as shown below instead.
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bmpUri;

    }

    // Gets the image URI and setup the associated share intent to hook into the provider
    public void prepareShareIntent() {

        ImageView ivImage = findViewById(R.id.ivBookCover);
        // Fetch Bitmap Uri locally
        Uri bmpUri = getLocalBitmapUri(ivImage);// see previous remote images section and notes for API > 23
        // Construct share intent as described above based on bitmap
        shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, tvTitle.getText());
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        shareIntent.setType("image/*");

    }

    // Attaches the share intent to the share menu item provider
    public void attachShareIntentAction() {

        if (miShareAction != null && shareIntent != null)
            miShareAction.setShareIntent(shareIntent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_book_detail, menu);
        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);
        // Fetch reference to the share action provider
        miShareAction = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        attachShareIntentAction(); // call here in case this method fires second

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

}