package net.connerbrooks.blackjack;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import net.connerbrooks.blackjack.models.Card;

import java.io.IOException;
import java.util.ArrayList;


public class GameActivity extends Activity implements Runnable{
    private Button hit;
    private Button stay;
    private SurfaceView surface;
    private SurfaceHolder holder;
    private Thread thread;
    private boolean locker=true;
    Game game;


    Bitmap[] cardImages;
    Bitmap mCardBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        game = new Game();

        loadBitmaps();

        hit = (Button) findViewById(R.id.hit);
        stay = (Button) findViewById(R.id.stay);

        surface = (SurfaceView) findViewById(R.id.gameview);
        holder = surface.getHolder();

        thread = new Thread(this);
        thread.start();



        hit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.hit(game.getCurrentPlayer());
                waitingForInput = false;
            }
        });
    }

    public void loadBitmaps() {
        mCardBack = BitmapFactory.decodeResource(getResources(), R.drawable.cardback);
        AssetManager assetManager = this.getAssets();

        cardImages = new Bitmap[53];

        for(int i = 0; i < 53; i++) {
            Bitmap bitmap = null;
            try {
                String fileName = "standard" + i + ".png";
                bitmap = BitmapFactory.decodeStream(assetManager.open(fileName));
            } catch (IOException e) {
                // handle exception
            }
            cardImages[i] = Bitmap.createScaledBitmap(bitmap, 138, 186, false);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game, menu);
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

    boolean waitingForInput = false;


    @Override
    public void run() {
        // TODO Auto-generated method stub
        while(locker){
            //checks if the lockCanvas() method will be success,and if not, will check this statement again
            if(!holder.getSurface().isValid()){
                continue;
            }

            if(waitingForInput){
                continue;
            }

            /** Start editing pixels in this surface.*/
            Canvas canvas = holder.lockCanvas();

            //ALL PAINT-JOB MAKE IN draw(canvas); method.
            draw(canvas);

            // End of painting to canvas. system will paint with this canvas,to the surface.
            holder.unlockCanvasAndPost(canvas);
            waitingForInput = true;
        }
    }

    private void draw(Canvas canvas) {
        // paint a background color
        canvas.drawColor(android.R.color.holo_blue_bright);

        // paint a rectangular shape that fill the surface.
        int border = 20;
        RectF r = new RectF(border, border, canvas.getWidth()-20, canvas.getHeight()-20);
        Paint paint = new Paint();
        paint.setARGB(200, 135, 135, 135); //paint color GRAY+SEMY TRANSPARENT
        canvas.drawRect(r , paint );


        for( int i=0; i<game.getPlayers().size(); i++ )
        {
            ArrayList<Card> hand = game.getPlayers().get(i).getHand();
            for( int j=0; j < hand.size(); j++ )
            {
                Card c = hand.get(j);

                if( i == 0 && j == 0 && !game.isHoleFlipped)
                {
                    canvas.drawBitmap(mCardBack, 400 + j * 50, 100 + i * 400, null );
                }
                else
                {
                    Log.i("Card number", "" + c.getCardNumber());
                    canvas.drawBitmap(cardImages[c.getCardNumber()], 400 + j * 50, 100 + i * 400, null );
                }
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        pause();
    }

    private void pause() {
        //CLOSE LOCKER FOR run();
        locker = false;
        while(true){
            try {
                //WAIT UNTIL THREAD DIE, THEN EXIT WHILE LOOP AND RELEASE a thread
                thread.join();
            } catch (InterruptedException e) {e.printStackTrace();
            }
            break;
        }
        thread = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        resume();
    }

    private void resume() {
        //RESTART THREAD AND OPEN LOCKER FOR run();
        locker = true;
        thread = new Thread(this);
        thread.start();
    }

}
