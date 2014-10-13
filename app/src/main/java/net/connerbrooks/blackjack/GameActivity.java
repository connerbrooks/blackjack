package net.connerbrooks.blackjack;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

    TextView textView;
    TextView dealerText;
    TextView playerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        textView  = (TextView) findViewById(R.id.winnerText);
        dealerText = (TextView) findViewById(R.id.dealerText);
        playerText = (TextView) findViewById(R.id.playerText);

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
                Player curr = game.getCurrentPlayer();
                game.hit();
                if(game.score(curr) > 21) {
                    textView.setText(curr.getName() + " busts!");
                    dealerAI();
                }

                waitingForInput = false;
            }
        });

        stay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waitingForInput = false;
                game.isHoleFlipped = false;
                game.nextPlayer();
                dealerAI();
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

    void dealerAI() {
        Player dealer = game.getCurrentPlayer();
        game.isHoleFlipped = false;
        while (game.score(dealer) < 17)
            game.hit();
        //stay
        // compare players and dealer scores
        Player winner = game.scoreHands();
        if(winner != null) {
            textView.setText(winner.getName() + "Wins!");
        }
        int playerScore = game.score(game.getPlayers().get(1));
        int dealerScore = game.score(game.getPlayers().get(0));

        playerText.setText("Player: " + playerScore);
        dealerText.setText("Dealer: " + dealerScore);
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

    public void resetGame() {
        game.newGame();
        waitingForInput = false;

        textView.setText("New Game!");
        playerText.setText("Player 1");
        dealerText.setText("Dealer");
    }

    @Override
    public void run() {
        while(locker){
            //checks if the lockCanvas() method will be success,and if not, will check this statement again
            if(!holder.getSurface().isValid())
                continue;
            if(waitingForInput)
                continue;

//                textView.setText(game.getCurrentPlayer().getName() + "'s turn!");
            Canvas canvas = holder.lockCanvas();
            draw(canvas);
            holder.unlockCanvasAndPost(canvas);
            waitingForInput = true;
        }
    }

    private void draw(Canvas canvas) {
        // paint a background color
        canvas.drawColor(android.R.color.holo_blue_bright);


        canvas.drawColor(Color.rgb(0, 135, 0));

        for( int i=0; i<game.getPlayers().size(); i++ )
        {
            ArrayList<Card> hand = game.getPlayers().get(i).getHand();
            for( int j=0; j < hand.size(); j++ )
            {
                Card c = hand.get(j);

                if( i == 0 && j == 0 && game.isHoleFlipped)
                {
                    canvas.drawBitmap(mCardBack, 400 + j * 50, 100 + i * 400, null );
                }
                else
                {
                    Log.i("Card number", "" + c.getCardIndex());
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
