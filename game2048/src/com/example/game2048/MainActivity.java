package com.example.game2048;

import com.example.game2048.R;
import com.example.game2048.view.*;
import com.example.game2048.view.Game2048Layout.OnGame2048Listener;
import com.example.game2048.MainActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.view.View;

//public class MainActivity extends ActionBarActivity {
public class MainActivity extends Activity implements OnGame2048Listener{
		private Game2048Layout mGame2048Layout;
		private static final int EXIT=1;
		private static final int RESTART=2;
		private TextView mScore;
		

		@Override
		protected void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_main);

			mScore = (TextView) findViewById(R.id.id_score);
			mGame2048Layout = (Game2048Layout) findViewById(R.id.id_game2048);
			mGame2048Layout.setOnGame2048Listener(this);
		}

		@Override
		public void onScoreChange(int score)
		{
			mScore.setText("Score: " + score);
		}

		@Override
		public void onGameOver()
		{
			new AlertDialog.Builder(this).setTitle("GAME OVER")
			.setMessage("YOU HAVE GOT " + mScore.getText())
			.setPositiveButton("RESTART", new OnClickListener()
			{
				@Override
					public void onClick(DialogInterface dialog, int which)
					{
						mGame2048Layout.restart();
					}
				}).setNegativeButton("EXIT", new OnClickListener()
			{

					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						finish();
					}
			}).show();
		}
		public boolean onCreateOptionsMenu(Menu menu) {
			menu.add(1, EXIT, 1, "Exit");
			menu.add(2, RESTART, 2, "Restart");
			return true;
		}
		public boolean onOptionsItemSelected(MenuItem item){
		if (item.getItemId()==EXIT){
			showAlertDialog();
		}
		else if (item.getItemId()==RESTART){
			Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
		}
			return super.onOptionsItemSelected(item);
		}
	
		private void showAlertDialog() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Exit?");
		builder.setMessage("Really Want to Exit?");
		builder.setPositiveButton("Yes", new OnClickListener(){
			public void onClick(DialogInterface dialog,int which){
				MainActivity.this.finish();
			}
		});
		builder.setNegativeButton("No", new OnClickListener(){
				public void onClick(DialogInterface dialog,int which){
					dialog.cancel();
				}
			});
			AlertDialog alert=builder.create();
			alert.show();
		}

}
