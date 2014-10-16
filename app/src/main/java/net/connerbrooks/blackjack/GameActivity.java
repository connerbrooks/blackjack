package net.connerbrooks.blackjack;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import net.connerbrooks.blackjack.models.Card;
import net.connerbrooks.blackjack.models.Player;

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

    boolean gameOver = false;

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
                game.hit();
                if(game.score(game.getCurrentPlayer()) > 21)
                    game.nextPlayer();

                if(game.getCurrentPlayer().getName().equals("Dealer")) {
                    dealerAI();
                }
                waitingForInput = false;
            }
        });

        stay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waitingForInput = false;
                game.nextPlayer();

                if(game.getCurrentPlayer().getName().equals("Dealer")){
                    dealerAI();
                }
            }
        });
    }

    void dealerAI() {
        Player dealer = game.getCurrentPlayer();
        //gameOver = true;
        game.isHoleFlipped = false;
        while (game.score(dealer) < 17)
            game.hit();

        waitingForInput = false;
    }

    public void resetGame() {
        game.newGame();
        //gameOver = false;
        waitingForInput = false;
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
        if (id == R.id.action_reset) {
            resetGame();
        }
        return super.onOptionsItemSelected(item);
    }

    boolean waitingForInput = false;


    @Override
    public void run() {
        while(locker){
            //checks if the lockCanvas() method will be success,and if not, will check this statement again
            if(!holder.getSurface().isValid())
                continue;
            if(gameOver)
                resetGame();
            if(waitingForInput)
                continue;

//                textView.setText(game.getCurrentPlayer().getName() + "'s turn!");
            Canvas canvas = holder.lockCanvas();
            draw(canvas);
            holder.unlockCanvasAndPost(canvas);
            waitingForInput = true;
        }
    }


    int cardCount = 0;

    private void draw(Canvas canvas) {
        // paint a background color
        //canvas.drawColor(android.R.color.holo_blue_bright);
        canvas.drawColor(Color.rgb(0, 135, 0));

        for( int i=0; i<game.getPlayers().size(); i++ )
        {
            ArrayList<Card> hand = game.getPlayers().get(i).getHand();

            Paint paint = new Paint();
            paint.setColor(Color.WHITE);

            paint.setTextSize(30);
            int score = game.score(game.getPlayers().get(i));

            if(score > 21) {
                canvas.drawText("BUST", 20, 200 + i * 400, paint);
            }

            canvas.drawText(game.getPlayers().get(i).getName() + ": " +  score, 20, 100 + i * 400, paint);
            //canvas.drawText("Bet" + game.getPlayers().get(i).getChips(), 20, 100 + i * 400, paint);

            canvas.drawText("Count: " + game.cardCounting, 800, 100, paint);

            if(game.getPlayers().get(i).equals(game.getCurrentPlayer())) {
                canvas.drawText("Turn -> ", 20, 150 + i * 400, paint);
            }

            for( int j=0; j < hand.size(); j++ )
            {
                Card c = hand.get(j);


                if( i == 0 && j == 0 && game.isHoleFlipped)
                {
                    canvas.drawBitmap(mCardBack, 400 + j * 50, 100 + i * 400, null );
                }
                else
                {
                    canvas.drawBitmap(cardImages[c.getCardIndex()], 400 + j * 50, 100 + i * 400, null );
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
        game = null;
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
        game = new Game();
        thread.start();
        waitingForInput = false;
    }

}
